package top.pin90.friend.chatserver.listener;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import top.pin90.common.pojo.info.ChatServerInfo;
import top.pin90.common.unti.zookeeper.ChatServerZkUtils;

import java.util.concurrent.ExecutionException;
@Slf4j
public class ChatServerStartListener implements GenericFutureListener<ChannelFuture> {
    private final String host;
    private final int port;
    private final ChatServerZkUtils zkUtils;
    private final ChatServerInfo chatServerInfo;
    private Thread updateInfo;
    public ChatServerStartListener(String host, int port, ChatServerZkUtils zkUtils, ChatServerInfo chatServerInfo) {
        this.host = host;
        this.port = port;
        this.zkUtils = zkUtils;
        this.chatServerInfo = chatServerInfo;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
            log.info("Start chat server o port {} success!!!", port);
        }

        zkUtils.register(chatServerInfo)
                .doOnError(e->log.error("",e))
                .subscribe(name->{
                    log.info("register {} to zookeeper success",name);
                });
        if (future.isDone() && !future.isSuccess()) {
            try {
                future.get();
            } catch (ExecutionException e) {
                log.error("", e);
            }
            System.exit(-1);
        }

        updateInfo = new Thread(()->{
            while (true){
                zkUtils.update(chatServerInfo);
                try {
                    Thread.sleep(10*1000);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        },"updateInfo");
        updateInfo.start();
    }
}
