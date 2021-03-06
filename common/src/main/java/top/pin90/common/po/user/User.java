package top.pin90.common.po.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
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
@TypeAlias("User")
public class User {

    @Id
    private ObjectId id;
    private String phone;
    private String password;
    private String nickname;
    private String avatar;
    /**
     * 学生认证信息
     */
    private Student student;
    /**
     * 联系方式
     */
    private Contact contact;
    /**
     * 用户角色
     */
    private int role;
    /**
     * 用户状态
     */
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
