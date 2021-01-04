package top.pin90.server.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import top.pin90.server.chat.handler.ChatServerInitializer;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

/**
 * 聊天服务器
 */
@Slf4j
public class ChatServer {
    private int port;
    private final String osName;
    public ChatServer(int port, String osName) {
        this.port = port;
        this.osName = osName;
    }

    /**
     * 启动初始化
     * @throws InterruptedException
     */
    @PostConstruct
    public void start() throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        Class<? extends ServerChannel> serverChannelClass;
        if(osName.toLowerCase().contains("linux")) {
            serverChannelClass = EpollServerSocketChannel.class;
            log.info("Use EpollServerSocketChannel");
        }
        else {
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

        ChannelFuture future = bootstrap.bind().sync();
        if (future.isSuccess()) {
            log.info("On port {},start chat server success!!!",port);
        }
    }
}
