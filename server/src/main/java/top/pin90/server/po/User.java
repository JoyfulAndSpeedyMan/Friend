package top.pin90.server.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import top.pin90.server.pojo.Contact;

import java.util.Date;

@Data
@Document("User")
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    //联系方式
    private Contact contact;
    //用户角色
    private int role;
    //用户状态
    private String status;
    private Date createTime;
    private Date updateTime;
}
