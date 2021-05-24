package top.pin90.server.dao.chat;

import org.bson.types.ObjectId;
import reactor.core.publisher.Flux;

import java.util.Map;

public interface ChatDao {
    Flux<Map> getAllChatSession(ObjectId userId);
    Flux<Map> loadMoreChatMessage(ObjectId sessionId,long skip,int size);
}
