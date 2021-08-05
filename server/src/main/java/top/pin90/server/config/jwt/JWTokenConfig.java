package top.pin90.server.config.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("auth.token")
@Data
public class JWTokenConfig {
    /**
     * token of http header key
     */
    private String key;
    private String prefix;
}
