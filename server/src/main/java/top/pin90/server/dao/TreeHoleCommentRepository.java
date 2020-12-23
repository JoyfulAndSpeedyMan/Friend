package top.pin90.server.dao;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import top.pin90.server.po.TreeHoleComment;

public interface TreeHoleCommentRepository extends ReactiveSortingRepository<TreeHoleComment,String> {
//    Flux<PostComment> findPostCommentByPostIdAndStatus(ObjectId postId,String status, Pageable pageable);

}
