package top.pin90.server.dao.user;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import top.pin90.common.annotation.MyDao;
import top.pin90.common.dao.BsonManager;
import top.pin90.common.po.user.UserFriendRelation;
import top.pin90.server.dao.JsonOperation;

import java.util.Map;

@Component
@MyDao
public class UserFriendRelationDaoImpl implements UserFriendRelationDao{
    private final ReactiveMongoTemplate template;
    private final BsonManager bsonManager;

    public UserFriendRelationDaoImpl(ReactiveMongoTemplate template, BsonManager bsonManager) {
        this.template = template;
        this.bsonManager = bsonManager;
    }

    @Override
    public Flux<Map> findAllFriend(ObjectId userId) {
        String bson = bsonManager.getBson("userFriendRelation/findAllFriend", userId, 1);
        JsonOperation jsonOperation = new JsonOperation(bson);
        return template.aggregate(Aggregation.newAggregation(jsonOperation), UserFriendRelation.class, Map.class);
    }
}
