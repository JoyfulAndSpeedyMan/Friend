package top.pin90.server.dao.user;

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
    /*
    @Query("{nickname:?0}")
    Flux<User> findByJson(String nickname);
    @Query("{nickname:?#{[0]}}")
    Flux<User> findBySpEL(String nickname);
    @Query("{nickname:?#{[0]?'黑太狼':[1]}}")
    Flux<User> findBySpEL2(boolean f,String nickname);


    @Aggregation(
            {
                    "{\n" +
                    "    $lookup: {\n" +
                    "        from: 'UserRole',\n" +
                    "        localField: 'role',\n" +
                    "        foreignField: '_id',\n" +
                    "        as: 'arr'\n" +
                    "    }\n" +
                    "}",
                    " {\n" +
                            "    $replaceRoot: {\n" +
                            "        newRoot: {\n" +
                            "            $mergeObjects: [\n" +
                            "                \"$$ROOT\",\n" +
                            "                {\n" +
                            "                    $arrayElemAt: [\"$arr\", 0]\n" +
                            "                }\n" +
                            "            ]\n" +
                            "        }\n" +
                            "    }\n" +
                            "}",
                    "{\n" +
                            "    $project: {\n" +
                            "        nickname: 1,\n" +
                            "        name: 1,\n" +
                            "        permission: 1\n" +
                            "    }\n" +
                            "}"
            }
    )
    Flux<Map> aggregationUser();
    default Flux<Map> aggregationUserTemplate(ReactiveMongoTemplate template){
        org.springframework.data.mongodb.core.aggregation.Aggregation aggregation = org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation(
                lookup("UserRole","role","_id","arr"),
                replaceRoot().withValueOf(ObjectOperators.MergeObjects.merge(ArrayOperators.arrayOf("arr").elementAt(0),ROOT)),
                project("nickname","name")
        );
        return template.aggregate(aggregation,"User",Map.class);
    }
*/
}
