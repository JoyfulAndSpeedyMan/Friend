package top.pin90.server.config.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("auth.jwt")
@Data
public class JWTConfig {
    /**
     * The issuance of the secret key
     */
    private String secret;
    /**
     * Key of user ID after signing
     */
    private String userIdKey;
    /**
     * The issuer of the token
     */
    private String iss;

}
