package top.pin90.friend.chatserver.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.pin90.friend.chatserver.OtherOpsRouter;
import top.pin90.friend.chatserver.protocol.WSMsg;
import top.pin90.friend.chatserver.protocol.WsOps;
import top.pin90.friend.chatserver.protocol.req.WsReqOps;
import top.pin90.friend.chatserver.protocol.res.WsResCode;
import top.pin90.friend.chatserver.protocol.res.WsResOps;
import top.pin90.friend.chatserver.service.ChatService;
import top.pin90.friend.chatserver.service.UserService;

import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static top.pin90.friend.chatserver.server.RuntimeData.channelMapId;

@Slf4j
public class MyHandler extends SimpleChannelInboundHandler<WSMsg.Msg> {

    // 已连接，但是还未进行身份验证任务队列队列
    private final static DelayQueue<ConnectNotLoginTask> delayQueue = new DelayQueue<>();
    private final UserService userService;
    private final ChatService chatService;
    public MyHandler(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
    }

    private static class ConnectNotLoginTask implements Delayed {

        private final Channel channel;
        private final long trigger;
        private final long enterTime = System.currentTimeMillis();

        ConnectNotLoginTask(Channel channel, long trigger) {
            this.channel = channel;
            this.trigger = trigger + enterTime;

        }

        public Channel getChannel() {
            return channel;
        }

        @Override
        public int compareTo(Delayed o) {
            ConnectNotLoginTask that = (ConnectNotLoginTask) o;
            if (trigger < that.trigger) return -1;
            if (trigger > that.trigger) return 1;
            return 0;

        }


        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(trigger - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }
    }

    @PostConstruct
    void init() {
        Thread clearNotLogin = new Thread(() -> {
            while (true) {
                try {
                    ConnectNotLoginTask take = delayQueue.take();
                    if (!channelMapId.containsKey(take.getChannel())) {
                        Channel channel = take.getChannel();
                        log.info("close channel {} ,because it not login", channel.id());
                        channel.close();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "clearNotLogin");
        clearNotLogin.start();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WSMsg.Msg req) throws Exception {
        Channel channel = ctx.channel();
        int ops = req.getOps();
        switch (ops) {
            case WsOps.PING:
                WSMsg.Msg res = WSMsg.Msg
                        .newBuilder()
                        .setCode(WsResCode.OK)
                        .setOps(WsResOps.PONG)
                        .setContent(req.getMsg())
                        .build();
                ctx.writeAndFlush(res);
                log.debug("PING , channel {}", channel.id());
                break;
            case WsOps.PONG:
                log.debug("PONG , channel {}", channel.id());
                break;
            case WsReqOps.LOGIN:
                userService.login(ctx, req);
                log.debug("LOGIN , user {}", req.getContent());
                break;
            case WsReqOps.LOGOUT:
                userService.logout(ctx);
                log.debug("LOGOUT , user {}", "phone");
                break;
            case WsReqOps.SEND_CHAT_MSG:
                chatService.sendChatMsg(ctx,req);
                log.debug("SEND_CHAT_MSG , {} to {}", "phone", "phone");
                break;
            case WsOps.OTHER:
                log.debug("OTHER , target: {}", req.getTarget());
                OtherOpsRouter.route(ctx, req);
                break;
            default:
                log.debug("UNKNOWN ops");
        }
        System.out.println(req);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Channel channel = ctx.channel();
        delayQueue.offer(new ConnectNotLoginTask(channel, 5L * 1000));
        log.debug("channel {} connected", channel.id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Channel channel = ctx.channel();
        userService.logout(ctx);

        log.debug("channel {} disconnect , login status : {}", channel.id(), channelMapId.containsKey(channel));
    }

}

