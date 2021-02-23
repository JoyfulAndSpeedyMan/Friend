package top.pin90.server.dao.user;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import top.pin90.common.po.user.UserFriendSetting;

public interface UserFriendSettingRepository extends ReactiveMongoRepository<UserFriendSetting, ObjectId> {
    Mono<UserFriendSetting> findByUserId(ObjectId userId);
}
