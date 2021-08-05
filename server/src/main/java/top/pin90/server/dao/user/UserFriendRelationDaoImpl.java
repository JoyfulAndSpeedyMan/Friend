package top.pin90.server.dao.user;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.pin90.common.annotation.MyDao;
import top.pin90.common.dao.BsonManager;
import top.pin90.common.po.user.UserFriendRelation;
import top.pin90.server.dao.JsonOperation;

import java.util.Map;

@Component
@MyDao
public class UserFriendRelationDaoImpl implements UserFriendRelationDao {
    private final ReactiveMongoTemplate template;
    private final BsonManager bsonManager;

    public UserFriendRelationDaoImpl(ReactiveMongoTemplate template, BsonManager bsonManager) {
        this.template = template;
        this.bsonManager = bsonManager;
    }

    @Override
    public Flux<Map> findAllFriend(ObjectId userId) {
        Aggregation findAllFriend = getAggregation("findAllFriend", userId);
        return template.aggregate(findAllFriend, UserFriendRelation.class, Map.class);
    }

    @Override
    public Flux<Map> findFriendRequest(ObjectId userId) {
        Aggregation findAllFriend = getAggregation("findFriendRequest", userId);
        return template.aggregate(findAllFriend, UserFriendRelation.class, Map.class);
    }

    @Override
    public Mono<Map> findFriendInfo(ObjectId userId, ObjectId friendId) {
        Aggregation aggregation = getAggregation("findFriendInfo", userId, friendId);
        Flux<Map> result = template.aggregate(aggregation, UserFriendRelation.class, Map.class);
        return result.collectList()
                .flatMap(l -> {
                    if (l.isEmpty())
                        return Mono.empty();
                    else
                        return Mono.just(l.get(0));
                });
    }

    private Aggregation getAggregation(String name, Object... params) {
        String bson = bsonManager.getBsonWithVar("userFriendRelation/" + name, params);
        JsonOperation jsonOperation = new JsonOperation(bson);
        return Aggregation.newAggregation(jsonOperation);
    }
}
