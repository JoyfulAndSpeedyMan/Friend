package top.pin90.friend.chatserver.service;

import io.netty.channel.ChannelHandlerContext;
import top.pin90.friend.chatserver.protocol.WSMsg;

public interface UserService {
    void login(ChannelHandlerContext ctx, WSMsg.Msg req);

    void logout(ChannelHandlerContext ctx);
}
