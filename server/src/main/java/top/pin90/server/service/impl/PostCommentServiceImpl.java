package top.pin90.server.service.impl;

import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.pin90.common.pojo.Code;
import top.pin90.common.pojo.Page;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.server.dao.post.PostCommentRepository;
import top.pin90.server.dao.post.PostCommentThumbRepository;
import top.pin90.server.dao.post.PostRepository;
import top.pin90.common.po.post.PostComment;
import top.pin90.common.po.post.PostCommentThumb;
import top.pin90.common.pojo.Status;
import top.pin90.server.service.PostCommentService;

import java.util.Date;

import static org.springframework.data.mongodb.core.query.Criteria.*;
import static top.pin90.server.utils.PageUtils.pageLimit;
import static top.pin90.server.utils.PageUtils.sizeLimit;

@Service
public class PostCommentServiceImpl implements PostCommentService {
    final private PostCommentRepository postCommentRepository;
    final private PostCommentThumbRepository thumbRepository;
    final private PostRepository postRepository;
    final private ReactiveMongoTemplate template;

    public PostCommentServiceImpl(PostCommentRepository postCommentRepository, PostCommentThumbRepository thumbRepository, PostRepository postRepository, ReactiveMongoTemplate template) {
        this.postCommentRepository = postCommentRepository;
        this.thumbRepository = thumbRepository;
        this.postRepository = postRepository;
        this.template = template;
    }


    @Override
    public Mono<ResponseResult> findCommentByPostId(ObjectId postId, int page, int size) {
        int page1= pageLimit(page);
        int size1= sizeLimit(size);
        final Flux<PostComment> comment = postCommentRepository.findPostCommentByPostIdAndStatus(postId, Status.NORMAL, PageRequest.of(page1, size1));
        final Mono<Long> longMono = postCommentRepository.countByPostIdAndStatus(postId, Status.NORMAL);
        return Page.from(comment, longMono, page, size)
                .map(pageResult -> {
                    if (pageResult.empty())
                        return ResponseResult.of(Code.CLIENT_ERROR, "???????????????");
                    else
                        return ResponseResult.ok(pageResult);
                });
    }

    @Override
    public Mono<ResponseResult> replyComment(ObjectId postId, ObjectId replyUserId, ObjectId replyId, String content, ObjectId userId) {
        return postRepository.findById(postId)
                .zipWith(postCommentRepository.findById(replyId))
                .map(tuple -> {
                    final Date date = new Date();
                    final PostComment postComment = PostComment.builder()
                            .userId(userId)
                            .postId(postId)
                            .replyUserId(replyUserId)
                            .replyId(replyId)
                            .content(content)
                            .createTime(date)
                            .build();
                    return postComment;
                })
                .flatMap(postCommentRepository::save)
                .flatMap(ResponseResult::monoOk)
                .switchIfEmpty(ResponseResult.toMono(Code.PARAM_ERROR, "???????????????"));
    }

    @Override
    public Mono<ResponseResult> deleteComment(ObjectId commentId, ObjectId userId) {
        return postCommentRepository.deleteByIdAndUserId(commentId, userId)
                .flatMap(v -> ResponseResult.monoOk("????????????", commentId))
                .switchIfEmpty(ResponseResult.toMono(Code.CLIENT_ERROR, "???????????????"));
    }

    @Override
    public Mono<ResponseResult> thumb(ObjectId commentId, ObjectId userId) {
        // ???????????????
        final Mono<ResponseResult> defer = Mono.defer(() -> {
            // ???????????????+1
            final Query query = new Query(where("_id").is(commentId));
            final Mono<UpdateResult> updateResultMono = template.updateFirst(query,
                    new Update().inc("thumb", 1),
                    PostComment.class);
            return updateResultMono
                    .zipWith(Mono.defer(() -> {
                        // ??????????????????
                        final PostCommentThumb commentThumb = PostCommentThumb.builder()
                                .postCommentId(commentId)
                                .userId(userId)
                                .createTime(new Date())
                                .build();
                        return thumbRepository.save(commentThumb);
                    }))
                    // ????????????
                    .flatMap(tuple -> {
                        if (tuple.getT1().getModifiedCount() == 1)
                            return ResponseResult.monoOk("????????????");
                        return ResponseResult.toMono(Code.SERVER_EXE_ERROR, "????????????");
                    })
                    .switchIfEmpty(ResponseResult.toMono(Code.SERVER_EXE_ERROR, "????????????"));
        });
        // ????????????????????????????????????
        return thumbRepository.findByPostCommentIdAndUserId(commentId, userId)
                .flatMap(comment -> ResponseResult.toMono(Code.OPERATION_ERROR, "??????????????????"))
                // ????????????
                .switchIfEmpty(defer);
    }

    @Override
    public Mono<ResponseResult> cancelThumb(ObjectId commentId, ObjectId userId) {
        // ????????????????????????????????????
        return thumbRepository.findByPostCommentIdAndUserId(commentId, userId)
                .flatMap(thumb -> {
                    // ?????????-1
                    final Query query = new Query(where("_id").is(commentId));
                    final Mono<UpdateResult> updateResultMono = template.updateFirst(query,
                            new Update().inc("thumb", -1),
                            PostComment.class);
                    return updateResultMono
                            .zipWith(thumbRepository.deleteByPostCommentIdAndUserId(commentId, userId))
                            // ????????????
                            .flatMap(tuple -> {
                                if (tuple.getT1().getModifiedCount() == 1 && tuple.getT2() == 1)
                                    return ResponseResult.monoOk("????????????");
                                return ResponseResult.toMono(Code.SERVER_EXE_ERROR, "????????????");
                            });
                })
                // ????????????
                .switchIfEmpty(ResponseResult.toMono(Code.OPERATION_ERROR, "??????????????????"));
    }
}
