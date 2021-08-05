package top.pin90.server.dao.post;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import top.pin90.common.po.post.PostCommentThumb;

public interface PostCommentThumbRepository extends ReactiveMongoRepository<PostCommentThumb,Object> {
    Mono<PostCommentThumb> findByPostCommentIdAndUserId(ObjectId postCommentId, ObjectId userId);
    Mono<Long> deleteByPostCommentIdAndUserId(ObjectId postCommentId, ObjectId userId);
}
