package top.pin90.server.controller;

import org.bson.types.ObjectId;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import top.pin90.common.annotation.Token;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.server.service.PostCommentService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/post/comment")
@Validated
public class PostCommentController {
    private final PostCommentService postCommentService;

    public PostCommentController(PostCommentService postCommentService) {
        this.postCommentService = postCommentService;
    }

    @GetMapping
    public Mono<ResponseResult> findCommentByPostId(@RequestParam ObjectId postId,
                                                    @RequestParam int page,
                                                    @RequestParam int size,
                                                    @Token ObjectId userId) {
        return postCommentService.findCommentByPostId(postId, page, size, userId);
    }

    @GetMapping("/all")
    public Mono<ResponseResult> findAllPostComment(@RequestParam ObjectId postId,
                                                   @RequestParam int page,
                                                   @RequestParam int size) {
        return postCommentService.findAllPostCommentByPostIdAndStatus(postId, page, size);
    }

    @PutMapping("/reply")
    Mono<ResponseResult> replyComment(ObjectId postId,
                                      ObjectId replyUserId,
                                      ObjectId replyId,
                                      @NotBlank
                                      @Size(min = 1, max = 400) String content,
                                      @Token ObjectId userId) {
        return postCommentService.replyComment(postId, replyUserId, replyId, content, userId);
    }

    @DeleteMapping
    Mono<ResponseResult> deleteComment(ObjectId commentId, @Token ObjectId userId) {
        return postCommentService.deleteComment(commentId, userId);
    }

    @PutMapping("/thumb")
    Mono<ResponseResult> thumb(ObjectId commentId, @Token ObjectId userId) {
        return postCommentService.thumb(commentId, userId);
    }

    @DeleteMapping("/thumb")
    Mono<ResponseResult> cancelThumb(ObjectId commentId, @Token ObjectId userId) {
        return postCommentService.cancelThumb(commentId, userId);
    }


}
