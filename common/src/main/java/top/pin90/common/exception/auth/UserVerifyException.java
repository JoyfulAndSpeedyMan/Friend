package top.pin90.common.exception.auth;

public class UserVerifyException extends RuntimeException{
    public UserVerifyException(String message) {
        super(message);
    }
    public UserVerifyException(String message,Throwable e) {
        super(message,e);
    }
}
