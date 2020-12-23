package top.pin90.server.dao;

import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.pin90.server.po.Post;
import top.pin90.server.po.Status;

import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.IfNull.*;
import static org.springframework.data.mongodb.core.query.Criteria.*;

public interface PostRepository extends ReactiveSortingRepository<Post, ObjectId> {

    Flux<Post> findAllByStatus(String status,Pageable pageable);

    Mono<Post> findByIdAndStatus(ObjectId id, String status);

    Mono<Long> countPostByForwardUidAndForwardPid(ObjectId forwardUid,ObjectId forwardPid);

    default Mono<UpdateResult> deleteById(ReactiveMongoTemplate template, ObjectId id, ObjectId userId) {
        final Criteria criteria = where("_id").is(id)
                .and("userId").is(userId)
                .and("status").is(Status.NORMAL);
        final Update update = new Update()
                .set("status", Status.DELETE);
        return template.updateFirst(new Query(criteria), update, Post.class);
    }

    default Flux<Map> findByUserIdAgg(ReactiveMongoTemplate template, ObjectId userId, long skip, int limit) {
        return Flux.defer(()->{
            final Aggregation aggregation = newAggregation(
                    match(where("userId").is(userId)),
                    lookup("Post", "forwardId", "_id", "array"),
                    unwind("array", true),
                    lookup("User", "forwardUid", "_id", "user"),
                    unwind("user", true),
                    project("userId", "forwardPid", "forwardUid", "thumb", "comment", "forward", "createTime", "updateTime")
                            .and("content").applyCondition(ifNull("content").thenValueOf("array.content"))
                            .and("user.nickname").as("nickname"),
                    skip(skip),
                    limit(limit)
            );
            return template.aggregate(aggregation, Post.class, Map.class);
        });


    }

    default Mono<UpdateResult> incThumb(ReactiveMongoTemplate template, ObjectId postId) {
        final Query query = getQuery(postId);
        final Update update = new Update()
                .inc("thumb", 1);
        return template.updateFirst(query, update, Post.class);
    }

    default Mono<UpdateResult> decThumb(ReactiveMongoTemplate template, ObjectId postId) {
        final Query query = getQuery(postId);
        final Update update = new Update()
                .inc("thumb", -1);
        return template.updateFirst(query, update, Post.class);
    }

    default Mono<UpdateResult> incComment(ReactiveMongoTemplate template, ObjectId postId) {

        final Query query = getQuery(postId);
        final Update update = new Update()
                .inc("comment", 1);
        return template.updateFirst(query, update, Post.class);
    }

    default Mono<UpdateResult> decComment(ReactiveMongoTemplate template, ObjectId postId) {
        final Query query = getQuery(postId);
        final Update update = new Update()
                .inc("comment", 1);
        return template.updateFirst(query, update, Post.class);
    }

    default Mono<UpdateResult> incForward(ReactiveMongoTemplate template, ObjectId postId) {
        final Query query = getQuery(postId);
        final Update update = new Update()
                .inc("forward", 1);

        return template.updateFirst(query, update, Post.class);
    }

    default Query getQuery(ObjectId postId) {
        final Criteria criteria = where("_id").is(postId);
        return new Query(criteria);
    }
}
