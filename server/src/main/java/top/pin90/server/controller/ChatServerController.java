package top.pin90.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.server.service.ChatServerService;

@RestController
@RequestMapping("/chat/server")
public class ChatServerController {
    final ChatServerService chatServerService;

    public ChatServerController(ChatServerService chatServerService) {
        this.chatServerService = chatServerService;
    }

    @GetMapping("/all")
    public Mono<ResponseResult> getAll() {
        return chatServerService.getAllServer();
    }

    @GetMapping("/get")
    public Mono<ResponseResult> get() {
        return chatServerService.getServer();

    }

}
