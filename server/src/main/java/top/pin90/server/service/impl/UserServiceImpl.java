package top.pin90.server.service.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import top.pin90.common.pojo.Code;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.common.unti.JwtUtils;
import top.pin90.common.unti.MyBeanUtils;
import top.pin90.common.unti.NumFormat;
import top.pin90.common.unti.SmsUtils;
import top.pin90.server.dao.user.UserFriendSettingRepository;
import top.pin90.server.dao.user.UserRepository;
import top.pin90.server.po.user.FriendReqVerMode;
import top.pin90.server.po.user.User;
import top.pin90.server.po.user.UserFriendSetting;
import top.pin90.server.po.user.UserStatus;
import top.pin90.server.service.UserService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Value("${user.register.code.expire}")
    private Integer codeExpire;
    @Value("user.register.code.type")
    private String codeType;
    @Value("${user.register.default.avatar}")
    private String defaultAvatar;

    final private String REGISTER_CACHE_KEY = "register-";
    final private String LOGIN_CACHE_KEY = "login-";

    final private SmsUtils smsUtils;
    final private JwtUtils jwtUtils;
    final private UserRepository userRepository;
    final private UserFriendSettingRepository userFriendSettingRepository;
    final private ConcurrentHashMap<String, CodeCache> codeCacheMap = new ConcurrentHashMap<>(300);


    public UserServiceImpl(SmsUtils smsUtils, JwtUtils jwtUtils, UserRepository userRepository, UserFriendSettingRepository userFriendSettingRepository) {
        this.smsUtils = smsUtils;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.userFriendSettingRepository = userFriendSettingRepository;
    }


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

    @Override
    public Mono<ResponseResult> findAllUser() {
        return userRepository.findAll()
                .collect(Collectors.toList())
                .map(ResponseResult::ok);
    }

    @Data
    @AllArgsConstructor
    private static class CodeCache {
        private String code;
        private Date expireDate;
    }

    /**
     * 发送登录验证码
     *
     * @param phone
     * @return
     */
    @Override
    public Mono<ResponseResult> sendLoginSmsCode(String phone) {
        return Mono.just(phone)
                .map(u -> {
                    final CodeCache codeCache = getCodeAndPut("login", phone);
                    smsUtils.sendLoginSmsCode(phone, codeCache.getCode());
                    return ResponseResult.ok("发送成功");
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
        return Mono.fromSupplier(() -> {
            String key = getCacheKey("login", phone);
            final CodeCache codeCache = codeCacheMap.get(key);
            if (codeCache != null) {
                if (codeCache.getExpireDate().after(new Date()) && codeCache.getCode().equals(code)) {
                    codeCacheMap.remove(codeCache.getCode());
                    return true;
                }
                return false;
            }
            return false;
        }).filter(b -> b)
                // 创建用户
                .flatMap(b -> userRepository
                        .findFirstByPhone(phone)
                        .switchIfEmpty(Mono.defer(() -> {
                            final Date date = new Date();
                            final User user = User.builder()
                                    .phone(phone)
                                    .nickname(phone)
                                    .avatar(defaultAvatar)
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
                    final String token = jwtUtils.createToken(userId);
                    return ResponseResult.ok("登录成功", token);
                })
                .defaultIfEmpty(ResponseResult.of(Code.SMS_CODE_ERROR, "验证码不正确或已过期"));
    }

    @Override
    public Mono<ResponseResult> getUserBaseInfo(ObjectId userId) {
        final Mono<User> baseInfoById = userRepository.getBaseInfoById(userId);
        return baseInfoById
                .map(MyBeanUtils::beanToMap)
                .map(ResponseResult::ok)
                .switchIfEmpty(Mono.fromSupplier(() -> ResponseResult.of(Code.USER_NOT_EXIST, "用户不存在")));
    }

    private CodeCache getCodeAndPut(String type, String phone) {
        String code;
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
            calendar.add(Calendar.SECOND, codeExpire);
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
        String key;
        switch (type) {
            case "register":
                key = REGISTER_CACHE_KEY + phone;
                break;
            case "login":
                key = LOGIN_CACHE_KEY + phone;
                break;
            default:
                throw new RuntimeException("code type " + type + " error");
        }
        return key;
    }


}
