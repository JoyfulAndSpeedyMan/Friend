package top.pin90.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Token {
    String value() default "userId";
    boolean required() default true;
}
