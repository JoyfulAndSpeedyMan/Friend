package top.pin90.server.service;

import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;
import top.pin90.common.pojo.ResponseResult;

public interface ChatService {
    Mono<ResponseResult> getAllChatSession(ObjectId userId);
    Mono<ResponseResult> loadMoreChatMessage(ObjectId userId,ObjectId sessionId, long skip , int size);
    Mono<ResponseResult> cleanUnread(ObjectId id);
}
