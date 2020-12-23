package top.pin90.server.po;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * 用户状态
 */
@Data
public class UserStatus {
    public static final String NORMAL="NORMAL";
    @Id
    private ObjectId id;
    private String status;
}
