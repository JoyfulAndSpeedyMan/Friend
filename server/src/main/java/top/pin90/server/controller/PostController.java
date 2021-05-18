package top.pin90.server.controller;

import org.bson.types.ObjectId;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import top.pin90.common.annotation.FormData;
import top.pin90.common.annotation.Token;
import top.pin90.common.pojo.Code;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.server.service.PostCommentService;
import top.pin90.server.service.PostService;
import top.pin90.server.utils.PageUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/post")
@Validated
public class PostController {
    private final PostService postService;
    private final PostCommentService postCommentService;
    public PostController(PostService postService, PostCommentService postCommentService) {
        this.postService = postService;
        this.postCommentService = postCommentService;
    }
    @ExceptionHandler
    public Mono<ResponseResult> handNumberFormatException(NumberFormatException e){
        return ResponseResult.toMono(Code.PARAM_ERROR,"参数错误");
    }

    @GetMapping
    Mono<ResponseResult> findAll(@RequestParam int page,@RequestParam int size){
        page=PageUtils.pageLimit(page);
        size=PageUtils.sizeLimit(size);
        return postService.findAll(page, size);
    }

    @GetMapping("/user")
    Mono<ResponseResult> findByUserId(@Token ObjectId userId,@RequestParam int page,@RequestParam int size){
        page=PageUtils.pageLimit(page);
        size=PageUtils.sizeLimit(size);
        return postService.findByUserId(userId, page, size);
    }
    @PutMapping
    public Mono<ResponseResult> savePost(
            @FormData @NotNull(message = "内容不能为空") @Size(min = 3, max = 1000, message = "长度不合适") String content,
            @Token ObjectId userId) {
        return postService.savePost(content, userId);
    }

    @DeleteMapping
    public Mono<ResponseResult> deletePost(@FormData  ObjectId postId, @Token ObjectId userId) {
        return postService.deletePostById(postId, userId);
    }

    @PutMapping("/thumb")
    public Mono<ResponseResult> thumb(ObjectId postId, @Token ObjectId userId) {
        return postService.thumb(postId, userId);
    }

    @DeleteMapping("/thumb")
    public Mono<ResponseResult> cancelThumb( ObjectId postId, @Token ObjectId userId) {
        return postService.cancelThumb(postId, userId);
    }

    @PutMapping("/comment")
    public Mono<ResponseResult> comment(ObjectId postId,
                                        @NotNull @Size(min = 1, max = 200) String content,
                                        @Token ObjectId userId) {
        return postService.comment(postId, content, userId);
    }

    @PutMapping("/forward")
    public Mono<ResponseResult> forward(ObjectId postId,@Token ObjectId userId) {
        return postService.forward(postId, userId);
    }


}
