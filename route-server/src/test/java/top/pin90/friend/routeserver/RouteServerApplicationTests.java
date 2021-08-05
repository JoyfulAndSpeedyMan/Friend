package top.pin90.friend.routeserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.pin90.friend.routeserver.api.service.ClusterService;

@SpringBootTest
class RouteServerApplicationTests {
    @Autowired
    ClusterService clusterService;
    @Test
    void contextLoads() {
        Mono.fromFuture(clusterService.getAllChatServer())
                .flatMapMany(Flux::fromIterable)
                .subscribe(System.out::println);
    }

}
