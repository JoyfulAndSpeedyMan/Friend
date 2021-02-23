package top.pin90.common.annotation.verification;

import top.pin90.common.Validator.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordValidator.class)
public @interface Password {
    String value() default "user";

    String message() default "格式错误";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
