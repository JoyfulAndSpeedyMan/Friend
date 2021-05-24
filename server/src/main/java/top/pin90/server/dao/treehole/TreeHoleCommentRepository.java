package top.pin90.server.dao.treehole;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import top.pin90.common.po.treehole.TreeHoleComment;

public interface TreeHoleCommentRepository extends ReactiveMongoRepository<TreeHoleComment,String> {
//    Flux<PostComment> findPostCommentByPostIdAndStatus(ObjectId postId,String status, Pageable pageable);

}
