package top.pin90.server.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class CommonPointcuts {
    @Pointcut("execution(public reactor.core.publisher.Mono top.pin90.server.service.*.*(..))")
    public void monoService(){}
    @Pointcut("execution(public reactor.core.publisher.Mono " +
            "top.pin90.server.argument.resolver.*."+
            "resolveArgument(" +
            "org.springframework.core.MethodParameter," +
            "org.springframework.web.reactive.BindingContext," +
            "org.springframework.web.server.ServerWebExchange))")
    public void argResolver(){}

    @Pointcut("@target(top.pin90.common.annotation.MyDao) || @annotation(top.pin90.common.annotation.MyDao)")
    public void myDao(){}
}
