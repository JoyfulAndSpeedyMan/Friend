package top.pin90.friend.chatserver.config.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties("chat.server")
@Data
public class ChatServerConfig {
    private String host;
    @NotNull
    private int port;
}
