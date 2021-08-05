package top.pin90.server.dao.chat;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import top.pin90.common.po.chat.ChatSessionMessage;

public interface ChatSessionMessageRepository extends ReactiveMongoRepository<ChatSessionMessage, ObjectId> {
    @Query(value = "{chatSessionId:ObjectId('?0')}",
            fields = "{ _id: 1, promulgator: 1, content: 1, status: 1, createTime: 1 }",
            sort="{createTime: -1}"
    )
    Flux<ChatSessionMessage> loadMoreMessage(ObjectId id, Pageable pageable);

}
