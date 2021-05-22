package top.pin90.friend.chatserver.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import top.pin90.common.unti.spring.SpringBeanFactory;
import top.pin90.friend.chatserver.protocol.WSMsg;
import top.pin90.friend.chatserver.service.ChatService;
import top.pin90.friend.chatserver.service.UserService;

public class ChatServerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast(new HttpServerCodec())
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpObjectAggregator(64*1024))
                .addLast(new WebSocketServerProtocolHandler("/ws"))
                // byteToWebSocketFrame
                .addLast(new WebFrameHandler())
                // Protobuf
                .addLast(new ProtobufDecoder(WSMsg.Msg.getDefaultInstance()))
                .addLast(new ProtobufEncoder());

        MyHandler myHandler = new MyHandler(
                SpringBeanFactory.getBean(UserService.class),
                SpringBeanFactory.getBean(ChatService.class));
        myHandler.init();
        ch.pipeline().addLast(myHandler);
    }
}
