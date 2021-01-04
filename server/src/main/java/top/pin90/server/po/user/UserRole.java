package top.pin90.server.po.user;

import org.bson.types.ObjectId;
import top.pin90.common.annotation.Describe;
import lombok.Data;

/**
 * 用户角色
 */
@Data
public class UserRole {
    private ObjectId id;
    private String name;
    @Describe("角色权限")
    private Permission permission;
    @Data
    static public class Permission {}
}
