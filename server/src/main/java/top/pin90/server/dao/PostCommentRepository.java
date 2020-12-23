package top.pin90.server.dao;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import top.pin90.server.po.PostComment;

public interface PostCommentRepository extends ReactiveSortingRepository<PostComment,String> {
    Flux<PostComment> findPostCommentByPostIdAndStatus(ObjectId postId,String status, Pageable pageable);



}
