package top.pin90.server.dao.post;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.pin90.server.po.post.PostComment;

public interface PostCommentRepository extends ReactiveSortingRepository<PostComment,ObjectId> {
    Flux<PostComment> findPostCommentByPostIdAndStatus(ObjectId postId, String status, Pageable pageable);
    Mono<Long> countByPostIdAndStatus(ObjectId postId, String status);
    Mono<Void> deleteByIdAndUserId(ObjectId id,ObjectId userId);

//    Mono<Page> findPostCommentByPostIdAndStatus(ObjectId postId, String status, Pageable pageable);

}
