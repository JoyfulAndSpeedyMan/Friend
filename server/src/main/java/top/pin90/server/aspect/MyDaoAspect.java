package top.pin90.server.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class MyDaoAspect {

//    @Around("top.pin90.server.aspect.CommonPointcuts.myDao()")
//    public Object  subscribeOn(ProceedingJoinPoint joinPoint) throws Throwable {
//        Signature signature = joinPoint.getSignature();
//        Class declaringType = signature.getDeclaringType();
//        int modifiers = signature.getModifiers();
//        String declaringTypeName = signature.getDeclaringTypeName();
//        String name = signature.getName();
//        Object result = joinPoint.proceed();
//        return result;
//    }
}
