package top.pin90.friend.chatserver.service;

import io.netty.channel.ChannelHandlerContext;
import top.pin90.friend.chatserver.protocol.req.ReqProto;

public interface UserService {
    void login(ChannelHandlerContext ctx, ReqProto.BaseReq req);

    void logout(ChannelHandlerContext ctx);
}
