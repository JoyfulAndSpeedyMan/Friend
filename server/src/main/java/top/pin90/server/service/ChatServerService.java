package top.pin90.server.service;

import reactor.core.publisher.Mono;
import top.pin90.common.pojo.ResponseResult;

public interface ChatServerService {
    Mono<ResponseResult> getServer();
    Mono<ResponseResult> getAllServer();

}
