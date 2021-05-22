package top.pin90.friend.chatserver;

import org.junit.jupiter.api.Test;
import top.pin90.friend.chatserver.protocol.req.WsReqOps;

public class TestOther {
    @Test
    public void testSwitch(){
        int ops=5;
        switch (ops){
            case WsReqOps.PING:
                System.out.println("PING");
                break;
            case WsReqOps.PONG:
                System.out.println("PONG");

                break;
            case WsReqOps.LOGIN:
                System.out.println("LOGIN");
                break;
            default:
                System.out.println("default");

        }
    }
}
