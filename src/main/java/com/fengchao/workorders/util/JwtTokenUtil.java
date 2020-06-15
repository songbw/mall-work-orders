package com.fengchao.workorders.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;

public class JwtTokenUtil {

    /**
     * JWT 密码
     */
    private static final String SECRET = "yearcon";
    /**
     * TOKEN前缀
     */
    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * HS256算法生成Token
     * @param jwtUserInfo
     *          用户信息
     * @return
     *          生成的Token
     */
    public static String generateToken(JWTUserInfo jwtUserInfo,int expire,String tokenSecret){
        if(jwtUserInfo == null){
            return null;
        }
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(tokenSecret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
        return Jwts.builder()
                .setSubject(jwtUserInfo.getUserId())
                .claim("userName", jwtUserInfo.getUserName())
                .claim("realName", jwtUserInfo.getRealName())
                .setExpiration(DateTime.now().plusSeconds(expire).toDate())
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .compact();
    }


    /**
     * HS256解密Token
     * @param token
     *          token字符串
     * @return
     *          返回用户信息
     */
    public static JWTUserInfo getInfoFromToken(String token, String tokenSecret){
        if(token == null){
            return null;
        }
        Claims body = null;
        try {
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(tokenSecret);
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token);
            body = claimsJws.getBody();
        }catch (Exception e){
            return null;
        }
        return new JWTUserInfo(body.getSubject(),
                getObjectValue(body.get("userName")),
                getObjectValue(body.get("realName")));
    }

    private static String getObjectValue(Object obj){
        return obj==null?"":obj.toString();
    }

    public static String getUsername(String authentication) {

        if (null == authentication || authentication.isEmpty()) {
            return "";
        }
        //System.out.println("==JWT Util= get authentication : " + authentication);
        Claims claims = Jwts.parser().setSigningKey(SECRET)
                .parseClaimsJws(authentication.replace(TOKEN_PREFIX, ""))
                .getBody();

        String username = claims.getSubject();
        //System.out.println("==JWT Util= get user name : " + username);
        return username;

    }
}
