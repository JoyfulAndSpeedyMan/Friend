package top.pin90.common.unti;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class SmsUtils {

    public void sendRegisterSmsCode(String phone, String code){
        log.info("Register user,send code: {} to {}",code,phone);

    }
    public void sendLoginSmsCode(String phone, String code){
        log.info("Login user,send code: {} to {}",code,phone);

    }

}
