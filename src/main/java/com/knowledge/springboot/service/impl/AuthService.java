package com.knowledge.springboot.service.impl;

import com.knowledge.springboot.domain.User;
import com.knowledge.springboot.utils.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务类
 * 处理用户登录、注册和令牌管理
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private TokenUtil tokenUtil;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    // 令牌缓存前缀
    private static final String TOKEN_KEY_PREFIX = "token:";
    // 令牌过期时间（24小时）
    private static final long TOKEN_EXPIRE_TIME = 24;

    /**
     * 用户登录或注册
     * 如果用户不存在，则自动注册
     * @param account 账号
     * @param password 密码
     * @param username 用户名（可选，仅在注册时使用）
     * @return 包含令牌和用户信息的结果
     */
    public Map<String, Object> loginOrRegister(String account, String password, String username) {
        Map<String, Object> result = new HashMap<>();
        
        // 先尝试查找用户
        User user = userService.findByAccount(account);
        
        // 如果用户不存在，进行注册
        if (user == null) {
            // 如果未提供用户名，则使用账号作为用户名
            if (username == null || username.isEmpty()) {
                username = account;
            }
            
            // 创建新用户
            user = userService.createUser(username, password, account);
            result.put("isNewUser", true);
        } else {
            // 用户存在，验证密码
            boolean authenticated = userService.authenticateByAccount(account, password);
            if (!authenticated) {
                // 密码验证失败
                result.put("success", false);
                result.put("message", "密码错误");
                return result;
            }
            result.put("isNewUser", false);
        }
        
        // 生成令牌
        String token = tokenUtil.generateToken(user);
        
        // 将令牌ID存入Redis
        String tokenId = tokenUtil.getTokenIdFromToken(token);
        String redisKey = TOKEN_KEY_PREFIX + user.getId();
        redisTemplate.opsForValue().set(redisKey, tokenId, TOKEN_EXPIRE_TIME, TimeUnit.HOURS);
        
        // 构造返回结果
        result.put("success", true);
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("account", user.getAccount());
        
        return result;
    }
    
    /**
     * 验证用户令牌
     * @param token 令牌字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            // 基本验证
            if (!tokenUtil.validateToken(token) || tokenUtil.isTokenExpired(token)) {
                return false;
            }
            
            // 获取用户ID和令牌ID
            String userId = tokenUtil.getUserIdFromToken(token);
            String tokenId = tokenUtil.getTokenIdFromToken(token);
            
            // 从Redis中获取当前有效的令牌ID
            String redisKey = TOKEN_KEY_PREFIX + userId;
            String storedTokenId = (String) redisTemplate.opsForValue().get(redisKey);
            
            // 验证令牌ID是否匹配
            return tokenId != null && tokenId.equals(storedTokenId);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 刷新令牌有效期
     * @param token 令牌字符串
     * @return 是否成功
     */
    public boolean refreshToken(String token) {
        if (!validateToken(token)) {
            return false;
        }
        
        String userId = tokenUtil.getUserIdFromToken(token);
        String redisKey = TOKEN_KEY_PREFIX + userId;
        
        // 刷新Redis中的令牌过期时间
        return redisTemplate.expire(redisKey, TOKEN_EXPIRE_TIME, TimeUnit.HOURS);
    }
    
    /**
     * 用户登出
     * @param token 令牌字符串
     * @return 是否成功
     */
    public boolean logout(String token) {
        if (!tokenUtil.validateToken(token)) {
            return false;
        }
        
        String userId = tokenUtil.getUserIdFromToken(token);
        String redisKey = TOKEN_KEY_PREFIX + userId;
        
        // 从Redis中删除令牌
        return redisTemplate.delete(redisKey);
    }
    
    /**
     * 获取当前用户
     * @param token 令牌字符串
     * @return 用户对象，如果令牌无效则返回null
     */
    public User getCurrentUser(String token) {
        try {
            // 基本验证
            if (!tokenUtil.validateToken(token)) {
                return null;
            }
            
            // 获取用户ID
            String userId = tokenUtil.getUserIdFromToken(token);
            if (userId == null) {
                return null;
            }
            
            // 从Redis中获取当前有效的令牌ID
            String redisKey = TOKEN_KEY_PREFIX + userId;
            String storedTokenId = (String) redisTemplate.opsForValue().get(redisKey);
            
            // 验证令牌ID是否匹配
            String tokenId = tokenUtil.getTokenIdFromToken(token);
            if (tokenId == null || !tokenId.equals(storedTokenId)) {
                return null;
            }
            
            // 获取用户信息
            return userService.findById(userId);
        } catch (Exception e) {
            return null;
        }
    }
} 