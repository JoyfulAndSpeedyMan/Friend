package top.pin90.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import top.pin90.common.unti.JwtUtils;
import top.pin90.server.argument.resolver.FormDataArgumentResolver;
import top.pin90.server.argument.resolver.UserIdArgumentResolver;

@Configuration
public class WebConfig implements WebFluxConfigurer {
    // 用户id在jwt中的key
    private final String USER_ID_KEY;
    // token 在 header中的key
    private final String TOKEN_KEY ;
    private final JwtUtils jwtUtils;
    private final ReactiveMongoTemplate template;
    @Autowired
    public WebConfig(
            @Value("${auth.jwt.userIdKey}") String user_id_key,
            @Value("${auth.token.key}") String token_key,
            JwtUtils jwtUtils, ReactiveMongoTemplate template) {
        this.USER_ID_KEY = user_id_key;
        this.TOKEN_KEY = token_key;
        this.jwtUtils = jwtUtils;
        this.template = template;
    }

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(userIdArgumentResolver());
        configurer.addCustomResolver(new FormDataArgumentResolver());
    }
    @Bean
    public UserIdArgumentResolver userIdArgumentResolver(){
        return new UserIdArgumentResolver(jwtUtils, USER_ID_KEY,TOKEN_KEY, template);
    }
}
