package top.pin90.friend.chatserver.dao;

import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;
import top.pin90.common.po.chat.UserChatSession;

public interface UserChatSessionRepository  extends ReactiveSortingRepository<UserChatSession, ObjectId> {
    Mono<UserChatSession> findFirstByUserIdAndTid(ObjectId userId,ObjectId tid);
}
