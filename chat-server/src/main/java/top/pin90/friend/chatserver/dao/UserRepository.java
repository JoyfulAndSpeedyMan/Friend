package top.pin90.friend.chatserver.dao;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;
import top.pin90.common.po.user.User;

public interface UserRepository extends ReactiveSortingRepository<User, ObjectId> {

    Mono<User> findFirstByPhone(String phone);

    @Query(value = "{_id: ObjectId('?0')}",
            fields =  "{_id: 0,phone: 1,nickname: 1,avatar: 1,status: 1,createTime: 1}")
    Mono<User> getBaseInfoById(ObjectId id);
}
