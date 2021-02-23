package top.pin90.friend.chatserver.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.netty.channel.ChannelHandlerContext;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import top.pin90.common.exception.auth.UserVerifyException;
import top.pin90.common.po.user.User;
import top.pin90.common.unti.JwtUtils;
import top.pin90.friend.chatserver.dao.UserRepository;
import top.pin90.friend.chatserver.protocol.req.ReqProto;
import top.pin90.friend.chatserver.protocol.res.ResProto;
import top.pin90.friend.chatserver.protocol.res.ResResult;
import top.pin90.friend.chatserver.protocol.res.ResType;
import top.pin90.friend.chatserver.service.RuntimeData;
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
    public void login(ChannelHandlerContext ctx, ReqProto.BaseReq req) {
        Mono<ObjectId> idMono = Mono.fromSupplier(() -> {
            String accessToken = req.getContent();
            DecodedJWT decodedJWT = jwtUtils.parseToken(accessToken);
            return jwtUtils.getUserId(decodedJWT);
        }).subscribeOn(Schedulers.parallel());

        idMono.zipWhen(userRepository::findById)
                .publishOn(Schedulers.parallel())
                .map(tuple2->{
                    ObjectId id = tuple2.getT1();
                    User user = tuple2.getT2();
                    RuntimeData.put(id,RuntimeData.buildCache(id,user.getPhone(),ctx.channel()));
                    RuntimeData.put(ctx.channel(),id);
                    return ResProto.BaseRes.newBuilder()
                                .setResult(ResResult.OK)
                                .build();
                })
                .defaultIfEmpty(ResProto.BaseRes.newBuilder()
                        .setResult(ResResult.USER_NOT_EXIST)
                        .build()
                )
                .onErrorResume(UserVerifyException.class,e-> Mono.fromSupplier(()->ResProto.BaseRes.newBuilder()
                        .setResult(ResResult.USER_VERITY_ERROR)
                        .setType(ResType.TEXT)
                        .setContent(e.getMessage())
                        .build()
                ))
                .subscribe(ctx::writeAndFlush);
    }

    @Override
    public void logout(ChannelHandlerContext ctx) {
        RuntimeData.logout(ctx.channel());
    }
}
