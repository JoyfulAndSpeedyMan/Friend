package top.pin90.friend.chatserver.listener;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import top.pin90.common.unti.zookeeper.ChatServerZkUtils;

@Slf4j
public class ChatServerStopListener implements GenericFutureListener<ChannelFuture> {
    private final ChatServerZkUtils zkUtils;

    public ChatServerStopListener(String host, int port, ChatServerZkUtils zkUtils) {
        this.zkUtils = zkUtils;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {

    }
}
