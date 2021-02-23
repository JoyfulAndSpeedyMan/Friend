package top.pin90.friend.chatserver.protocol.req;

public class ReqOps {
    public static final int PING=1;
    public static final int PONG=2;
    /**
     * 登录
     */
    public static final int LOGIN=5;
    /**
     *  退出
     */
    public static final int LOGOUT=6;

    /**
     * 发送消息
     */
    public static final int SEND_CHAT_MSG=10;

}
