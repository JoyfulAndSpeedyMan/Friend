package top.pin90.server.service.impl;

import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.pin90.common.pojo.Code;
import top.pin90.common.pojo.Page;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.common.unti.PinyinUtils;
import top.pin90.server.dao.user.UserFriendRelationDaoImpl;
import top.pin90.server.dao.user.UserFriendRelationRepository;
import top.pin90.server.dao.user.UserFriendSettingRepository;
import top.pin90.server.dao.user.UserRepository;
import top.pin90.common.po.user.FriendReqVerMode;
import top.pin90.common.po.user.UserFriendRelation;
import top.pin90.common.po.user.UserFriendRelationStatus;
import top.pin90.server.service.UserFriendRelationService;

import java.util.Date;

import static org.springframework.data.mongodb.core.query.Criteria.*;
import static top.pin90.server.utils.PageUtils.*;

@Service
public class UserFriendRelationServiceImpl implements UserFriendRelationService {
    private final UserRepository userRepository;
    private final UserFriendRelationRepository friendRelationRepository;
    private final UserFriendSettingRepository friendSettingRepository;
    private final ReactiveMongoTemplate template;
    private final UserFriendRelationDaoImpl userFriendRelationDao;

    public UserFriendRelationServiceImpl(UserRepository userRepository,
                                         UserFriendRelationRepository friendRelationRepository,
                                         UserFriendSettingRepository friendSettingRepository,
                                         ReactiveMongoTemplate template,
                                         UserFriendRelationDaoImpl userFriendRelationDao) {
        this.userRepository = userRepository;
        this.friendRelationRepository = friendRelationRepository;
        this.friendSettingRepository = friendSettingRepository;
        this.template = template;
        this.userFriendRelationDao = userFriendRelationDao;
    }

    @Override
    public Mono<ResponseResult> getAllFriend(ObjectId userId) {
        return userFriendRelationDao.findAllFriend(userId)
                .collectList()
                .map(l-> PinyinUtils.spellGroup(l,"fNickname"))
                .map(ResponseResult::ok);
    }

    @Override
    public Mono<ResponseResult> preAddFriend(ObjectId friendId, ObjectId userId) {
        return friendSettingRepository.findByUserId(friendId)
                .flatMap(ResponseResult::monoOk);
    }

    @Override
    public Mono<ResponseResult> getFriendRequest(ObjectId userId, int page, int size) {
        int page1 = pageLimit(page);
        int size1 = sizeLimit(size);

        final PageRequest pageRequest = PageRequest.of(page1, size1);
        final Flux<UserFriendRelation> relationFlux =
                friendRelationRepository.findBySuidAndStatus(userId,UserFriendRelationStatus.REQUEST,pageRequest);
        final Mono<Long> longMono = friendRelationRepository.countBySuid(userId);

        return Page.from(relationFlux, longMono, page, size)
                .map(ResponseResult::ok);
    }

    @Override
    public Mono<ResponseResult> addFriend(ObjectId friendId, String content, ObjectId userId) {
        // 查找好友请求设置
        return friendSettingRepository.findByUserId(friendId)
                // 业务逻辑处理
                .flatMap(setting -> {
                    // 第一次添加好友进行的操作
                    final Mono<UserFriendRelation> save = Mono.defer(() -> {
                        final Date date = new Date();
                        final UserFriendRelation.UserFriendRelationBuilder builder = UserFriendRelation.builder()
                                .fuid(userId)
                                .suid(friendId)
                                .status(UserFriendRelationStatus.REQUEST)
                                .createTime(date)
                                .updateTime(date);
                        if (setting.getFriReqVerMode().equals(FriendReqVerMode.MESSAGE))
                            builder.requestMsg(content);
                        else if (setting.getFriReqVerMode().equals(FriendReqVerMode.QUESTION)) {
                            // TODO: 2020/12/30 请求问题验证
                        }
                        return friendRelationRepository.save(builder.build())
                                .map(relation -> {
                                    // TODO: 2020/12/30  推送好友请求消息

                                    return relation;
                                });
                    });

                    return friendRelationRepository.findByFuidAndSuid(userId, friendId)
                            // 不是第一次添加好友进行的操作
                            .flatMap(relation -> {
                                Mono<UserFriendRelation> relationMono = Mono.just(relation);
                                final String status = relation.getStatus();
                                // 如果是在黑名单中，则直接更新为正常状态
                                if (status.equals(UserFriendRelationStatus.BLACKLIST)) {
                                    relation.setStatus(UserFriendRelationStatus.NORMAL);
                                    relation.setUpdateTime(new Date());
                                    relationMono = friendRelationRepository.save(relation);
                                }
                                // 如果为请求状态，则推送好友请求
                                else if (status.equals(UserFriendRelationStatus.REQUEST)) {
                                    relation.setUpdateTime(new Date());
                                    relationMono = friendRelationRepository.save(relation);
                                    // TODO: 2020/12/30 推送好友请求
                                }
                                // 如果为拒绝状态，则推送好友请求并设置为请求状态
                                else if (status.equals(UserFriendRelationStatus.REJECT)) {
                                    relation.setStatus(UserFriendRelationStatus.REQUEST);
                                    relation.setUpdateTime(new Date());
                                    relationMono = friendRelationRepository.save(relation);
                                }
                                // 如果为删除状态，则根据是否被对方删除来判断是否推送消息
                                else if (status.equals(UserFriendRelationStatus.DELETE)) {
                                    relationMono = friendRelationRepository.findByFuidAndSuid(friendId,userId)
                                            .flatMap(r -> {
                                                // 如果对方也已经删除，则需要推送好友请求
                                                if (r.getStatus().equals(UserFriendRelationStatus.DELETE)) {
                                                    relation.setLastStatus(null);
                                                    relation.setStatus(UserFriendRelationStatus.REQUEST);

                                                    // TODO: 2020/12/30 推送好友请求

                                                    return friendRelationRepository.save(relation);
                                                }
                                                // 否则直接设置为正常状态
                                                relation.setStatus(UserFriendRelationStatus.NORMAL);
                                                return friendRelationRepository.save(relation);
                                            })
                                            // 如果对方也已经删除，则需要推送好友请求
                                            .switchIfEmpty(Mono.defer(() -> {
                                                relation.setLastStatus(null);
                                                relation.setStatus(UserFriendRelationStatus.REQUEST);
                                                // TODO: 2020/12/30 推送好友请求
                                                return friendRelationRepository.save(relation);
                                            }));
                                } else if (status.equals(UserFriendRelationStatus.NORMAL)) {
                                    // 正常状态，啥也不做
                                }
                                return relationMono;
                            })
                            .switchIfEmpty(save);
                })
                .map(r -> ResponseResult.ok("操作成功"))
                // 处理返回结果
                .switchIfEmpty(ResponseResult.toMono(Code.USER_VERITY_ERROR, "添加失败"));
    }

