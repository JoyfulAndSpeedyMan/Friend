package top.pin90.common.po.user;

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
 * 用户的好友关系
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("UserFriendRelation")
@TypeAlias("UserFriendRelation")
public class UserFriendRelation {
    @Id
    private ObjectId id;
    /**
     * 第一个用户id
     */
    private ObjectId fuid;
    /**
     * 第二个用户id
     */
    private ObjectId suid;
    /**
     * 好友请求验证消息
     */
    private String requestMsg;
    /**
     * 对方响应消息
     */
    private String resMsg;
    /**
     * 好友备注
     */
    private String noteName;

    /**
     * 上一个好友关联状态
     */
    private String lastStatus;
    /**
     * 好友关联状态
     */
    private String status;
    private Date createTime;
    private Date updateTime;

}
