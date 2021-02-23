package top.pin90.common.po.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * 聊天会话
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("ChatSession")
public class ChatSession {
    @Id
    private ObjectId id;
    /**
     * 是否是群会话
     */
    private Boolean group;
    /**
     * 会话成员信息
     */
    private List<UserSessionInfo> sessionInfos;
    /**
     * 会话创建的时间
     */
    private Date createTime;

    /**
     * 会话中的用户信息
     */
    @Data
    public static class UserSessionInfo{
        /**
         * 用户Id
         */
        private ObjectId userId;
        /**
         * 用户加入会话时间
         */
        private Date createTime;
    }
}
