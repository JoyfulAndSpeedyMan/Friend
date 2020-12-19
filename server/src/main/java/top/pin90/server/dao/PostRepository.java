package top.pin90.server.dao;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import top.pin90.server.po.Post;

public interface PostRepository extends ReactiveSortingRepository<Post,String> {
}
