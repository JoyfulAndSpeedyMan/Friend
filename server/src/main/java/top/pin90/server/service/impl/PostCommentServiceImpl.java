package top.pin90.server.service.impl;

import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.server.dao.PostCommentRepository;
import top.pin90.server.po.Status;
import top.pin90.server.service.PostCommentService;

@Service
public class PostCommentServiceImpl implements PostCommentService {
    final private PostCommentRepository postCommentRepository;

    public PostCommentServiceImpl(PostCommentRepository postCommentRepository) {
        this.postCommentRepository = postCommentRepository;
    }


    @Override
    public Mono<ResponseResult> findCommentByPostId(ObjectId postId, int page, int size) {
        return postCommentRepository.findPostCommentByPostIdAndStatus(postId, Status.NORMAL,PageRequest.of(page, size))
                .collectList()
                .map(ResponseResult::ok);

    }
}
