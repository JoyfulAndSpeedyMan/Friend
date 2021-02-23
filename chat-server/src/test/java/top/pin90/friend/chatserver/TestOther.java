package top.pin90.friend.chatserver;

import org.junit.jupiter.api.Test;
import top.pin90.friend.chatserver.protocol.req.ReqOps;

public class TestOther {
    @Test
    public void testSwitch(){
        int ops=5;
        switch (ops){
            case ReqOps.PING:
                System.out.println("PING");
                break;
            case ReqOps.PONG:
                System.out.println("PONG");

                break;
            case ReqOps.LOGIN:
                System.out.println("LOGIN");
                break;
            default:
                System.out.println("default");

        }
    }
}
