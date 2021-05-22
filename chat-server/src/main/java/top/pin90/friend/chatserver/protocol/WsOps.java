package top.pin90.friend.chatserver.protocol;

public class WsOps {
    public static final int PING=1;
    public static final int PONG=2;
    /**
     * 其他操作，需要设置target指定具体的操作
     */
    public static final int OTHER = 15;
}
