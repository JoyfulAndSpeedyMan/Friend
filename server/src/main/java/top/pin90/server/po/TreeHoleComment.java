package top.pin90.server.po;

import top.pin90.common.annotation.Describe;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * 大树洞评论
 */
@Data
public class TreeHoleComment {
    @Id
    private String id;
    private String userId;
    private String postId;
    private String replyId;
    private String content;
    @Describe("评论状态")
    private String status;
    private int thumb;
    private Date createTime;
    private Date updateTime;
}
