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
        ObjectId objectId = new ObjectId("609e180e8a22b970ac52af34");
        String accessToken = jwtUtils.createAccessToken(objectId, Integer.MAX_VALUE);
        System.out.println(accessToken);
    }
}
