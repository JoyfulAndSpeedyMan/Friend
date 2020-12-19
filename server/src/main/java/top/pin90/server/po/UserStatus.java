package top.pin90.server.po;

import lombok.Data;

/**
 * 用户状态
 */
@Data
public class UserStatus {
    public static final String NORMAL="NORMAL";

    private String id;
    private String status;
}
