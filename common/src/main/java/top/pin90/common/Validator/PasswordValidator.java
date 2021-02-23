package top.pin90.common.Validator;

import top.pin90.common.annotation.verification.Password;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password,String> {
    private Password constraint;
    @Override
    public void initialize(Password constraintAnnotation) {
        this.constraint =constraintAnnotation;
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if(StringUtils.hasText(password) && password.length()>2)
            return true;
        return false;
    }
}
