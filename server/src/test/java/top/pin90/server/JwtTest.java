package top.pin90.server;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.pin90.common.unti.JwtUtils;

public class JwtTest {
    JwtUtils jwtUtils;
    @BeforeEach
    public void before(){
        jwtUtils=new JwtUtils("123456","userId","pin");
    }

    @Test
    public void getUserId(){
        String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJBUFAiLCJpc3MiOiJwaW4iLCJleHAiOjE2MDkyMzE3NTAsInVzZXJJZCI6IjVmZGQ4MDBlNzA4YTYyMDU1NDg3OGJmOCIsImlhdCI6MTYwODM2Nzc1MH0.awl1W1Xw_GchdUFMMeDvFJBlpeBMB4h8a1Rg7vUVGto";
        final ObjectId userId = jwtUtils.getUserId(token);
        System.out.println(userId);
    }
}
