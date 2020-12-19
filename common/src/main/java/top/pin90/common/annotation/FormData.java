package top.pin90.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FormData {
    String value() default "";
}
