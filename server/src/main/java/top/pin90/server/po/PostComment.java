package top.pin90.server.po;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * 贴吧帖子的评论
 */
@Data
public class PostComment {
    @Id
    private String id;
    private String userId;
    private String postId;
    private String replyId;
    private String content;
    private int thumb;
    private Date createTime;
    private Date updateTime;
}
