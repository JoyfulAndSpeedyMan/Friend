package top.pin90.common.po.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 用户到目标用户或群的会话信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("UserChatSession")
@TypeAlias("UserChatSession")
public class UserChatSession {
    @Id
    private ObjectId id;
    /**
     * 用户的id
     */
    private ObjectId userId;
    /**
     * 目标用户Id
     */
    private ObjectId tid;
    /**
     * 未读消息数量
     */
    private Integer unread;
    /**
     * 是否是群组会话
     */
    private String sessionType;
    /**
     * 会话的Id
     */
    private ObjectId sessionId;

    private Date createTime;
}
