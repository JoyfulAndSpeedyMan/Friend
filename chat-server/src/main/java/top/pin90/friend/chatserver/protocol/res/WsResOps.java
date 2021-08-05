package top.pin90.friend.chatserver.protocol.res;

import top.pin90.friend.chatserver.protocol.WsOps;

public interface WsResOps extends WsOps {
    /**
     * 推送
     */
    int PUSH=20;
    /**
     * 响应
     */
    int RESPONSE=25;

}
