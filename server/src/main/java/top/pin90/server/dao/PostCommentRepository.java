package top.pin90.server.dao;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import top.pin90.server.po.PostComment;

public interface PostCommentRepository extends ReactiveSortingRepository<PostComment,String> {
}
