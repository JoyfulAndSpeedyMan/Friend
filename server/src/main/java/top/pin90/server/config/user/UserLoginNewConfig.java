package top.pin90.server.config.user;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("user.login.new")
@Data
public class UserLoginNewConfig {
    /**
     * new user default avatar
     */
    private String avatar;
}
