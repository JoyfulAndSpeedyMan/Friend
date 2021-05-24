package top.pin90.server.dao.post;

import com.mongodb.client.result.DeleteResult;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import top.pin90.common.po.post.PostThumb;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public interface PostThumbRepository extends ReactiveMongoRepository<PostThumb,ObjectId> {
    Mono<PostThumb> findFirstByPostIdAndUserId(ObjectId postId, ObjectId userId);

    default Mono<DeleteResult> deleteThumb(ReactiveMongoTemplate template,ObjectId postId, ObjectId userId){
        final Query query = new Query(where("postId").is(postId).and("userId").is(userId));
        return template.remove(query,PostThumb.class);
    }
}
