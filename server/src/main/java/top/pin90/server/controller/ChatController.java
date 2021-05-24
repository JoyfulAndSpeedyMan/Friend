package top.pin90.server.controller;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import top.pin90.common.annotation.Token;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.server.service.ChatService;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/session")
    public Mono<ResponseResult> getAllChatSession(@Token ObjectId userId) {
        return chatService.getAllChatSession(userId);
    }

    @GetMapping("/message")
    public Mono<ResponseResult> loadMoreChatMessage(@Token ObjectId userId,
                                                    @RequestParam ObjectId sessionId,
                                                    @RequestParam int skip,
                                                    @RequestParam int size){
        return chatService.loadMoreChatMessage(userId, sessionId, skip, size);
    }
    @PutMapping("/uc/cleanUnread")
    public Mono<ResponseResult> cleanUnread(ObjectId id){
        return chatService.cleanUnread(id);
    }
}
