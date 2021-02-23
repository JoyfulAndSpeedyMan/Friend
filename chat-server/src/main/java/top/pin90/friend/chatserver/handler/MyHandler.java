package top.pin90.friend.chatserver.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.pin90.friend.chatserver.protocol.req.ReqOps;
import top.pin90.friend.chatserver.protocol.req.ReqProto;
import top.pin90.friend.chatserver.protocol.res.ResOps;
import top.pin90.friend.chatserver.protocol.res.ResProto;
import top.pin90.friend.chatserver.protocol.res.ResResult;
import top.pin90.friend.chatserver.service.UserService;

import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static top.pin90.friend.chatserver.service.RuntimeData.channelMapId;
@Slf4j
public class MyHandler extends SimpleChannelInboundHandler<ReqProto.BaseReq> {

    // 已连接，但是还未进行身份验证任务队列队列
    private final static DelayQueue<ConnectNotLoginTask> delayQueue = new DelayQueue<>();
    private final UserService userService;

    public MyHandler(UserService userService) {
        this.userService = userService;
    }

    private static class ConnectNotLoginTask implements Delayed {

        private final Channel channel;
        private final long trigger;

        ConnectNotLoginTask(Channel channel, long trigger) {
            this.channel = channel;
            this.trigger = trigger;
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
            return unit.convert(trigger, TimeUnit.NANOSECONDS);
        }
    }

    @PostConstruct
    void init() {
        Thread clearNotLogin = new Thread(() -> {
            while (true) {
                try {
                    ConnectNotLoginTask take = delayQueue.take();
                    Channel channel = take.getChannel();
                    if (!channelMapId.containsKey(channel)) {
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
    protected void channelRead0(ChannelHandlerContext ctx, ReqProto.BaseReq req) throws Exception {
        Channel channel = ctx.channel();
        int ops = req.getOps();
        switch (ops) {
            case ReqOps.PING:
                ResProto.BaseRes res = ResProto.BaseRes
                        .newBuilder()
                        .setResult(ResResult.OK)
                        .setOps(ResOps.PONG)
                        .build();
                ctx.writeAndFlush(res);
                log.debug("PING , channel {}", channel.id());
                break;
            case ReqOps.PONG:
                log.debug("PONG , channel {}", channel.id());
                break;
            case ReqOps.LOGIN:
                userService.login(ctx, req);
                log.debug("LOGIN , user {}", req.getContent());
                break;
            case ReqOps.LOGOUT:
                log.debug("LOGOUT , user {}", "phone");
                break;
            case ReqOps.SEND_CHAT_MSG:
                log.debug("SEND_CHAT_MSG , {} to {}", "phone", "phone");
                break;
            default:

        }
        System.out.println(req);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Channel channel = ctx.channel();
        delayQueue.offer(new ConnectNotLoginTask(channel, 5L * 1000 * 1000 * 1000));
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

