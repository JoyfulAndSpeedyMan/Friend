package top.pin90.server.po.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import top.pin90.common.pojo.Status;

import java.util.Date;

/**
 * 聊天消息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("ChatSessionMessage")
public class ChatSessionMessage {
    @Id
    private ObjectId id;
    /**
     * 聊天会话id
     */
    private ObjectId chatSessionId;
    /**
     * 消息的类型（二进制数据用路径表示）
     */
    private String type;
    /**
     * 消息的内容人
     */
    private String content;
    /**
     * 消息状态
     */
    private String status;
    /**
     * 是否已推送
     */
    @Field("isPush")
    private Boolean push;
    private Date createTime;

    /**
     * 消息的状态
     */
    public static class MessageStatus extends Status {
        /**
         * 撤回状态
         */
        public static final String REVOCATION="REVOCATION";
    }

    /**
     * 消息的类型
     */
    public static class MessageType{
        /**
         * 纯文本类型
         */
        public static final String TEXT="TEXT";
        /**
         * 富文本类型
         */
        public static final String RICH_TEXT="RICH_TEXT";
        /**
         * 图片类型
         */
        public static final String PHOTO="PHOTO";
        /**
         * 语音类型
         */
        public static final String VOICE="VOICE";
        /**
         * 复合类型(富文本，图片)
         */
        public static final String COMPOUND="COMPOUND";

        /**
         * 其他类型
         */
        public static final String OTHER="OTHER";

    }
}
