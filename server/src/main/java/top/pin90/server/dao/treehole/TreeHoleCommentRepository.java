package top.pin90.server.dao.treehole;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import top.pin90.common.po.treehole.TreeHoleComment;

public interface TreeHoleCommentRepository extends ReactiveSortingRepository<TreeHoleComment,String> {
//    Flux<PostComment> findPostCommentByPostIdAndStatus(ObjectId postId,String status, Pageable pageable);

}
