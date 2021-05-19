package top.pin90.server.service.impl;

import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.pin90.common.dao.BsonManager;
import top.pin90.common.po.post.PostComment;
import top.pin90.common.po.post.PostCommentThumb;
import top.pin90.common.pojo.Code;
import top.pin90.common.pojo.Page;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.common.pojo.Status;
import top.pin90.server.dao.post.PostCommentDao;
import top.pin90.server.dao.post.PostCommentRepository;
import top.pin90.server.dao.post.PostCommentThumbRepository;
import top.pin90.server.dao.post.PostRepository;
import top.pin90.server.service.PostCommentService;

import java.util.Date;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static top.pin90.server.utils.PageUtils.pageLimit;
import static top.pin90.server.utils.PageUtils.sizeLimit;

@Service
public class PostCommentServiceImpl implements PostCommentService {
    final private PostCommentRepository postCommentRepository;
    final private PostCommentThumbRepository thumbRepository;
    final private PostRepository postRepository;
    final private PostCommentDao postCommentDao;
    final private ReactiveMongoTemplate template;
    final private BsonManager bsonManager;
    public PostCommentServiceImpl(PostCommentRepository postCommentRepository,
                                  PostCommentThumbRepository thumbRepository,
                                  PostRepository postRepository,
                                  PostCommentDao postCommentDao, ReactiveMongoTemplate template, BsonManager bsonManager) {
        this.postCommentRepository = postCommentRepository;
        this.thumbRepository = thumbRepository;
        this.postRepository = postRepository;
        this.postCommentDao = postCommentDao;
        this.template = template;
        this.bsonManager = bsonManager;
    }


    @Override
    public Mono<ResponseResult> publishComment(ObjectId userId, ObjectId postId, String content) {
        PostComment comment = PostComment.builder()
                .userId(userId)
                .postId(postId)
                .content(content)
                .thumb(0)
                .status(Status.NORMAL)
                .createTime(new Date())
                .build();
        postCommentRepository.save(comment);
        return ResponseResult.monoOk("评论成功");
    }

    @Override
    public Mono<ResponseResult> findCommentByPostId(ObjectId postId, int page, int size,ObjectId userId) {
        int page1 = pageLimit(page);
        int size1 = sizeLimit(size);
        final Flux<Map> comment =
                postCommentDao.findPostComment(
                        postId, userId,Status.NORMAL, page1, size1);
        final Mono<Long> longMono = postCommentRepository.countByPostIdAndStatus(postId, Status.NORMAL);
        return Page.from(comment, longMono, page, size)
                .map(ResponseResult::ok);
    }

    @Override
    public Mono<ResponseResult> findAllPostCommentByPostIdAndStatus(ObjectId postId, int page, int size) {
        int page1 = pageLimit(page);
        int size1 = sizeLimit(size);
        final Flux<Map> comment =
                postCommentDao.findAllPostCommentByPostIdAndStatus(
                        postId, Status.NORMAL, page1, size1);
        final Mono<Long> longMono = postCommentRepository.countByPostIdAndStatus(postId, Status.NORMAL);
        return Page.from(comment, longMono, page, size)
                .map(ResponseResult::ok);
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
                            .status(Status.NORMAL)
                            .createTime(date)
                            .build();
                    return postComment;
                })
                .flatMap(postCommentRepository::save)
                .flatMap(p->postRepository.incComment(template,postId))
                .flatMap(ResponseResult::monoOk)
                .switchIfEmpty(ResponseResult.toMono(Code.PARAM_ERROR, "评论或帖子不存在"));
    }

    @Override
    public Mono<ResponseResult> deleteComment(ObjectId commentId, ObjectId userId) {
        return postCommentRepository.deleteByIdAndUserId(commentId, userId)
                .flatMap(v -> ResponseResult.monoOk("删除成功", commentId))
                .switchIfEmpty(ResponseResult.toMono(Code.CLIENT_ERROR, "评论不存在"));
    }

    @Override
    public Mono<ResponseResult> thumb(ObjectId commentId, ObjectId userId) {
        // 具体的操作
        final Mono<ResponseResult> defer = Mono.defer(() -> {
            // 评论点赞数+1
            final Query query = new Query(where("_id").is(commentId));
            final Mono<UpdateResult> updateResultMono = template.updateFirst(query,
                    new Update().inc("thumb", 1),
                    PostComment.class);
            return updateResultMono
                    .zipWith(Mono.defer(() -> {
                        // 保存点赞记录
                        final PostCommentThumb commentThumb = PostCommentThumb.builder()
                                .postCommentId(commentId)
                                .userId(userId)
                                .createTime(new Date())
                                .build();
                        return thumbRepository.save(commentThumb);
                    }))
                    // 判断结果
                    .flatMap(tuple -> {
                        if (tuple.getT1().getModifiedCount() == 1)
                            return ResponseResult.monoOk("点赞成功");
                        return ResponseResult.toMono(Code.SERVER_EXE_ERROR, "点赞失败");
                    })
                    .switchIfEmpty(ResponseResult.toMono(Code.SERVER_EXE_ERROR, "点赞失败"));
        });
        // 查看用户是否已经点过赞了
        return thumbRepository.findByPostCommentIdAndUserId(commentId, userId)
                .flatMap(comment -> ResponseResult.toMono(Code.OPERATION_ERROR, "已经点过赞了"))
                // 执行逻辑
                .switchIfEmpty(defer);
    }

    @Override
    public Mono<ResponseResult> cancelThumb(ObjectId commentId, ObjectId userId) {
        // 查看用户是否已经点过赞了
        return thumbRepository.findByPostCommentIdAndUserId(commentId, userId)
                .flatMap(thumb -> {
                    // 点赞数-1
                    final Query query = new Query(where("_id").is(commentId));
                    final Mono<UpdateResult> updateResultMono = template.updateFirst(query,
                            new Update().inc("thumb", -1),
                            PostComment.class);
                    return updateResultMono
                            .zipWith(thumbRepository.deleteByPostCommentIdAndUserId(commentId, userId))
                            // 判断结果
                            .flatMap(tuple -> {
                                if (tuple.getT1().getModifiedCount() == 1 && tuple.getT2() == 1)
                                    return ResponseResult.monoOk("取消成功");
                                return ResponseResult.toMono(Code.SERVER_EXE_ERROR, "点赞失败");
                            });
                })
                // 执行逻辑
                .switchIfEmpty(ResponseResult.toMono(Code.OPERATION_ERROR, "你还没有点赞"));
    }
}
