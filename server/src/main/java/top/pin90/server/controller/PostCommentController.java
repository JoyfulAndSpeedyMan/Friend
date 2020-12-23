package top.pin90.server.controller;

import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.server.service.PostCommentService;
import top.pin90.server.utils.PageUtils;

@RestController
@RequestMapping("/post/comment")
public class PostCommentController {
    private final PostCommentService postCommentService;

    public PostCommentController(PostCommentService postCommentService) {
        this.postCommentService = postCommentService;
    }

    @GetMapping
    public Mono<ResponseResult> findCommentByPostId(ObjectId postId, int page, int size){
        page= PageUtils.pageLimit(page);
        size= PageUtils.sizeLimit(size);
        return postCommentService.findCommentByPostId(postId, page, size);
    }
}
