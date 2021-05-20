package top.pin90.common.po.user;

import lombok.Data;
import top.pin90.common.annotation.Describe;

/**
 * 用户角色
 */
@Data
public class UserRole {
    private String name;
    @Describe("角色权限")
    private Permission permission;
    @Data
    static public class Permission {}
}
