package top.pin90.server.dao.user;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.pin90.common.po.user.UserFriendRelation;

public interface UserFriendRelationRepository extends ReactiveMongoRepository<UserFriendRelation, ObjectId> {
    /**
     * 查找用户关系
     * @param fuid 第一个用户Id
     * @param suid 第二个用户Id
     * @return
     */
    Mono<UserFriendRelation> findByFuidAndSuid(ObjectId fuid,ObjectId suid);


    /**
     * 获取好友请求
     * @param suid 被请求的用户
     * @param pageable 分页参数
     * @return 请求列表
     */
    Flux<UserFriendRelation> findBySuidAndStatus(ObjectId suid,String status, Pageable pageable);

    Flux<UserFriendRelation> findBySuidAndStatus(ObjectId suid,String status);

    /**
     * 获取请求总数量
     * @param suid
     * @return
     */
    Mono<Long> countBySuid(ObjectId suid);
}
