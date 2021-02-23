package top.pin90.common.po.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 贴吧帖子的评论
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("PostComment")
/**
 * 帖子评论
 */
public class PostComment {
    @Id
    private ObjectId id;
    /**
     * 发表评论的用户的Id
     */
    private ObjectId userId;
    /**
     * 当前评论所属的帖子(Post)Id
     */
    private ObjectId postId;
    /**
     * 评论回复的用户的Id
     */
    private ObjectId replyUserId;
    /**
     * 回复的评论Id
     */
    private ObjectId replyId;
    private String content;
    /**
     * 评论状态
     */
    private String status;
    private int thumb;
    private Date createTime;
}
