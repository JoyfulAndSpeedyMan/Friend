package top.pin90.friend.chatserver.service;

import org.springframework.beans.factory.annotation.Value;
import top.pin90.friend.chatserver.api.service.MessageService;

/**
 * 写错了，暂时无用
 */
//@DubboService
public class MessageServiceImpl implements MessageService {
    @Value("${id}")
    private String id;

    @Override
    public String test(String s) {
        return String.format("id:%-3s%s",id,s);
    }
}
