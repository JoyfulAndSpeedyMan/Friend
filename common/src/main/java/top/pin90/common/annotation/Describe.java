package top.pin90.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE,ElementType.TYPE,ElementType.FIELD,ElementType.METHOD,ElementType.CONSTRUCTOR,ElementType.PARAMETER,ElementType.TYPE_PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Describe {
    String value();
    Class<?> assocClass() default Void.class;
    String assocFiled() default "id";
}
