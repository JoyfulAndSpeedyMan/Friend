package top.pin90.server.config.user;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("user.login.code")
@Data
public class UserLoginCodeConfig {
    /**
     * code type
     */
    private String codeType="code6";
    /**
     * valid time
     */
    private int validTime=60;
}
