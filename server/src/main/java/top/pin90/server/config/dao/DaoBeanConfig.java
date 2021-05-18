package top.pin90.server.config.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.pin90.common.dao.BsonManager;
import top.pin90.common.dao.CachedFileBsonManager;

@Configuration
public class DaoBeanConfig {
    @Bean
    public BsonManager bsonManager(){
        return new CachedFileBsonManager("bson");
    }
}
