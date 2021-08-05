package top.pin90.friend.chatserver.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import top.pin90.common.pojo.info.ServerInfo;
import top.pin90.friend.chatserver.handler.ChatServerInitializer;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

/**
 * 聊天服务器
 */
@Slf4j
public class ChatServer {
    // 服务器的运行状态
    public final ServerInfo serverInfo;

    private String host;
    private int port;
    private final String osName;
    private Channel serverChannel;
    private ReactiveHashOperations<String, Object, Object> opsForHash;
    private final GenericFutureListener<ChannelFuture> startListener;
    private final GenericFutureListener<ChannelFuture> stopListener;

    private String redisKey = "chat-server";
    private String thisRedisKey;

    public Channel getServerChannel() {
        return serverChannel;
    }

    public ChatServer(String host,
                      int port,
                      String osName,
                      GenericFutureListener<ChannelFuture> startListener, GenericFutureListener<ChannelFuture> stopListener) {
        this.host = host;
        this.port = port;
        this.osName = osName;
        this.startListener = startListener;
        this.stopListener = stopListener;
        serverInfo = new ServerInfo(host, port);
    }

    /**
     * 启动初始化
     *
     */
    @PostConstruct
    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        Class<? extends ServerChannel> serverChannelClass;
        if (osName.toLowerCase().contains("linux")) {
            serverChannelClass = EpollServerSocketChannel.class;
            log.info("Use EpollServerSocketChannel");
        } else {
            serverChannelClass = NioServerSocketChannel.class;
            log.info("Use NioServerSocketChannel");
        }
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(boss, work)
                .channel(serverChannelClass)
                .localAddress(new InetSocketAddress(port))
                //保持长连接
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChatServerInitializer());

        ChannelFuture addListener = bootstrap.bind().addListener(startListener);
        serverChannel = addListener.channel();
        serverChannel.closeFuture().addListener(stopListener);
    }

}
