package com.fengchao.workorders.service;

import com.fengchao.workorders.util.StringUtil;
//import com.fengchao.workorders.security.UsernameIsExitedException;
//import com.fengchao.workorders.security.GrantedAuthorityImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

//import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.xml.bind.DatatypeConverter;
//import java.util.ArrayList;
//import java.security.Key;
import java.util.Date;
import java.util.List;

public class TokenAuthenticationService {

    /**
     * 过期时间 2小时
     */
    private static final long EXPIRATIONTIME = 1000 * 60 * 60 * 2;
    /**
     * JWT 密码
     */
    private static final String SECRET = "yearcon";
    /**
     * TOKEN前缀
     */
    private static final String TOKEN_PREFIX = "Bearer ";
    /**
     * 存放Token的Header Key
     */
    private static final String HEADER_STRING = "Authorization";

    /**
     * 自定义的 playload
     */
    private static final String AUTHORITIES = "authorities";

    /**
     * 将jwt token 写入header头部
     *
     * @param response Http response
     * @param authentication authentication from Filter
     */
    public static void addAuthenticatiotoHttpHeader(HttpServletResponse response, Authentication authentication) {
        //Generate jwt
        String username = authentication.getName();
        //String uid = StringUtil.getUserId(username);
        Claims claims = Jwts.claims().setSubject(username);
        //生成token的时候可以把自定义数据加进去,比如用户权限
        //claims.put("uid", uid);
        //System.out.println("add uid in token: " + uid + " claims = " + claims);
        String token = Jwts.builder()
                .setSubject(authentication.getName())
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        //System.out.println("user: "+authentication.getName() +"  add header token: " + token);
                //把token设置到响应头中去
        response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }

    public static String buildToken(String username,Long id) {
        //Generate jwt
        String uid = id.toString();
        Claims claims = Jwts.claims().setSubject(username);
        //生成token的时候可以把自定义数据加进去,比如用户权限
        claims.put("uid", uid);
        //System.out.println("add uid in token: " + uid + " claims = " + claims);
        String token = Jwts.builder()
                .setSubject(username)
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        //System.out.println("user: "+ username +"  add header token: " + token);
                return token;
    }

    /**
     * 从请求头中解析出 Authentication
     * @param request Http request
     * @return authentication info
     */
    public static Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // 从Header中拿到token
        String token = request.getHeader(HEADER_STRING);
        if(token == null){
            return null;
        }
        //System.out.println("try get token from request : " + token);
        //may throw exception
        Claims claims;
        try {
            //byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET);
            //Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SECRET)
                                       .parseClaimsJws(token.replace(TOKEN_PREFIX, ""));
            //System.out.println("===: jws: " + claimsJws);
            claims = claimsJws.getBody();
            //System.out.println("===: claims: " + claimsJws.getBody());
        }catch (Exception e){
            //System.out.println("Exception : " + e.getMessage());
            return null;
        }

        //System.out.println("=== : claims: " + claims);
        //String auth = (String)claims.get(AUTHORITIES);

        // 得到 权限（角色）
        List<GrantedAuthority> authorities =  AuthorityUtils.
                commaSeparatedStringToAuthorityList((String) claims.get(AUTHORITIES));

        //System.out.println("=== : get authorities: " + authorities);
        String username = claims.getSubject();
        if (null == username || username.isEmpty()) {
            return null;
        }

        if (null != claims && null != claims.get("uid")) {
            System.out.println("=== get uid from token: " + claims.get("uid").toString());
        }

        return new UsernamePasswordAuthenticationToken(username, null, authorities);

    }


}


