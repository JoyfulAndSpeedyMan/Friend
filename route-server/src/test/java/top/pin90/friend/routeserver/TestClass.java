package top.pin90.friend.routeserver;

import org.junit.Test;
import top.pin90.friend.chatserver.api.service.MessageService;

public class TestClass {
    @Test
    public void test(){
        String name = MessageService.class.getName();
        System.out.println(name);
    }
}
