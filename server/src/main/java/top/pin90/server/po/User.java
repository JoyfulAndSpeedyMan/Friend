package top.pin90.server.po;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import top.pin90.common.annotation.Describe;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import java.util.Date;

/**
 * 用户
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("User")
public class User {

    @Id
    private ObjectId id;
    private String phone;
    private String password;
    private String nickname;
    private String avatar;
    @Describe("学生认证信息")
    private Student student;
    @Describe("联系方式")
    private Contact contact;
    @Describe("用户角色")
    private int role;
    @Describe("用户状态")
    private String status;
    private Date createTime;
    private Date updateTime;
    @Data
    static public class Student{
    }
    @Data
    static public class Contact {
        @Email
        private String email;
    }
}
