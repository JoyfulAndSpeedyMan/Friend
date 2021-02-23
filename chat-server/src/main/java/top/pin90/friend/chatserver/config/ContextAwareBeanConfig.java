package top.pin90.friend.chatserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.pin90.common.unti.spring.SpringBeanFactory;

@Configuration
public class ContextAwareBeanConfig {
    @Bean
    public SpringBeanFactory springBeanFactory(){
        return new SpringBeanFactory();
    }
}
