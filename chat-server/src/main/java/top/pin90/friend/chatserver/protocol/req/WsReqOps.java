package top.pin90.friend.chatserver.protocol.req;

import top.pin90.friend.chatserver.protocol.WsOps;

public class WsReqOps extends WsOps {
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
