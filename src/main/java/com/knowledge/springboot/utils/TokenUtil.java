package com.knowledge.springboot.utils;

import com.knowledge.springboot.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Token工具类，用于生成和验证JWT令牌
 */
@Component
public class TokenUtil {

    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24小时过期
    
    @Value("${jwt.secret:defaultSecret}")
    private String secret;

    /**
     * 生成JWT令牌
     * @param user 用户信息
     * @return 令牌字符串
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("account", user.getAccount());
        
        // 生成唯一的令牌ID
        String tokenId = UUID.randomUUID().toString();
        
        return Jwts.builder()
                .setClaims(claims)
                .setId(tokenId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    
    /**
     * 从令牌中获取用户ID
     * @param token 令牌字符串
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        
        return claims.get("userId", String.class);
    }
    
    /**
     * 从令牌中获取用户名
     * @param token 令牌字符串
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        
        return claims.get("username", String.class);
    }
    
    /**
     * 从令牌中获取令牌ID
     * @param token 令牌字符串
     * @return 令牌ID
     */
    public String getTokenIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getId();
    }
    
    /**
     * 验证令牌是否有效
     * @param token 令牌字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 验证令牌是否过期
     * @param token 令牌字符串
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
} 