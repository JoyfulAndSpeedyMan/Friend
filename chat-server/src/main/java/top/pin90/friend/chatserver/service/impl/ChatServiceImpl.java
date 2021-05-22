package top.pin90.friend.chatserver.service.impl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import top.pin90.friend.chatserver.protocol.MsgUtils;
import top.pin90.friend.chatserver.protocol.WSMsg;
import top.pin90.friend.chatserver.protocol.res.WsResCode;
import top.pin90.friend.chatserver.server.RuntimeData;
import top.pin90.friend.chatserver.service.ChatService;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {
    @Override
    public void sendChatMsg(ChannelHandlerContext ctx, WSMsg.Msg req) {
        Mono.create(sink -> {
            ObjectId userId = new ObjectId(req.getTarget());
            Channel channel = RuntimeData.getChannel(userId);
            if (channel != null && channel.isActive()) {
                WSMsg.Msg push = WSMsg.Msg.newBuilder()
                        .setCode(WsResCode.OK)
                        .setContent(req.getContent())
                        .build();
                channel.writeAndFlush(push)
                        .addListener(t -> {
                            if (t.isSuccess()) {
                                log.info("send msg: user {} to {} ", RuntimeData.getUserId(ctx.channel()), userId);
                                sink.success(MsgUtils.okCode());
                            }
                        });

            } else {
                log.info("send msg: user {} to {} ,bug user {} not on line  ",
                        RuntimeData.getUserId(ctx.channel()), userId , userId);
            }
        })
                .switchIfEmpty(Mono.fromSupplier(MsgUtils::serverError))
                .subscribeOn(Schedulers.parallel())
                .subscribe(ctx::writeAndFlush);
    }


}
