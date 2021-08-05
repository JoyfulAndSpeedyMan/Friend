package top.pin90.server.service;

import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;
import top.pin90.common.pojo.ResponseResult;

public interface PostCommentService {
    Mono<ResponseResult> publishComment(ObjectId userId, ObjectId postId, String content);

    Mono<ResponseResult> findCommentByPostId(ObjectId postId, int page, int size, ObjectId userId);

    Mono<ResponseResult> findAllPostCommentByPostIdAndStatus(ObjectId postId, int page, int size);

    Mono<ResponseResult> replyComment(ObjectId postId, ObjectId replyUserId, ObjectId replyId, String content, ObjectId userId);

    Mono<ResponseResult> deleteComment(ObjectId commentId, ObjectId userId);

    Mono<ResponseResult> thumb(ObjectId commentId, ObjectId userId);

    Mono<ResponseResult> cancelThumb(ObjectId commentId, ObjectId userId);
}
