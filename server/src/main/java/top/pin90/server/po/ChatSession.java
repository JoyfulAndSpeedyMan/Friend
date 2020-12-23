package top.pin90.server.po;

import org.bson.types.ObjectId;
import top.pin90.common.annotation.Describe;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * 用户聊天会话
 */
@Data
public class ChatSession {
    @Id
    private ObjectId id;
    @Describe("会话成员信息")
    private List<UserSessionInfo> sessionInfos;
    @Describe("会话创建的时间")
    private String createTime;


    public static class UserSessionInfo{
        private ObjectId userId;
        @Describe("用户加入会话时间")
        private String createTime;
    }
}
