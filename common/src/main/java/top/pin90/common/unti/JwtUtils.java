package top.pin90.common.unti;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.util.StringUtils;
import top.pin90.common.exception.auth.UserTokenExpireException;
import top.pin90.common.exception.auth.UserVerifyException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    /**
     * 签发密钥
     */
    private  final String SECRET;
    /**
     * token 默认过期时间
     */
    private  final int calendarInterval;
    /**
     * 用户Id key
     */
    private  final String USER_ID_KET;
    /**
     * 签发者
     */
    private  final String ISS;

    public JwtUtils(String SECRET,int calendarInterval,String USER_ID_KET,String ISS) {
        this.SECRET = SECRET;
        this.calendarInterval = calendarInterval;
        this.ISS=ISS;
        this.USER_ID_KET=USER_ID_KET;
    }

    /**
     * JWT生成Token.<br/>
     *
     * JWT构成: header, payload, signature
     *
     * @param user_id
     *            登录成功后用户user_id, 参数user_id不可传空
     */
    public  String createToken(String user_id){
        Date iatDate = new Date();
        // expire time
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.SECOND, calendarInterval);
        Date expiresDate = nowTime.getTime();

        // header Map
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        // build token
        // param backups {iss:Service, aud:APP}
        String token = JWT.create().withHeader(map) // header
                .withClaim("iss", ISS) // payload
                .withClaim("aud", "APP")
                .withClaim(this.USER_ID_KET, user_id)
                .withIssuedAt(iatDate) // sign time
                .withExpiresAt(expiresDate) // expire time
                .sign(Algorithm.HMAC256(SECRET)); // signature
        return token;
    }

    /**
     * 解密Token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public  Map<String, Claim> parseToken(String token) {
        DecodedJWT jwt = null;
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        try {
            jwt = verifier.verify(token);
        }
        catch (TokenExpiredException e){
            throw new UserTokenExpireException("用户身份已过期",e);
        }
        catch (AlgorithmMismatchException e){
            throw new UserVerifyException("token加密算法不一致",e);
        }
        catch (InvalidClaimException | SignatureVerificationException | JWTDecodeException e){
            throw new UserVerifyException("token不正确",e);
        }
        return jwt.getClaims();
    }

    /**
     * 根据Token获取user_id
     *
     * @param token
     * @return user_id
     */
    public  String getUserId(String token) throws UserVerifyException {
        Map<String, Claim> claims = parseToken(token);
        Claim user_id_claim = claims.get(this.USER_ID_KET);
        if (null == user_id_claim || !StringUtils.hasText(user_id_claim.asString())) {
            // token 校验失败, 抛出Token验证非法异常
            throw new UserVerifyException("User Id Invalid");
        }
        return user_id_claim.asString();
    }

    public static class Audience{
        public static final String APP="APP";
    }

}
