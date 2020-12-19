package top.pin90.server.service;

import top.pin90.common.pojo.ResponseResult;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<ResponseResult> findAllUser();
    Mono<ResponseResult> sendLoginSmsCode(String phone);
    Mono<ResponseResult> smsCodeLogin(String phone, String code);
    Mono<ResponseResult> getUserBaseInfo(String userId);
}

