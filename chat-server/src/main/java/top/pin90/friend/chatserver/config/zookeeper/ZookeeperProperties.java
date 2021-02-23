package top.pin90.friend.chatserver.config.zookeeper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("zookeeper")
@Data
public class ZookeeperProperties {
    private String connectString;
}
