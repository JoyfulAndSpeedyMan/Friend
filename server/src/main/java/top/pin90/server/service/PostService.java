package top.pin90.server.service;

import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;
import top.pin90.common.pojo.ResponseResult;

public interface PostService {
    Mono<ResponseResult> findAll(int page, int size);
    Mono<ResponseResult> findByUserId(ObjectId userId, int page, int size);
    Mono<ResponseResult> savePost(String content, ObjectId userId);
    Mono<ResponseResult> deletePostById(ObjectId postId,ObjectId userId);
    Mono<ResponseResult> thumb(ObjectId postId,ObjectId userId);
    Mono<ResponseResult> cancelThumb(ObjectId postId,ObjectId userId);
    Mono<ResponseResult> comment(ObjectId postId,String content,ObjectId userId);

    Mono<ResponseResult> forward(ObjectId postId,ObjectId userId);
}
