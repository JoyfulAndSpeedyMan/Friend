package top.pin90.server.controller;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import top.pin90.common.exception.auth.UserTokenExpireException;
import top.pin90.common.exception.auth.UserVerifyException;
import top.pin90.common.pojo.Code;
import top.pin90.common.pojo.ResponseResult;

import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.*;

@RestControllerAdvice
public class ExceptionHandlers {
    /**
     * 参数校验出错
     * @param e
     * @return
     */
    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseResult handleBindException(ConstraintViolationException e) {
        TreeMap<String,String> map= new TreeMap<>();
        e.getConstraintViolations().forEach(ele -> {
            final Path propertyPath = ele.getPropertyPath();
            if(propertyPath instanceof PathImpl) {
                map.put(((PathImpl) propertyPath).getLeafNode().getName(),ele.getMessage());
            }
        });
        return ResponseResult.of(Code.PARAM_ERROR, "参数错误",map);
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseResult handleIllegalArgumentException(IllegalArgumentException e){
        e.printStackTrace();
        return ResponseResult.of(Code.PARAM_ERROR,e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseResult handleBindException(WebExchangeBindException e) {
        TreeMap<String,String> map= new TreeMap<>();
        final List<FieldError> fieldErrors = e.getFieldErrors();
        fieldErrors.forEach(fr->map.put(fr.getField(),fr.getDefaultMessage()));
        return ResponseResult.of(Code.PARAM_ERROR, "参数错误",map);
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseResult handleBindException(UserVerifyException e) {
        if(e instanceof UserTokenExpireException)
            return ResponseResult.of(Code.USER_VERITY_EXPIRE,e.getMessage());
        return ResponseResult.of(Code.USER_VERITY_ERROR, e.getMessage());
    }
}
