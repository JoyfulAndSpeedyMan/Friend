package top.pin90.server.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import top.pin90.common.pojo.Code;
import top.pin90.common.pojo.Page;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.server.dao.post.PostCommentRepository;
import top.pin90.server.dao.post.PostRepository;
import top.pin90.server.dao.post.PostThumbRepository;
import top.pin90.common.po.post.Post;
import top.pin90.common.po.post.PostComment;
import top.pin90.common.po.post.PostThumb;
import top.pin90.common.pojo.Status;
import top.pin90.server.service.PostService;

import java.util.Date;
import java.util.Map;

@Service
public class PostServiceImpl implements PostService {
    final private PostRepository postRepository;
    final private PostThumbRepository postThumbRepository;
    final private PostCommentRepository postCommentRepository;
    final private ReactiveMongoTemplate template;

    public PostServiceImpl(PostRepository postRepository, PostThumbRepository postThumbRepository, PostCommentRepository postCommentRepository, ReactiveMongoTemplate template) {
        this.postRepository = postRepository;
        this.postThumbRepository = postThumbRepository;
        this.postCommentRepository = postCommentRepository;
        this.template = template;
    }

    @Override
    public Mono<ResponseResult> findByUserId(ObjectId userId, int page, int size) {
        final Flux<Map> postFlux = postRepository.findByUserIdAgg(template,userId, (long) page *size,size);
        final Mono<Long> longMono = postRepository.countByUserIdAndStatus(userId, Status.NORMAL);
        return Page.from(postFlux,longMono,page,size)
                .map(ResponseResult::ok);
    }

    @Override
    public Mono<ResponseResult> savePost(String content, ObjectId userId) {
        return Mono.fromSupplier(() -> {
            final Date date = new Date();
            return Post.builder()
                    .userId(userId)
                    .content(content)
                    .status(Status.NORMAL)
                    .thumb(0)
                    .comment(0)
                    .forward(0)
                    .createTime(date)
                    .updateTime(date)
                    .build();
        })
                .flatMap(postRepository::save)
                .map(post -> ResponseResult.ok("????????????", post.getId()));
    }

    @Override
    public Mono<ResponseResult> deletePostById(ObjectId postId, ObjectId userId) {
        final Mono<UpdateResult> updateResultMono = postRepository.deleteById(template,postId, userId);
        return updateResultMono
                .map(result -> result.wasAcknowledged()?
                        ResponseResult.ok(postId) : ResponseResult.of(Code.CLIENT_ERROR, "???????????????"));
    }

    @Override
    public Mono<ResponseResult> thumb(ObjectId postId, ObjectId userId) {
        final Mono<PostThumb> postThumbMono = postThumbRepository.findFirstByPostIdAndUserId(postId, userId);
        final Mono<ResponseResult> resultMono = postRepository.incThumb(template, postId)
                .zipWith(Mono.defer(() -> {
                    final Date date = new Date();
                    final PostThumb postThumb = PostThumb.builder()
                            .postId(postId)
                            .userId(userId)
                            .createTime(date)
                            .build();
                    return postThumbRepository.save(postThumb);
                }))
                .map(tuple2 -> {
                    final UpdateResult t1 = tuple2.getT1();
                    final PostThumb t2 = tuple2.getT2();
                    if (t1.wasAcknowledged())
                        return ResponseResult.ok("????????????", postId);
                    else
                        return ResponseResult.of(Code.PARAM_ERROR, "????????????");
                });
        return postThumbMono
                .map(postThumb -> ResponseResult.ok(Code.CLIENT_ERROR, "?????????????????????"))
                .switchIfEmpty(resultMono);

    }

    @Override
    public Mono<ResponseResult> cancelThumb(ObjectId postId, ObjectId userId) {
        final Mono<PostThumb> postThumbMono = postThumbRepository.findFirstByPostIdAndUserId(postId, userId);
        // ????????????
        final Mono<Tuple2<DeleteResult, UpdateResult>> tuple2Mono = postThumbRepository.deleteThumb(template, postId, userId)
                .zipWith(postRepository.decThumb(template, postId));
        return postThumbMono
                .flatMap(postThumb -> tuple2Mono)
                .map(tuple2 -> {

                    if (tuple2.getT2().wasAcknowledged() && tuple2.getT1().wasAcknowledged())
                        return ResponseResult.ok("????????????", postId);
                    else
                        return ResponseResult.of(Code.PARAM_ERROR, "????????????");
                })
                .switchIfEmpty(ResponseResult.toMono(Code.CLIENT_ERROR, "??????????????????"));
    }

    @Override
    public Mono<ResponseResult> comment(ObjectId postId, String content, ObjectId userId) {
        final Mono<PostComment> postCommentMono = Mono.fromSupplier(() -> {
            final Date date = new Date();
            return PostComment.builder()
                    .userId(userId)
                    .postId(postId)
                    .replyId(null)
                    .content(content)
                    .status(Status.NORMAL)
                    .thumb(0)
                    .createTime(date)
                    .build();
        });
        return postCommentMono
                .flatMap(postCommentRepository::save)
                .zipWith(postRepository.incComment(template, postId))
                .map(tuple2 -> ResponseResult.ok("????????????", tuple2.getT2().getUpsertedId()))
                .defaultIfEmpty(ResponseResult.of(Code.PARAM_ERROR, "????????????"));
    }

    @Override
    public Mono<ResponseResult> forward(ObjectId postId, ObjectId userId) {

        return postRepository.findByIdAndStatus(postId,Status.NORMAL)

                .zipWhen(post -> {
//                    Post post=tuple2.getT1();
                    final Date date = new Date();
                    final Post.PostBuilder builder = Post.builder()
                            .userId(userId)
                            .thumb(0)
                            .comment(0)
                            .forward(0)
                            .createTime(date)
                            .updateTime(date);
                    if (post.getForwardPid() != null)
                        builder
                                .forwardUid(post.getForwardUid())
                                .forwardPid(post.getForwardPid());
                    else
                        builder
                                .forwardUid(post.getUserId())
                                .forwardPid(postId);
                    final Post post1 = builder.build();

                    return postRepository.save(post1);
                })
                .zipWith(postRepository.countPostByForwardUidAndForwardPid(postId,userId))
                .flatMap(tuple -> {
                    if(tuple.getT2()>0)
                        return Mono.just(true);
                    final Post post = tuple.getT1().getT1();
                    // ??????????????????id
                    final ObjectId sid = post.getId();
                    // ????????????id
                    final ObjectId rid = post.getForwardPid();
                    // ??????sid??????????????????
                    final Mono<UpdateResult> incForward = postRepository.incForward(template, sid);
                    if(rid!=null)
                        // ????????????rid??????????????????
                        return incForward
                                .zipWith(postRepository.incForward(template, rid))
                                .map(tuple2->{
                                    if(tuple2.getT1().wasAcknowledged() && tuple2.getT2().wasAcknowledged())
                                        return true;
                                    return false;
                                });
                   return incForward.map(UpdateResult::wasAcknowledged);
                })
                .map(b -> b?
                        ResponseResult.ok("????????????"):
                        ResponseResult.of(Code.CLIENT_ERROR,"????????????"))
                .defaultIfEmpty(ResponseResult.of(Code.PARAM_ERROR, "???????????????"));


    }


}
