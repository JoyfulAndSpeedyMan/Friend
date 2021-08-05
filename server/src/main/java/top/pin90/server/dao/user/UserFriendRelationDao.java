package top.pin90.server.dao.user;

import org.bson.types.ObjectId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.pin90.common.dao.MyRepository;
import top.pin90.common.po.user.UserFriendRelation;

import java.util.Map;

public interface UserFriendRelationDao extends MyRepository<UserFriendRelation> {
    Flux<Map> findAllFriend(ObjectId userId);
    Flux<Map> findFriendRequest(ObjectId userId);
    Mono<Map> findFriendInfo(ObjectId userId,ObjectId friendId);
}
