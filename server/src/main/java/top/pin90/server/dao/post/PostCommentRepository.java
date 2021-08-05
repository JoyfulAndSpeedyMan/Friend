package top.pin90.server.dao.post;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import top.pin90.common.po.post.PostComment;

public interface PostCommentRepository extends ReactiveMongoRepository<PostComment, ObjectId> {

    Mono<Long> countByPostIdAndStatus(ObjectId postId, String status);

    Mono<Boolean> deleteByIdAndUserId(ObjectId id, ObjectId userId);

//    Mono<Page> findPostCommentByPostIdAndStatus(ObjectId postId, String status, Pageable pageable);

}
