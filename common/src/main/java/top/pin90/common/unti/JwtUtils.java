package top.pin90.common.unti;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.bson.types.ObjectId;
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
     * 用户Id key
     */
    private  final String USER_ID_KET;
    /**
     * 签发者
     */
    private  final String ISS;
    /**
     * 标志是否是refreshToken的key
     */
    private final String rsKey;
    public JwtUtils(String SECRET, String USER_ID_KET, String ISS){
        this(SECRET,USER_ID_KET,ISS,"rt");
    }
    public JwtUtils(String SECRET, String USER_ID_KET, String ISS, String rsKey) {
        this.SECRET = SECRET;
        this.ISS=ISS;
        this.USER_ID_KET=USER_ID_KET;
        this.rsKey = rsKey;
    }

    /**
     * JWT生成Token.<br/>
     *
     * JWT构成: header, payload, signature
     *
     * @param id
     *            登录成功后用户id, 参数id不可传空
     */
    public  String createAccessToken(ObjectId id, int validTime){
        String userId=id.toString();

        String token = JWT.create()
                .withHeader(getDefaultHeads()) // header
                .withClaim("iss", ISS) // payload
                .withClaim(this.USER_ID_KET, userId)
                .withIssuedAt(new Date()) // sign time
                .withExpiresAt(getExpireTime(validTime)) // expire time
                .sign(Algorithm.HMAC256(SECRET)); // signature
        return token;
    }
    public  String refreshAccessToken(ObjectId id, int validTime){
        String userId=id.toString();
        String token = JWT.create()
                .withHeader(getDefaultHeads()) // header
                .withClaim("iss", ISS) // payload
                .withClaim(this.USER_ID_KET, userId)
                .withClaim(rsKey,true)   // 设置为refreshToken
                .withIssuedAt(new Date()) // sign time
                .withExpiresAt(getExpireTime(validTime)) // expire time
                .sign(Algorithm.HMAC256(SECRET)); // signature
        return token;
    }
    public boolean isRefreshToken(String token){
        return isRefreshToken(parseToken(token));
    }
    public boolean isRefreshToken(DecodedJWT decodedJWT){
        return decodedJWT.getClaims().containsKey(rsKey);
    }
    public Map<String, Object> getDefaultHeads(){
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        return map;
    }
    public Date getExpireTime(int validTime){
        Date iatDate = new Date();
        // expire time
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.SECOND, validTime);
        Date expiresDate = nowTime.getTime();
        return expiresDate;
    }

    /**
     * 解密Token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public  DecodedJWT parseToken(String token) {
        if(!StringUtils.hasLength(token))
            throw new UserVerifyException("token不能为空");
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
        return jwt;
    }
    public Map<String,Claim> getClaims(String token){
        return parseToken(token).getClaims();
    }
    /**
     * 根据Token获取user_id
     *
     * @param token
     * @return user_id
     */
    public  ObjectId getUserId(String token) throws UserVerifyException {
        return new ObjectId(getValue(token,USER_ID_KET));
    }
    public ObjectId getUserId(DecodedJWT decodedJWT){
        return new ObjectId(decodedJWT.getClaims().get(USER_ID_KET).asString());
    }
    public Claim getClaim(String token,String key){
        return getClaims(token).get(key);
    }
    public String getValue(String token,String key){
        final Claim claim = getClaim(token, key);
        if(claim==null || !StringUtils.hasText(claim.asString()))
            throw new UserVerifyException("User Id Invalid");
        return claim.asString();
    }
}
