package top.pin90.server.service;

import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;
import top.pin90.common.pojo.ResponseResult;

public interface PostCommentService {
    Mono<ResponseResult> findCommentByPostId(ObjectId postId, int page, int size);
}
