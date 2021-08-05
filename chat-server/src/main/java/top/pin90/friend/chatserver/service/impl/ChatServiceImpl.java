package top.pin90.friend.chatserver.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import top.pin90.common.po.chat.*;
import top.pin90.common.unti.Sets;
import top.pin90.common.unti.function.MyTuple2;
import top.pin90.friend.chatserver.dao.ChatSessionMessageRepository;
import top.pin90.friend.chatserver.dao.ChatSessionRepository;
import top.pin90.friend.chatserver.dao.UserChatSessionRepository;
import top.pin90.friend.chatserver.protocol.MsgUtils;
import top.pin90.friend.chatserver.protocol.WSMsg;
import top.pin90.friend.chatserver.protocol.res.ResTarget;
import top.pin90.friend.chatserver.protocol.res.WsResOps;
import top.pin90.friend.chatserver.server.RuntimeData;
import top.pin90.friend.chatserver.service.ChatService;

import java.util.Arrays;
import java.util.Date;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ChatSessionMessageRepository chatSessionMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final UserChatSessionRepository userChatSessionRepository;
    private final ObjectMapper objectMapper;
    public ChatServiceImpl(ChatSessionMessageRepository chatSessionMessageRepository,
                           ChatSessionRepository chatSessionRepository,
                           UserChatSessionRepository userChatSessionRepository,
                           ObjectMapper objectMapper) {
        this.chatSessionMessageRepository = chatSessionMessageRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.userChatSessionRepository = userChatSessionRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendChatMsg(ChannelHandlerContext ctx, WSMsg.Msg req) {
        Mono.create(sink -> {
            RuntimeData.UserInfoCache cacheInfo = RuntimeData.getCacheInfo(ctx.channel());
            ObjectId friendId = new ObjectId(req.getTarget());

            Channel channel = RuntimeData.getChannel(friendId);
            if (channel != null && channel.isActive()) {
                saveMessage(req, true, cacheInfo, friendId)
                        .subscribe(msg -> {
                            ObjectId csId = msg.getT1();
                            ChatSessionMessage chatSessionMessage = msg.getT2();
                            WSMsg.Msg push = WSMsg.Msg.newBuilder()
                                    .setOps(WsResOps.PUSH)
                                    .setTarget(ResTarget.PUSH_CHAT_MSG)
                                    .setContent(ObjectToJson(chatSessionMessage))
                                    .setExtend(csId.toString())
                                    .build();
                            channel.writeAndFlush(push)
                                    .addListener(t -> {
                                        if (t.isSuccess()) {
                                            sink.success(buildResProto(req, msg.getT2()));
                                            log.info("send msg: user {} to {} ", RuntimeData.getUserId(ctx.channel()), friendId);
                                        } else {
                                        }
                                    });
//                            sink.success(buildResProto(req, msg));
                        });

            } else {
                saveMessage(req, false, cacheInfo, friendId)
                        .subscribe(msg -> {
                            sink.success(buildResProto(req, msg.getT2()));
                        });
                ;
                log.info("send msg: user {} to {} ,bug user {} not on line  ",
                        RuntimeData.getUserId(ctx.channel()), friendId, friendId);
            }
        })
                .switchIfEmpty(Mono.fromSupplier(MsgUtils::serverError))
                .subscribeOn(Schedulers.parallel())
                .subscribe(ctx::writeAndFlush);
    }

    private WSMsg.Msg buildResProto(WSMsg.Msg req, ChatSessionMessage msg) {
        return MsgUtils.okCode()
                .setOps(WsResOps.RESPONSE)
                .setTarget(ResTarget.SEND_CHAT_MSG)
                .setMsgId(req.getMsgId())
                .setContent(ObjectToJson(msg))
                .build();
    }

    private Mono<MyTuple2<ObjectId, ChatSessionMessage>> saveMessage(WSMsg.Msg req, boolean pushed, RuntimeData.UserInfoCache cacheInfo, ObjectId friendId) {
        Date now = new Date();
        MyTuple2<ObjectId, ChatSessionMessage> tuple2 = new MyTuple2<>();
        return userChatSessionRepository.findFirstByUserIdAndTid(friendId, cacheInfo.getId())
                .subscribeOn(Schedulers.parallel())
                // 不是第一次聊天
                .flatMap(uc -> {
                    uc.setUnread(uc.getUnread() + 1);
                    tuple2.t1 = uc.getId();
                    return userChatSessionRepository.save(uc);
                })
                .flatMap(uc -> {
                    ChatSessionMessage chatSessionMessage = ChatSessionMessage.builder()
                            .status(ChatSessionMessage.MessageStatus.NORMAL)
                            .promulgator(cacheInfo.getId())
                            .createTime(now)
                            .content(req.getContent())
                            .push(pushed)
                            .chatSessionId(uc.getSessionId())
                            .type(MessageType.TEXT)
                            .build();
                    tuple2.t2= chatSessionMessage;
                    return chatSessionMessageRepository.save(chatSessionMessage);
                })
                .map(cs -> {
                    tuple2.t2 = cs;
                    return tuple2;
                })
                // 第一次发起聊天
                .switchIfEmpty(Mono.defer(() -> {
                    ChatSession.UserSessionInfo userSessionInfo1 = new ChatSession.UserSessionInfo(cacheInfo.getId(), now);
                    ChatSession.UserSessionInfo userSessionInfo2 = new ChatSession.UserSessionInfo(friendId, now);
                    ChatSession chatSession = ChatSession.builder()
                            .createTime(now)
                            .sessionType(SessionType.USER_CHAT)
                            .sessionInfos(Sets.of(userSessionInfo1, userSessionInfo2))
                            .build();

                    return chatSessionRepository.save(chatSession)
                            .zipWhen(cs -> {
                                ChatSessionMessage chatSessionMessage = ChatSessionMessage.builder()
                                        .chatSessionId(cs.getId())
                                        .content(req.getContent())
                                        .createTime(now)
                                        .status(ChatSessionMessage.MessageStatus.NORMAL)
                                        .type(MessageType.TEXT)
                                        .promulgator(cacheInfo.getId())
                                        .push(pushed)
                                        .build();
                                return chatSessionMessageRepository.save(chatSessionMessage);
                            })
                            .zipWhen(t -> {
                                UserChatSession userChatSession = UserChatSession.builder()
                                        .createTime(now)
                                        .sessionType(SessionType.USER_CHAT)
                                        .userId(cacheInfo.getId())
                                        .tid(friendId)
                                        .unread(0)
                                        .sessionId(t.getT1().getId())
                                        .build();
                                UserChatSession friendChatSession = UserChatSession.builder()
                                        .createTime(now)
                                        .sessionType(SessionType.USER_CHAT)
                                        .userId(friendId)
                                        .tid(cacheInfo.getId())
                                        .unread(1)
                                        .sessionId(t.getT1().getId())
                                        .build();
                                return userChatSessionRepository
                                        .saveAll(Arrays.asList(userChatSession, friendChatSession))
                                        .collectList();
                            })
                            .map(t -> {
                                tuple2.t1 = t.getT2().get(0).getId();
                                tuple2.t2 = t.getT1().getT2();
                                return tuple2;
                            });
                }));
    }

    private String ObjectToJson(Object o){
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
