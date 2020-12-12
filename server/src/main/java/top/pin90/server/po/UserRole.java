package top.pin90.server.po;

import lombok.Data;
import top.pin90.server.pojo.Permission;

@Data
public class UserRole {
    private String id;
    private String name;
    private Permission permission;
}