    @Override
    public Mono<ResponseResult> acceptFriend(ObjectId friendId, ObjectId userId) {
        final Mono<UpdateResult> updateResultMono = template.updateFirst(
                Query.query(
                        where("fuid").is(friendId)
                        .and("suid").is(userId)
                        .and("status").is(UserFriendRelationStatus.REQUEST)),
                Update.update("status", UserFriendRelationStatus.NORMAL),
                UserFriendRelation.class);
        final Mono<UserFriendRelation> userFriendRelationMono = Mono.defer(() -> {
            final Date date = new Date();
            final UserFriendRelation userFriendRelation = UserFriendRelation.builder()
                    .fuid(userId)
                    .suid(friendId)
                    .status(UserFriendRelationStatus.NORMAL)
                    .createTime(date)
                    .updateTime(date)
                    .build();
            return friendRelationRepository.save(userFriendRelation);
        });
        return updateResultMono.flatMap(result -> {
            if (result.getMatchedCount() == 1) {
                return userFriendRelationMono.map(u -> ResponseResult.ok("操作成功"));
            }
            return ResponseResult.toMono(Code.CLIENT_ERROR, "还没有好友请求");
        });

    }

    @Override
    public Mono<ResponseResult> rejectFriend(ObjectId friendId, String content, ObjectId userId) {
        final Update update = updateStatus(UserFriendRelationStatus.REJECT);
        if (StringUtils.hasText(content))
            update.set("resMsg", content);
        return updateFriendRelation(update, friendId, userId);
    }

    @Override
    public Mono<ResponseResult> blacklistFriend(ObjectId friendId, ObjectId userId) {
        return friendRelationRepository.findByFuidAndSuid(friendId, userId)
                .flatMap(relation -> {
                    relation.setLastStatus(relation.getStatus());
                    relation.setStatus(UserFriendRelationStatus.BLACKLIST);
                    return friendRelationRepository.save(relation);
                })
                .map(relation -> ResponseResult.ok("操作成功！"))
                .switchIfEmpty(ResponseResult.toMono(Code.CLIENT_ERROR, "好友请求不存在"));
    }

    @Override
    public Mono<ResponseResult> cancelBlacklistFriend(ObjectId friendId, ObjectId userId) {
        return friendRelationRepository.findByFuidAndSuid(friendId, userId)
                .flatMap(relation -> {
                    final String lastStatus = relation.getLastStatus();
                    if (StringUtils.hasText(lastStatus)) {
                        relation.setLastStatus(null);
                        relation.setStatus(lastStatus);
                    }
                    return friendRelationRepository.save(relation);
                })
                .map(relation -> ResponseResult.ok("操作成功！"))
                .switchIfEmpty(ResponseResult.toMono(Code.CLIENT_ERROR, "好友请求不存在"));
    }

    @Override
    public Mono<ResponseResult> deleteFriend(ObjectId friendId, ObjectId userId) {
        return updateFriendRelation(updateStatus(UserFriendRelationStatus.DELETE), friendId, userId);
    }

    private Update updateStatus(String status) {
        return Update.update("status", status);
    }

    private Mono<ResponseResult> updateFriendRelation(Update update, ObjectId fid, ObjectId uid) {
        final Mono<UpdateResult> updateResultMono = template.updateFirst(
                Query.query(where("fuid").is(fid).and("suid").is(uid)),
                update,
                UserFriendRelation.class);
        return updateResultMono
                .map(result -> {
                    if (result.wasAcknowledged()) {
                        // TODO: 2020/12/30 推送消息
                        return ResponseResult.ok("操作成功");
                    }
                    return ResponseResult.of(Code.SERVER_EXE_ERROR, "操作失败");
                });
    }
}
