package top.pin90.server.po;

import top.pin90.common.annotation.Describe;
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
    @Describe("帖子状态")
    private String status;
    @Describe("点赞数")
    private Integer thumb;
    @Describe("评论数")
    private Integer comment;
    @Describe("转发数")
    private Integer forward;
    private Date createTime;
    private Date updateTime;
}
