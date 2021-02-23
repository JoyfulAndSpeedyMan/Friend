package top.pin90.common.po.treehole;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.TypeAlias;
import top.pin90.common.annotation.Describe;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * 大树洞帖子
 */
@Data
@TypeAlias("TreeHole")
public class TreeHole {
    @Id
    private ObjectId id;
    private ObjectId userId;
    private String content;
    @Describe("帖子状态")
    private String status;
    @Describe("点赞数")
    private Integer thumb;
    @Describe("评论数")
    private Integer comment;

    private Date createTime;
    private Date updateTime;
}
