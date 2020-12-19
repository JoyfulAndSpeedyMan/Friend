package top.pin90.server.po;

import top.pin90.common.annotation.Describe;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * 用户的好友关系
 */
@Data
public class UserFriend {
    @Id
    private String id;
    @Describe("第一个用户id")
    private String fuid;
    @Describe("第二个用户id")
    private String suid;
    @Describe("好友关联状态")
    private Integer status;
    private Date createTime;
    private Date updateTime;

}
