package top.pin90.server.service.impl;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.server.dao.chat.ChatDao;
import top.pin90.server.dao.chat.ChatSessionMessageRepository;
import top.pin90.server.dao.chat.UserChatSessionRepository;
import top.pin90.server.service.ChatService;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatDao chatDao;
    private final ChatSessionMessageRepository chatSessionMessageRepository;
    private final UserChatSessionRepository userChatSessionRepository;
    public ChatServiceImpl(ChatDao chatDao, ChatSessionMessageRepository chatSessionMessageRepository, UserChatSessionRepository userChatSessionRepository) {
        this.chatDao = chatDao;
        this.chatSessionMessageRepository = chatSessionMessageRepository;
        this.userChatSessionRepository = userChatSessionRepository;
    }

    @Override
    public Mono<ResponseResult> getAllChatSession(ObjectId userId) {
        return chatDao.getAllChatSession(userId)
                .collectList()
                .map(ResponseResult::ok);
    }

    @Override
    public Mono<ResponseResult> loadMoreChatMessage(ObjectId userId, ObjectId sessionId, long skip, int size) {
        return chatDao.loadMoreChatMessage(sessionId,skip,size)
                .collectList()
                .map(ResponseResult::ok);
    }

    @Override
    public Mono<ResponseResult> cleanUnread(ObjectId id) {
        return userChatSessionRepository.findFirstById(id)
                .flatMap(uc-> {
                    uc.setUnread(0);
                    return userChatSessionRepository.save(uc);
                })
                .map(uc-> ResponseResult.ok());
    }
}
