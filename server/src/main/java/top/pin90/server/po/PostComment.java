package top.pin90.server.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import top.pin90.common.annotation.Describe;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * 贴吧帖子的评论
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("PostComment")
public class PostComment {
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
