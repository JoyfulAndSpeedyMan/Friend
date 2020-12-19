package top.pin90.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.pin90.common.unti.JwtUtils;
import top.pin90.common.unti.SmsUtils;

@Configuration
public class BeanConfig {
    @Bean
    public SmsUtils smsUtils(){
        return new SmsUtils();
    }
    @Bean
    public JwtUtils jwtUtils(
            @Value("${auth.jwt.secret}") String SECRET
            ,@Value("#{${auth.jwt.calendarInterval}}") int calendarInterval
            ,@Value("${auth.jwt.userIdKey}") String USER_ID_KET
            ,@Value("${auth.jwt.iss}") String ISS){
        return new JwtUtils(SECRET,calendarInterval,USER_ID_KET,ISS);
    }
}
