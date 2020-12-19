package top.pin90.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import top.pin90.common.unti.JwtUtils;
import top.pin90.server.argument.resolver.FormDataArgumentResolver;
import top.pin90.server.argument.resolver.UserIdArgumentResolver;

@Configuration
public class WebConfig implements WebFluxConfigurer {
    private final String TOKEN_KEY ="token";
    private final JwtUtils jwtUtils;

    @Autowired
    public WebConfig(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new UserIdArgumentResolver(jwtUtils, TOKEN_KEY));
        configurer.addCustomResolver(new FormDataArgumentResolver());
    }
}
