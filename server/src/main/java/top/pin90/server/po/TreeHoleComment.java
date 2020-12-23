package top.pin90.server.po;

import org.bson.types.ObjectId;
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
    private ObjectId id;
    private ObjectId userId;
    private ObjectId postId;
    private ObjectId replyId;
    private String content;
    @Describe("评论状态")
    private String status;
    private int thumb;
    private Date createTime;
    private Date updateTime;
}
