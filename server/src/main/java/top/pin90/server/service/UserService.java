package top.pin90.server.service;

import org.bson.types.ObjectId;
import top.pin90.common.po.user.User;
import top.pin90.common.pojo.ResponseResult;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<ResponseResult> findAllUser();
    Mono<ResponseResult> sendLoginSmsCode(String phone);

    Mono<ResponseResult> smsCodeLogin(String phone, String code);
    Mono<ResponseResult> refreshToken(String refreshToken);
    Mono<ResponseResult> getUserBaseInfo(ObjectId userId);

    Mono<ResponseResult> updateUserInfo(User user);
}

