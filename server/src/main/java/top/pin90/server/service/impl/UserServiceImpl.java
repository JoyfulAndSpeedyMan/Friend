package top.pin90.server.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import top.pin90.common.exception.auth.UserVerifyException;
import top.pin90.common.pojo.Code;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.common.unti.*;
import top.pin90.server.config.user.UserLoginCodeConfig;
import top.pin90.server.config.user.UserLoginConfig;
import top.pin90.server.config.user.UserLoginNewConfig;
import top.pin90.server.dao.user.UserFriendSettingRepository;
import top.pin90.server.dao.user.UserRepository;
import top.pin90.common.po.user.FriendReqVerMode;
import top.pin90.common.po.user.User;
import top.pin90.common.po.user.UserFriendSetting;
import top.pin90.common.po.user.UserStatus;
import top.pin90.server.service.UserService;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * @author pin
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    final UserLoginCodeConfig userLoginCodeConfig;
    final UserLoginNewConfig userLoginNewConfig;
    final UserLoginConfig userLoginConfig;

    final private ReactiveStringRedisTemplate template;
    final private SmsUtils smsUtils;
    final private JwtUtils jwtUtils;
    final private UserRepository userRepository;
    final private UserFriendSettingRepository userFriendSettingRepository;
//    final private ConcurrentHashMap<String, CodeCache> codeCacheMap = new ConcurrentHashMap<>(300);


    public UserServiceImpl(UserLoginCodeConfig userLoginCodeConfig, UserLoginNewConfig userLoginNewConfig, UserLoginConfig userLoginConfig, ReactiveStringRedisTemplate template, SmsUtils smsUtils, JwtUtils jwtUtils, UserRepository userRepository, UserFriendSettingRepository userFriendSettingRepository) {
        this.userLoginCodeConfig = userLoginCodeConfig;
        this.userLoginNewConfig = userLoginNewConfig;
        this.userLoginConfig = userLoginConfig;
        this.template = template;
        this.smsUtils = smsUtils;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.userFriendSettingRepository = userFriendSettingRepository;

    }

    /*
        单机code缓存实现

        @Scheduled(cron = "0 #{'0/'+${user.register.code.cleanPeriodRate}} * * * ?")
        public void clearCodeMap() {
            if (codeCacheMap.isEmpty()) {
                log.info("The codeCacheMap is empty,no need to clean up");
                return;
            }
            int beforeSize = codeCacheMap.size();
            final Instant start = Instant.now();
            final Date date = new Date();
            codeCacheMap.forEach((k, v) -> {
                final Date expireDate = v.getExpireDate();
                if (expireDate.before(date))
                    codeCacheMap.remove(k);
            });
            final long until = start.until(Instant.now(), ChronoUnit.MILLIS);
            int afterSize = codeCacheMap.size();
            log.info("Clear codeMap Cache , {} element are cleaned up,before clear cache size: {} , after size : {} , take {} ms", beforeSize - afterSize, beforeSize, afterSize, until);

        }
        @Data
        @AllArgsConstructor
        private static class CodeCache {
            private String code;
            private Date expireDate;
        }

        private CodeCache getCodeAndPut(String type, String phone) {
            String code;
            String codeType = userRegisterCodeConfig.getCodeType();
            final String key = getCacheKey(type, phone);
            // 获取缓存
            CodeCache codeCache = codeCacheMap.get(key);
            if (codeCache == null || codeCache.getExpireDate().before(new Date())) {
                // 生成code
                if (codeType.equals("code6"))
                    code = NumFormat.randCode6();
                else if (codeType.equals("code4"))
                    code = NumFormat.randCode4();
                else
                    code = NumFormat.randCode6();
                // 设置过期时间
                final Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.SECOND, codeValidTime);
                //刷新缓存
                codeCache = new CodeCache(code, calendar.getTime());
                codeCacheMap.put(key, codeCache);
            }
            return codeCache;
        }

        private CodeCache getCodeAndRemove(String type, String phone) {
            final String key = getCacheKey(type, phone);
            return codeCacheMap.remove(key);
        }

        private String getCacheKey(String type, String phone) {
            String key="";
            switch (type) {
                case "register":
    //                key = REGISTER_CACHE_KEY + phone;
                    break;
                case "login":
    //                key = LOGIN_CACHE_KEY + phone;
                    break;
                default:
                    throw new RuntimeException("code type " + type + " error");
            }
            return key;
        }
        */
    @Override
    public Mono<ResponseResult> findAllUser() {
        return userRepository.findAll()
                .collect(Collectors.toList())
                .map(ResponseResult::ok);
    }


    /**
     * 发送登录验证码
     *
     * @param phone
     * @return
     */
    @Override
    public Mono<ResponseResult> sendLoginSmsCode(String phone) {
        return Mono.defer(() -> {
            String key = getLoginCacheKey(phone);
            int validTime = userLoginCodeConfig.getValidTime();
            String codeType = userLoginCodeConfig.getCodeType();
            ReactiveValueOperations<String, String> opsForValue = template.opsForValue();
            String code;
            // 生成code
            if (codeType.equals("code6"))
                code = NumFormat.randCode6();
            else if (codeType.equals("code4"))
                code = NumFormat.randCode4();
            else
                code = NumFormat.randCode6();

            Mono<String> getCodeMono = opsForValue.get(key);
            Mono<Boolean> hasElement = getCodeMono.hasElement();

            Mono<Boolean> setMono = opsForValue.set(key, code, Duration.ofSeconds(validTime));
            Mono<String> codeMono = getCodeMono
                    .defaultIfEmpty(code)
                    .zipWith(hasElement)
                    .zipWhen(tuple2 -> {
                        if (tuple2.getT2())
                            return Mono.just(true);
                        return setMono;
                    })
                    .map(tuple -> tuple.getT1().getT1());


            return Mono.just(phone)
                    .zipWith(codeMono)
                    .map(tuple2 -> {
                        smsUtils.sendLoginSmsCode(phone, tuple2.getT2());
                        return ResponseResult.ok("发送成功");
                    });
        });

    }

    /**
     * 短信验证码登录
     *
     * @param phone
     * @param code
     * @return
     */
    @Override
    public Mono<ResponseResult> smsCodeLogin(String phone, String code) {
        log.trace("smsCodeLogin");
        // 判断验证码是否有效
        Mono<Boolean> codeValid = template.opsForValue()
                .get(getLoginCacheKey(phone))
                .map(s -> s.equals(code));

        return codeValid
                .filter(b -> b)
                .zipWith(template.opsForValue().delete(getLoginCacheKey(phone)))
                .map(Tuple2::getT2)
                // 创建用户
                .flatMap(b -> userRepository
                        .findFirstByPhone(phone)
                        .switchIfEmpty(Mono.defer(() -> {
                            final Date date = new Date();
                            final User user = User.builder()
                                    .phone(phone)
                                    .nickname(phone)
                                    .avatar(userLoginNewConfig.getAvatar())
                                    .status(UserStatus.NORMAL)
                                    .createTime(date)
                                    .updateTime(date)
                                    .build();
                            final Mono<User> save = userRepository.save(user);
                            return save;
                        })))
                // 返回或创建好友验证设置
                .zipWhen(user -> userFriendSettingRepository.findByUserId(user.getId())
                        .switchIfEmpty(Mono.defer(() -> {
                            final UserFriendSetting userFriendSetting = UserFriendSetting.builder()
                                    .userId(user.getId())
                                    .friReqVerMode(FriendReqVerMode.MESSAGE)
                                    .build();
                            return userFriendSettingRepository.save(userFriendSetting);
                        })))
                // 获取用户Id
                .map(tuple2 -> tuple2.getT1().getId())
                // 返回token
                .map(userId -> {
                    // 登录逻辑
                    final String accessToken = jwtUtils.createAccessToken(userId, userLoginConfig.getAccessValidTime());
                    final String refreshToken = jwtUtils.refreshAccessToken(userId, userLoginConfig.getRefreshValidTime());
                    HashMap<String, String> result = new HashMap<>();
                    result.put("accessToken", accessToken);
                    result.put("refreshToken", refreshToken);

                    return ResponseResult.ok("登录成功", result);
                })
                .defaultIfEmpty(ResponseResult.of(Code.SMS_CODE_ERROR, "验证码不正确或已过期"));
    }

    @Override
    public Mono<ResponseResult> refreshToken(String refreshToken) {
        return Mono.defer(() -> {
            DecodedJWT decodedJWT;
            try {
                decodedJWT = jwtUtils.parseToken(refreshToken);

            } catch (UserVerifyException e) {
                return ResponseResult.toMono(Code.PARAM_ERROR, "refreshToken invalid");
            }
            String accessToken = jwtUtils.createAccessToken(jwtUtils.getUserId(decodedJWT), userLoginConfig.getAccessValidTime());
            return ResponseResult.monoOk(accessToken);
        });
    }

    @Override
    public Mono<ResponseResult> getUserBaseInfo(ObjectId userId) {
        final Mono<User> baseInfoById = userRepository.getBaseInfoById(userId);
        return baseInfoById
                .map(MyBeanUtils::beanToMap)
                .map(ResponseResult::ok)
                .switchIfEmpty(Mono.fromSupplier(() -> ResponseResult.of(Code.USER_NOT_EXIST, "用户不存在")));
    }

    @Override
    public Mono<ResponseResult> updateUserInfo(User user) {
        return userRepository.findById(user.getId())
                .flatMap(u->{
                    String[] updateFields={
                            "avatar",
                            "sex",
                            "birthday",
                            "profile"
                    };
                    MyBeanWrap oldInfo  = MyBeanWrap.wrap(u);
                    MyBeanWrap newInfo = MyBeanWrap.wrap(user);
                    for (String name : updateFields) {
                        Object o = newInfo.get(name);
                        if(o!=null)
                            oldInfo.set(name,o);
                    }
                    u.setUpdateTime(new Date());
                    return userRepository.save(u);
                })
                .map(u-> ResponseResult.ok("更新成功",user));
    }


    private String getLoginCacheKey(String phone) {
        return userLoginConfig.getLoginCacheKey() + phone;
    }


}
