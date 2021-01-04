package top.pin90.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class ChatServerConfig {
    @Value("${chat.server.port}")
    private int port;
}
