import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.pin90.common.unti.JwtUtils;

public class TestToken {
    JwtUtils jwtUtils;
    @BeforeEach
    public void init(){
        jwtUtils=new JwtUtils("123456","userId","pin");
    }
    @Test
    public void testAccessToken(){
        ObjectId objectId = new ObjectId("5ff2ae8dcfb6de362b2296b9");
        String accessToken = jwtUtils.createAccessToken(objectId, Integer.MAX_VALUE);
        System.out.println(accessToken);
    }
}
