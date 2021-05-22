package top.pin90.server.service.impl;

import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.pin90.common.po.user.FriendReqVerMode;
import top.pin90.common.po.user.UserFriendRelation;
import top.pin90.common.po.user.UserFriendRelationStatus;
import top.pin90.common.pojo.Code;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.common.unti.function.SingleMyTuple2;
import top.pin90.common.unti.pinyin.PinyinUtils;
import top.pin90.server.dao.user.UserFriendRelationDao;
import top.pin90.server.dao.user.UserFriendRelationRepository;
import top.pin90.server.dao.user.UserFriendSettingRepository;
import top.pin90.server.dao.user.UserRepository;
import top.pin90.server.service.UserFriendRelationService;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class UserFriendRelationServiceImpl implements UserFriendRelationService {
    private final UserRepository userRepository;
    private final UserFriendRelationRepository friendRelationRepository;
    private final UserFriendSettingRepository friendSettingRepository;
    private final ReactiveMongoTemplate template;
    private final UserFriendRelationDao userFriendRelationDao;


    public UserFriendRelationServiceImpl(UserRepository userRepository,
                                         UserFriendRelationRepository friendRelationRepository,
                                         UserFriendSettingRepository friendSettingRepository,
                                         ReactiveMongoTemplate template,
                                         UserFriendRelationDao userFriendRelationDao) {
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
                .map(l -> PinyinUtils.spellGroup(l, "friendName"))
                .map(ResponseResult::ok);
    }

    @Override
    public Mono<ResponseResult> preAddFriend(ObjectId friendId, ObjectId userId) {
        return friendSettingRepository.findByUserId(friendId)
                .flatMap(ResponseResult::monoOk);
    }

    @Override
    public Mono<ResponseResult> getFriendRequest(ObjectId userId) {
        Flux<Map> allFriend = userFriendRelationDao.findFriendRequest(userId);
        return allFriend.collectList()
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
                        } else if (setting.getFriReqVerMode().equals(FriendReqVerMode.NONE)) {
                            // TODO: 2021/5/20 无需验证，直接通过请求
                        }
                        return friendRelationRepository.save(builder.build())
                                .map(relation -> {
                                    // TODO: 2020/12/30  推送好友请求消息

                                    return relation;
                                });
                    });

                    return friendRelationRepository.findUserFriendRelation(userId, friendId)
                            .collectList()
                            // 不是第一次添加好友进行的操作
                            .flatMap(rels -> {
                                if (rels.isEmpty())
                                    return save;
                                SingleMyTuple2<UserFriendRelation> tr = sortByUserAndFriendId(rels, userId, friendId);
                                UserFriendRelation relation1 = tr.getT1();
                                Mono<UserFriendRelation> relationMono = Mono.just(relation1);
                                final String status = relation1.getStatus();
                                // 如果是在黑名单中，则直接更新为正常状态
                                if (status.equals(UserFriendRelationStatus.BLACKLIST)) {
                                    relation1.setStatus(UserFriendRelationStatus.NORMAL);
                                    relation1.setUpdateTime(new Date());
                                    relationMono = friendRelationRepository.save(relation1);
                                }
                                // 如果为请求状态，则推送好友请求
                                else if (status.equals(UserFriendRelationStatus.REQUEST)) {
                                    relation1.setUpdateTime(new Date());
                                    relation1.setRequestMsg(content);
                                    relationMono = friendRelationRepository.save(relation1);
                                    // TODO: 2020/12/30 推送好友请求
                                }
                                // 如果为拒绝状态，则推送好友请求并设置为请求状态
                                else if (status.equals(UserFriendRelationStatus.REJECT)) {
                                    relation1.setRequestMsg(content);
                                    relation1.setStatus(UserFriendRelationStatus.REQUEST);
                                    relation1.setUpdateTime(new Date());
                                    relationMono = friendRelationRepository.save(relation1);
                                }
                                // 如果为删除状态，则根据是否被对方删除来判断是否推送消息
                                else {
                                    int size = rels.size();
                                    if (status.equals(UserFriendRelationStatus.DELETE)) {
                                        relationMono = friendRelationRepository.findByFuidAndSuid(friendId, userId)
                                                .flatMap(r -> {
                                                    // 如果对方也已经删除，则需要推送好友请求
                                                    if (r.getStatus().equals(UserFriendRelationStatus.DELETE) || !tr.hasT2()) {
                                                        relation1.setLastStatus(null);
                                                        relation1.setStatus(UserFriendRelationStatus.REQUEST);
                                                        relation1.setUpdateTime(new Date());
                                                        relation1.setRequestMsg(content);
                                                        // TODO: 2020/12/30 推送好友请求

                                                        return friendRelationRepository.save(relation1);
                                                    }
                                                    // 否则直接设置为正常状态
                                                    relation1.setStatus(UserFriendRelationStatus.NORMAL);
                                                    return friendRelationRepository.save(relation1);
                                                })
                                                // 如果对方也已经删除，则需要推送好友请求
                                                .switchIfEmpty(Mono.defer(() -> {
                                                    relation1.setLastStatus(null);
                                                    relation1.setStatus(UserFriendRelationStatus.REQUEST);
                                                    relation1.setRequestMsg(content);
                                                    relation1.setUpdateTime(new Date());
                                                    // TODO: 2020/12/30 推送好友请求
                                                    return friendRelationRepository.save(relation1);
                                                }));
                                    } else if (status.equals(UserFriendRelationStatus.NORMAL)) {
                                        // 已经被对方删除了
                                        if (!tr.hasT2() || tr.getT2().getStatus().equals(UserFriendRelationStatus.DELETE)) {
                                            relation1.setLastStatus(relation1.getStatus());
                                            relation1.setStatus(UserFriendRelationStatus.REQUEST);
                                            relation1.setUpdateTime(new Date());
                                            relation1.setRequestMsg(content);
                                            relationMono = friendRelationRepository.save(relation1);
                                        }
                                        // 正常状态，啥也不做
                                    }
                                }
                                return relationMono;
                            })
                            .switchIfEmpty(save);
                })
                .map(r -> ResponseResult.ok("操作成功"))
                // 处理返回结果
                .switchIfEmpty(ResponseResult.toMono(Code.USER_VERITY_ERROR, "添加失败"));
    }

    private SingleMyTuple2<UserFriendRelation> sortByUserAndFriendId(List<UserFriendRelation> rels, ObjectId userId, ObjectId friendId) {
        if (rels.isEmpty())
            return new SingleMyTuple2<>();
        if (rels.size() == 1)
            return new SingleMyTuple2<>(rels.get(0), null);
        UserFriendRelation r1 = rels.get(0);
        UserFriendRelation r2 = rels.get(1);
        if (r1.getFuid().equals(userId))
            return new SingleMyTuple2<>(r1, r2);
        else
            return new SingleMyTuple2<>(r2, r1);

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
            return friendRelationRepository.findByFuidAndSuid(userId, friendId)
                    .flatMap(r -> {
                        r.setLastStatus(r.getStatus());
                        r.setStatus(UserFriendRelationStatus.NORMAL);
                        r.setUpdateTime(new Date());
                        return friendRelationRepository.save(r);
                    })
                    .switchIfEmpty(friendRelationRepository.save(userFriendRelation));
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
        return updateFriendRelation(update, userId, friendId);
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
        return updateFriendRelation(updateStatus(UserFriendRelationStatus.DELETE), userId, friendId);
    }

    @Override
    public Mono<ResponseResult> findUserByPhone(String phone, ObjectId userId) {
        return userRepository.findFirstByPhone(phone)
                .map(ResponseResult::ok)
                .switchIfEmpty(ResponseResult.toMono(Code.PARAM_ERROR, "找不到该用户"));
    }

    @Override
    public Mono<ResponseResult> getFriendInfo(ObjectId userId, ObjectId friendId) {
        return userFriendRelationDao.findFriendInfo(userId, friendId)
                .map(ResponseResult::ok)
                .defaultIfEmpty(ResponseResult.of(Code.PARAM_ERROR, "用户不存在"));
    }

    private Update updateStatus(String status) {
        return Update.update("status", status);
    }


    private Mono<ResponseResult> updateFriendRelation(Update update, ObjectId fuid, ObjectId suid) {
        final Mono<UpdateResult> updateResultMono = template.updateFirst(
                Query.query(where("fuid").is(fuid).and("suid").is(suid)),
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
