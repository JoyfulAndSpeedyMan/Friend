package top.pin90.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@Configuration
public class TestConfig {
    @Autowired
    public void testFind(ReactiveMongoTemplate template){



    }

}
