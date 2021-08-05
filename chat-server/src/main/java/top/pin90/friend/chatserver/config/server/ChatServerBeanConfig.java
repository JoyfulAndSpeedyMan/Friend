package top.pin90.friend.chatserver.config.server;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import top.pin90.common.pojo.info.ChatServerInfo;
import top.pin90.friend.chatserver.server.ChatServer;
import top.pin90.friend.chatserver.listener.ChatServerStartListener;
import top.pin90.friend.chatserver.listener.ChatServerStopListener;
import top.pin90.common.unti.zookeeper.ChatServerZkUtils;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@Slf4j
public class ChatServerBeanConfig {
    private final ChatServerConfig chatServerConfig;
    private String host = "";
    private int port;

    public ChatServerBeanConfig(ChatServerConfig chatServerConfig) {
        this.chatServerConfig = chatServerConfig;
    }

    @PostConstruct
    public void init() {
        try {
            host = chatServerConfig.getHost() != null ? chatServerConfig.getHost() : InetAddress.getLocalHost().getHostAddress();
            port = chatServerConfig.getPort();
        } catch (UnknownHostException e) {
            log.error("", e);
            System.exit(-1);
        }

    }

    @Bean
    @DependsOn("reactiveStringRedisTemplate")
    public ChatServer chatServer(@Value("${os.name}") String osName,
                                 @Qualifier("startListener") @Autowired GenericFutureListener<ChannelFuture> startListener,
                                 @Qualifier("stopListener") @Autowired GenericFutureListener<ChannelFuture> stopListener) {
        return new ChatServer(host, port, osName, startListener, stopListener);
    }
    @Bean
    public ChatServerInfo chatServerInfo(){
        String wsPath="ws://"+host+":"+port+"/ws";
        return new ChatServerInfo(host,port,wsPath);
    }

    @Bean
    @Autowired
    public GenericFutureListener<ChannelFuture> startListener(ChatServerZkUtils chatServerZkUtils,ChatServerInfo chatServerInfo) {
        return new ChatServerStartListener(host, port, chatServerZkUtils, chatServerInfo);
    }

    @Bean
    @Autowired
    public GenericFutureListener<ChannelFuture> stopListener(ChatServerZkUtils chatServerZkUtils) {
        return new ChatServerStopListener(host, port, chatServerZkUtils);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
