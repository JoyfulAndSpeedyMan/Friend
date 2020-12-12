package top.pin90.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import top.pin90.server.po.User;

import java.util.List;

@Configuration
public class TestConfig {
    @Autowired
    public void testFind(ReactiveMongoTemplate template){



    }

}
