package top.pin90.friend.chatserver.dao;

import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import top.pin90.common.po.chat.ChatSession;
import top.pin90.common.po.chat.UserChatSession;

public interface ChatSessionRepository  extends ReactiveSortingRepository<ChatSession, ObjectId> {
}
