package top.pin90.server.argument.resolver;

import com.auth0.jwt.interfaces.Claim;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.pin90.common.exception.auth.UserVerifyException;
import top.pin90.common.unti.JwtUtils;
import top.pin90.common.annotation.Token;

import java.util.List;
import java.util.Map;

public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtUtils jwtUtils;
    private final String tokenKey;
    public UserIdArgumentResolver(JwtUtils jwtUtils, String tokenKey) {
        this.jwtUtils = jwtUtils;
        this.tokenKey=tokenKey;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Token.class);
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
        return Mono.create(monoSink -> {
            final ServerHttpRequest request = exchange.getRequest();
            final HttpHeaders headers = request.getHeaders();
            final List<String> tokenList = headers.get(tokenKey);

            final Token tokenAnnotation = parameter.getParameterAnnotation(Token.class);
            if(tokenAnnotation.required()){
                if (tokenList.isEmpty())
                    monoSink.error(new UserVerifyException("身份验证失败"));
                else{
                    final String token = tokenList.get(0);
                    final Map<String, Claim> claimMap = jwtUtils.parseToken(token);
                    final Claim claim = claimMap.get(tokenAnnotation.value());
                    if (null == claim || !StringUtils.hasText(claim.asString())) {
                        if(tokenAnnotation.required())
                            monoSink.error(new UserVerifyException("身份验证失败"));
                        else
                            monoSink.success();
                        return;
                    }
                    monoSink.success(claim.asString());
                }
            }
            else {
                if (tokenList.isEmpty())
                    monoSink.success();
                else{
                    final String token = tokenList.get(0);
                    final String userId = jwtUtils.getUserId(token);
                    monoSink.success(userId);
                }
            }
        });


    }
}
