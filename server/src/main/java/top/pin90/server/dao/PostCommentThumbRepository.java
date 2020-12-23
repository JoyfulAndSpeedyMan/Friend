package top.pin90.server.dao;

import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;
import top.pin90.server.po.PostCommentThumb;

public interface PostCommentThumbRepository extends ReactiveSortingRepository<PostCommentThumb,Object> {
    Mono<PostCommentThumb> findByPostCommentIdAndUserId(ObjectId postCommentId, ObjectId userId);
    Mono<Long> deleteByPostCommentIdAndUserId(ObjectId postCommentId, ObjectId userId);
}
