package top.pin90.server.dao.chat;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import top.pin90.common.po.chat.UserChatSession;

public interface UserChatSessionRepository extends ReactiveMongoRepository<UserChatSession, ObjectId> {
    Mono<UserChatSession> findFirstById(ObjectId id);
}
