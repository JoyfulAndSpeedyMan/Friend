package top.pin90.common.po.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import top.pin90.common.annotation.Describe;

import java.util.Date;
/**
 * 贴吧帖子
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("Post")
public class Post {
    @Id
    private ObjectId id;
    private ObjectId userId;
    @Describe("被转发的帖子Id")
    private ObjectId forwardPid;
    @Describe("被转发人的用户Id")
    private ObjectId forwardUid;
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
