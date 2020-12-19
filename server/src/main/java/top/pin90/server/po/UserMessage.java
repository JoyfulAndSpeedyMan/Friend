package top.pin90.server.po;

import top.pin90.common.annotation.Describe;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * 用户发送的聊天消息
 */
@Data
public class UserMessage {
    @Id
    private String id;
    @Describe(value = "聊天会话id",assocClass = ChatSession.class)
    private String chatSessionId;
    @Describe("消息状态")
    private Integer status;
    private Date createTime;

    /**
     * 消息状态类
     */
    public static class MessageStatus{
        public static final int OK=1;
        @Describe("撤回")
        public static final int REVOCATION=2;
    }

}
