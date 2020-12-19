package top.pin90.server.po;

import top.pin90.common.annotation.Describe;
import lombok.Data;

/**
 * 用户角色
 */
@Data
public class UserRole {
    private String id;
    private String name;
    @Describe("角色权限")
    private Permission permission;
    @Data
    static public class Permission {}
}
