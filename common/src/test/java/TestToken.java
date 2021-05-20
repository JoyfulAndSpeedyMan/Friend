import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.pin90.common.unti.JwtUtils;

public class TestToken {
    JwtUtils jwtUtils;

    @BeforeEach
    public void init() {
        jwtUtils = new JwtUtils("123456", "userId", "pin");
    }

    @Test
    public void testAccessToken() {
        String[] ids = {
                "60a4f877602e122ea07b8dec",
                "60a4f88b602e122ea07b8dee",
                "60a51940a05854418b8e6e43",
                "60a51958a05854418b8e6e45",
                "60a51976a05854418b8e6e47",
                "60a5198aa05854418b8e6e49"
        };
        for (String id : ids) {
            ObjectId objectId = new ObjectId(id);
            String accessToken = jwtUtils.createAccessToken(objectId, Integer.MAX_VALUE);
            System.out.println(accessToken);
        }

    }
}
