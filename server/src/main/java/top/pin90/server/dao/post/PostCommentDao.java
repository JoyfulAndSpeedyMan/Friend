package top.pin90.server.dao.post;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import top.pin90.common.dao.BsonManager;
import top.pin90.common.po.post.PostComment;
import top.pin90.server.dao.JsonOperation;

import java.util.Map;

//@Repository
@Component
public class PostCommentDao {
    private final ReactiveMongoTemplate template;
    private final BsonManager bsonManager;

    public PostCommentDao(ReactiveMongoTemplate template, BsonManager bsonManager) {
        this.template = template;
        this.bsonManager = bsonManager;
    }

    public Flux<Map> findPostComment(ObjectId postId, ObjectId userId, String status, int page, int size) {
        String bson = bsonManager.getBsonWithVar("postComment/findPostComment", postId, status, userId, page, size);
        JsonOperation operation = new JsonOperation(bson);
        return template.aggregate(Aggregation.newAggregation(operation), PostComment.class, Map.class);
    }

    public Flux<Map> findAllPostCommentByPostIdAndStatus(ObjectId postId, String status,
                                                         int page, int size) {
        String bson = bsonManager.getBsonWithVar("postComment/findPostComment", postId, status, page, size);
        JsonOperation operation = new JsonOperation(bson);
        return template.aggregate(Aggregation.newAggregation(operation), PostComment.class, Map.class);
    }


}
