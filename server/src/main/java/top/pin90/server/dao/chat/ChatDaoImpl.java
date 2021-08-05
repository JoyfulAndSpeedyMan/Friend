package top.pin90.server.dao.chat;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import top.pin90.common.dao.BsonManager;
import top.pin90.common.po.chat.ChatSessionMessage;
import top.pin90.common.po.chat.UserChatSession;
import top.pin90.server.dao.JsonOperation;

import java.util.Map;

@Repository
@Component
public class ChatDaoImpl implements ChatDao{
    private final ReactiveMongoTemplate template;
    private final BsonManager bsonManager;

    public ChatDaoImpl(ReactiveMongoTemplate template, BsonManager bsonManager) {
        this.template = template;
        this.bsonManager = bsonManager;
    }

    @Override
    public Flux<Map> getAllChatSession(ObjectId userId) {
        String bson = bsonManager.getBsonWithVar("chat/getAllChatSession", userId);
        JsonOperation operation = new JsonOperation(bson);
        return template.aggregate(Aggregation.newAggregation(operation), UserChatSession.class, Map.class);
    }

    @Override
    public Flux<Map> loadMoreChatMessage(ObjectId sessionId, long skip, int size) {
        String bson = bsonManager.getBsonWithVar("chat/loadMoreChatMessage", sessionId,skip,size);
        JsonOperation operation = new JsonOperation(bson);
        return template.aggregate(Aggregation.newAggregation(operation), ChatSessionMessage.class, Map.class);
    }


}
