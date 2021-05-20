package top.pin90.server.argument.resolver;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.logging.log4j.util.Strings;
import org.bson.types.ObjectId;
import org.springframework.core.MethodParameter;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import top.pin90.common.annotation.Token;
import top.pin90.common.exception.auth.UserVerifyException;
import top.pin90.common.po.user.User;
import top.pin90.common.po.user.UserStatus;
import top.pin90.common.unti.JwtUtils;
import top.pin90.server.config.jwt.JWTokenConfig;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtUtils jwtUtils;
    // 用户id在jwt中的key
    private final String USER_ID_KEY;
    // token 在 header中的key
    private final String tokenKey;
    private final JWTokenConfig jwTokenConfig;
    private final ReactiveMongoTemplate template;

    public UserIdArgumentResolver(JwtUtils jwtUtils, String user_id_key, String tokenKey, JWTokenConfig jwTokenConfig, ReactiveMongoTemplate template) {
        this.jwtUtils = jwtUtils;
        this.USER_ID_KEY = user_id_key;
        this.tokenKey = tokenKey;
        this.jwTokenConfig = jwTokenConfig;
        this.template = template;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Token.class);
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
        final Token tokenAnnotation = parameter.getParameterAnnotation(Token.class);
        final boolean required = tokenAnnotation.required();
        return Mono.<ObjectId>create(monoSink -> {
            final ServerHttpRequest request = exchange.getRequest();
            final HttpHeaders headers = request.getHeaders();
            final List<String> tokenList = headers.get(tokenKey);
            if (tokenList == null) {
                if (required) {
                    monoSink.error(new UserVerifyException("身份验证失败"));
                    return;
                }
                monoSink.success();
                return;
            }
            String key = tokenAnnotation.value();
            if (Strings.isBlank(key))
                key = USER_ID_KEY;

            if (required) {
                if (tokenList.isEmpty())
                    monoSink.error(new UserVerifyException("身份验证失败"));
                else {
                    String token = tokenList.get(0);
                    token = token.trim();
                    String prifix = jwTokenConfig.getPrefix();
                    int i = token.indexOf(prifix);
                    if (i == -1 || token.length() <= prifix.length()) {
                        monoSink.error(new UserVerifyException("身份验证失败"));
                        return;
                    }

                    token = token.substring(i + prifix.length());
                    DecodedJWT decodedJWT = jwtUtils.parseToken(token);
                    if (jwtUtils.isRefreshToken(decodedJWT)) {
                        monoSink.error(new UserVerifyException("身份验证失败"));
                        return;
                    }
                    final Claim claim = decodedJWT.getClaims().get(key);

                    if (null == claim || !StringUtils.hasText(claim.asString())) {
                        monoSink.error(new UserVerifyException("身份验证失败"));
                        return;
                    }

                    monoSink.success(new ObjectId(claim.asString()));
                }
            } else {
                if (tokenList.isEmpty())
                    monoSink.success();
                else {
                    String token = tokenList.get(0);
                    token = token.trim();
                    DecodedJWT decodedJWT = jwtUtils.parseToken(token);
                    if (jwtUtils.isRefreshToken(decodedJWT)) {
                        monoSink.success();
                        return;
                    }
                    final Claim claim = decodedJWT.getClaims().get(key);
                    if (null == claim || !StringUtils.hasText(claim.asString()))
                        monoSink.success();
                    else
                        monoSink.success(new ObjectId(claim.asString()));
                }
            }
        }).
                subscribeOn(Schedulers.parallel())
                .zipWhen(this::existUser)
                .map(tuple -> {
                    if (tuple.getT2()) {
                        return tuple.getT1();
                    }
                    throw new UserVerifyException("用户" + tuple.getT1() + "不存在");
                });
    }

    private Mono<Boolean> existUser(ObjectId userId) {
        final Query query = Query.query(where("id").is(userId).and("status").is(UserStatus.NORMAL));
        final Mono<Boolean> exists = template.exists(query, User.class);
        return exists;
    }
}
