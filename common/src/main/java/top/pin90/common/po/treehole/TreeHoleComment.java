package top.pin90.common.po.treehole;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.TypeAlias;
import top.pin90.common.annotation.Describe;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * 大树洞评论
 */
@Data
@TypeAlias("TreeHoleComment")

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
