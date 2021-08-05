package top.pin90.common.po.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import top.pin90.common.pojo.Status;

/**
 * 用户状态
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("UserStatus")
@TypeAlias("UserStatus")
public class UserStatus extends Status {
    @Id
    private ObjectId id;
    private String status;
}
