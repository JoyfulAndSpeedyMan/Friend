package top.pin90.friend.chatserver.protocol;

public interface WsOps {
    int PING=1;
    int PONG=2;
    /**
     * 其他操作，需要设置target指定具体的操作
     */
   int OTHER = 15;
}
