package top.pin90.common.exception.auth;

public class UserTokenExpireException extends UserVerifyException{

    public UserTokenExpireException(String message) {
        super(message);
    }

    public UserTokenExpireException(String message, Throwable e) {
        super(message, e);
    }
}
