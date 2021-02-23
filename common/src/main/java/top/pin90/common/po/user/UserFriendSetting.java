package top.pin90.common.po.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 用户好友的设置信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("UserFriendSetting")
@TypeAlias("UserFriendSetting")
public class UserFriendSetting {
    @Id
    private ObjectId id;
    /**
     * 用户Id
     */
    private ObjectId userId;
    /**
     * 好友请求验证方式
     */
    private Integer friReqVerMode;

}
