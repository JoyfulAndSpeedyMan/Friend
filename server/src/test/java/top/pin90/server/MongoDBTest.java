package top.pin90.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;
import top.pin90.common.pojo.Code;
import top.pin90.common.pojo.ResponseResult;
import top.pin90.server.dao.post.PostRepository;
import top.pin90.server.dao.user.UserRepository;
import top.pin90.common.po.user.User;

import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.*;
@SpringBootTest
public class MongoDBTest {
    @Autowired
    ReactiveMongoTemplate template;
    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Test
    void testQuery(){
        template.query(User.class)
                .matching(query(where("id").is(0)))
                .all()
                .doOnNext(System.out::println).subscribe();


    }
    @Test
    void testQuery2(){
        Mono.just("17854560896")
                .flatMap(p->userRepository.findFirstByPhone(p))
                .map(user -> ResponseResult.ok("成功",user))
                .defaultIfEmpty(ResponseResult.of(Code.SMS_CODE_ERROR,"错误"))
                .doOnNext(System.out::println)
                .subscribe();
    }
    @Test
    void testGetBaseInfoById(){

    }
/*
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
    */



/*
    @Test
    void repositoryJsonTest(){
        Flux<User> userFlux = userRepository.findByJson("灰太狼");
        userFlux.doOnNext(u-> System.out.println(u)).subscribe();
    }

    @Test
    void repositorySpELTest(){
        Flux<User> userFlux = userRepository.findBySpEL("灰太狼");
        userFlux.doOnNext(u-> System.out.println(u)).subscribe();
    }

    @Test
    void repositorySpEL2Test(){
        Flux<User> userFlux = userRepository.findBySpEL2(false,"灰太狼");
        userFlux.doOnNext(u-> System.out.println(u)).subscribe();
    }


    @Test
    void repositoryAggregationTest(){
        Flux<Map> userFlux = userRepository.aggregationUser();
        userFlux.doOnNext(u-> System.out.println(u)).subscribe();
    }

    @Test
    void repositoryAggregationTemplateTest(){
        Flux<Map> userFlux = userRepository.aggregationUserTemplate(template);
        userFlux.doOnNext(u-> System.out.println(u)).subscribe();
    }
    */

}
