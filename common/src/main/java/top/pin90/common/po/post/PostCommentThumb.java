package top.pin90.common.po.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("PostCommentThumb")
/**
 * 用户对帖子评论点赞的关系
 */
public class PostCommentThumb {
    @Id
    private ObjectId id;
    /**
     * 被点赞的评论Id
     */
    private ObjectId postCommentId;
    /**
     * 点赞的用户Id
     */
    private ObjectId userId;
    private Date createTime;
}
