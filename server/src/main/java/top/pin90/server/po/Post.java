package top.pin90.server.po;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;
/**
 * 贴吧帖子
 */
@Data
public class Post {
    @Id
    private String id;
    private String userId;
    private String content;
    private Integer thumbNumber;
    private Integer commentNumber;
    private Integer forwardNumber;
    private Date createTime;
    private Date updateTime;
}
