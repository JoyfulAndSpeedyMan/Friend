package top.pin90.friend.chatserver.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.netty.channel.ChannelHandlerContext;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import top.pin90.common.exception.auth.UserVerifyException;
import top.pin90.common.unti.JwtUtils;
import top.pin90.friend.chatserver.dao.UserRepository;
import top.pin90.friend.chatserver.protocol.WSMsg;
import top.pin90.friend.chatserver.protocol.res.WsResCode;
import top.pin90.friend.chatserver.server.RuntimeData;
import top.pin90.friend.chatserver.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public UserServiceImpl(JwtUtils jwtUtils, UserRepository userRepository) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @Override
    public void login(ChannelHandlerContext ctx, WSMsg.Msg req) {
        String accessToken = req.getContent();
        DecodedJWT decodedJWT = jwtUtils.parseToken(accessToken);
        ObjectId userId = jwtUtils.getUserId(decodedJWT);

        userRepository.findById(userId)
                .map(user->{
                    if(!ctx.channel().isActive()) {
                        return WSMsg.Msg.newBuilder()
                                .setCode(WsResCode.CLIENT_ERROR)
                                .setMsg("连接已断开")
                                .build();
                    }
                    RuntimeData.put(userId, RuntimeData.buildCache(userId, user.getPhone(), ctx.channel()));
                    RuntimeData.put(ctx.channel(), userId);
                    return WSMsg.Msg.newBuilder()
                                .setCode(WsResCode.OK)
                                .build();
                })
                .defaultIfEmpty(WSMsg.Msg.newBuilder()
                        .setCode(WsResCode.USER_NOT_EXIST)
                        .setMsg("用户不存在")
                        .build()
                )
                .onErrorResume(UserVerifyException.class,e-> Mono.fromSupplier(()->WSMsg.Msg.newBuilder()
                        .setCode(WsResCode.USER_VERITY_ERROR)
                        .build()
                ))
                .subscribe(ctx::writeAndFlush);
    }

    @Override
    public void logout(ChannelHandlerContext ctx) {
        RuntimeData.logout(ctx.channel());
    }
}
