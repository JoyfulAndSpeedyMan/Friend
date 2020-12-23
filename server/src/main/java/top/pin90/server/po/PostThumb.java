package top.pin90.server.po;

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
@Document("PostThumb")
public class PostThumb {
    @Id
    private ObjectId id;
    private ObjectId postId;
    private ObjectId userId;
    private Date createTime;
}
