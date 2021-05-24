package top.pin90.friend.chatserver.protocol.req;

import top.pin90.friend.chatserver.protocol.WsOps;

public interface WsReqOps extends WsOps {
    /**
     * 登录
     */
    int LOGIN = 5;
    /**
     * 退出
     */
    int LOGOUT = 6;

    /**
     * 发送消息
     */
    int SEND_CHAT_MSG = 10;


}
