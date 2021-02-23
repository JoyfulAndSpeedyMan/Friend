package top.pin90.server.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class MonoAspect {
   /*
    @Around("top.pin90.server.aspect.CommonPointcuts.monoService()")
    public Mono<?>  subscribeOn(ProceedingJoinPoint joinPoint) throws Throwable {
        Mono<?> retVal = (Mono<?>) joinPoint.proceed();
        return retVal.subscribeOn(Schedulers.parallel());
    }*/
}
