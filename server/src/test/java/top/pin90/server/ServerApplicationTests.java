package top.pin90.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.ObjectOperators;
import org.springframework.data.mongodb.core.aggregation.ObjectOperators.MergeObjects;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.pin90.server.po.User;


import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.ArrayElemAt.arrayOf;
import static org.springframework.data.mongodb.core.aggregation.ObjectOperators.MergeObjects.merge;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@SpringBootTest
class ServerApplicationTests {
    @Autowired
    ReactiveMongoTemplate template;
    @Test
    void contextLoads() {
    }

    @Test
    void testQuery(){
        template.query(User.class)
                .matching(query(where("id").is(0)))
                .all()
                .doOnNext(u -> {
                    System.out.println(u);
                }).subscribe();


    }
    @Test
    void testAggregation(){
        TypedAggregation<User> aggregation = newAggregation(User.class
                ,match(where("id").is(0))
                ,lookup("UserRole","role","_id","ARR")
                ,replaceRoot().withValueOf(merge(ROOT,arrayOf("ARR").elementAt(0)))
                ,project().andExclude("role","ARR")
        );
        Flux<Map> user = template.aggregate(aggregation, Map.class);

        user.doOnNext(System.out::println).subscribe();
    }

    @Test
    void testInsert(){
        User user = new User();
        user.setNickname("哈哈");
        Mono<User> insert = template.insert(user);
        User block = insert.block();
    }
}
