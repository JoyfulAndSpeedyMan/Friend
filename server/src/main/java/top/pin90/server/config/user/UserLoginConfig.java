package top.pin90.server.config.user;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("user.login")
@Data
public class UserLoginConfig {
    /**
     * AccessToken valid time
     */
    private int accessValidTime;
    /**
     * RefreshToken valid time
     */
    private int refreshValidTime;

    /**
     * login code prefix
     */
    private String loginCacheKey = "login-";
}
