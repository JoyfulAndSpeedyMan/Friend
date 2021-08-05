package top.pin90.server.config.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.pin90.common.unti.JwtUtils;
import top.pin90.common.unti.SmsUtils;

@Configuration
public class JWTBeanConfig {
    private final JWTConfig jwtConfig;

    public JWTBeanConfig(JWTConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Bean
    public SmsUtils smsUtils(){
        return new SmsUtils();
    }
    @Bean
    public JwtUtils jwtUtils(){
        return new JwtUtils(jwtConfig.getSecret(),jwtConfig.getUserIdKey(),jwtConfig.getIss());
    }
}
