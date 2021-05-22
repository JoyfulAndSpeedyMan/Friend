package top.pin90.friend.chatserver.service;

import io.netty.channel.ChannelHandlerContext;
import top.pin90.friend.chatserver.protocol.WSMsg;

public interface ChatService {
    void sendChatMsg(ChannelHandlerContext ctx, WSMsg.Msg req);
}
