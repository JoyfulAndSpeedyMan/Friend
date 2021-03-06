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

    public UserFriendRelationServiceImpl(UserRepository userRepository,
                                         UserFriendRelationRepository friendRelationRepository,
                                         UserFriendSettingRepository friendSettingRepository,
                                         ReactiveMongoTemplate template) {
        this.userRepository = userRepository;
        this.friendRelationRepository = friendRelationRepository;
        this.friendSettingRepository = friendSettingRepository;
        this.template = template;
    }

    @Override
    public Mono<ResponseResult> getAllFriend(ObjectId userId) {

        return null;
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
        final Flux<UserFriendRelation> relationFlux = friendRelationRepository.findBySuid(userId, pageRequest);
        final Mono<Long> longMono = friendRelationRepository.countBySuid(userId);

        return Page.from(relationFlux, longMono, page, size)
                .map(p -> {
                    return ResponseResult.ok(p);
                });
    }

    @Override
    public Mono<ResponseResult> addFriend(ObjectId friendId, String content, ObjectId userId) {
        // ????????????????????????
        return friendSettingRepository.findByUserId(friendId)
                // ??????????????????
                .flatMap(setting -> {
                    // ????????????????????????????????????
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
                            // TODO: 2020/12/30 ??????????????????
                        }
                        return friendRelationRepository.save(builder.build())
                                .map(relation -> {
                                    // TODO: 2020/12/30  ????????????????????????

                                    return relation;
                                });
                    });

                    return friendRelationRepository.findByFuidAndSuid(friendId, userId)
                            // ??????????????????????????????????????????
                            .flatMap(relation -> {
                                Mono<UserFriendRelation> relationMono = Mono.just(relation);
                                final String status = relation.getStatus();
                                // ?????????????????????????????????????????????????????????
                                if (status.equals(UserFriendRelationStatus.BLACKLIST)) {
                                    relation.setStatus(UserFriendRelationStatus.NORMAL);
                                    relationMono = friendRelationRepository.save(relation);
                                }
                                // ?????????????????????????????????????????????
                                else if (status.equals(UserFriendRelationStatus.REQUEST)) {
                                    // TODO: 2020/12/30 ??????????????????
                                }
                                // ?????????????????????????????????????????????????????????????????????
                                else if (status.equals(UserFriendRelationStatus.REJECT)) {
                                    relation.setStatus(UserFriendRelationStatus.REQUEST);
                                    relationMono = friendRelationRepository.save(relation);
                                }
                                // ?????????????????????????????????????????????????????????????????????????????????
                                else if (status.equals(UserFriendRelationStatus.DELETE)) {
                                    relationMono = friendRelationRepository.findByFuidAndSuid(userId, friendId)
                                            .flatMap(r -> {
                                                // ?????????????????????????????????????????????????????????
                                                if (r.getStatus().equals(UserFriendRelationStatus.DELETE)) {
                                                    relation.setLastStatus(null);
                                                    relation.setStatus(UserFriendRelationStatus.REQUEST);

                                                    // TODO: 2020/12/30 ??????????????????

                                                    return friendRelationRepository.save(relation);
                                                }
                                                // ?????????????????????????????????
                                                relation.setStatus(UserFriendRelationStatus.NORMAL);
                                                return friendRelationRepository.save(relation);
                                            })
                                            // ?????????????????????????????????????????????????????????
                                            .switchIfEmpty(Mono.defer(() -> {
                                                relation.setLastStatus(null);
                                                relation.setStatus(UserFriendRelationStatus.REQUEST);
                                                // TODO: 2020/12/30 ??????????????????
                                                return friendRelationRepository.save(relation);
                                            }));
                                }
                                return relationMono;
                            })
                            .switchIfEmpty(save);
                })
                .map(r -> ResponseResult.ok("????????????"))
                // ??????????????????
                .switchIfEmpty(ResponseResult.toMono(Code.USER_VERITY_ERROR, "????????????"));
    }

    @Override
    public Mono<ResponseResult> acceptFriend(ObjectId friendId, ObjectId userId) {
        final Mono<UpdateResult> updateResultMono = template.updateFirst(
                Query.query(where("fuid").is(friendId).and("suid").is(userId)),
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
                return userFriendRelationMono.map(u -> ResponseResult.ok("????????????"));
            }
            return ResponseResult.toMono(Code.CLIENT_ERROR, "?????????????????????");
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
                .map(relation -> ResponseResult.ok("???????????????"))
                .switchIfEmpty(ResponseResult.toMono(Code.CLIENT_ERROR, "?????????????????????"));
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
                .map(relation -> ResponseResult.ok("???????????????"))
                .switchIfEmpty(ResponseResult.toMono(Code.CLIENT_ERROR, "?????????????????????"));
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
                        // TODO: 2020/12/30 ????????????
                        return ResponseResult.ok("????????????");
                    }
                    return ResponseResult.of(Code.SERVER_EXE_ERROR, "????????????");
                });
    }
}
