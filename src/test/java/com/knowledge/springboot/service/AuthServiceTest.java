package com.knowledge.springboot.service;

import com.knowledge.springboot.domain.User;
import com.knowledge.springboot.service.impl.AuthService;
import com.knowledge.springboot.service.impl.UserService;
import com.knowledge.springboot.utils.TokenUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 认证服务测试类
 */
@SpringBootTest
public class AuthServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceTest.class);

    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TokenUtil tokenUtil;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 测试登录或注册
     */
    @Test
    public void testLoginOrRegister() {
        // 生成随机账号，避免冲突
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        String account = "test_account_" + randomSuffix;
        String password = "password123";
        String username = "test_user_" + randomSuffix;
        
        logger.info("开始登录或注册测试，账号：{}，用户名：{}", account, username);
        
        // 首次登录（注册）
        Map<String, Object> result = authService.loginOrRegister(account, password, username);
        
        // 打印结果
        logger.info("登录结果：{}", result);
        
        // 验证是否成功
        assertTrue((Boolean) result.get("success"), "首次登录应该成功");
        assertTrue((Boolean) result.get("isNewUser"), "首次登录应是新用户");
        assertNotNull(result.get("token"), "应返回令牌");
        assertNotNull(result.get("userId"), "应返回用户ID");
        assertEquals(username, result.get("username"), "用户名应匹配");
        assertEquals(account, result.get("account"), "账号应匹配");
        
        String token = (String) result.get("token");
        String userId = (String) result.get("userId");
        
        logger.info("token: {}", token);
        logger.info("userId: {}", userId);
        
        // 验证令牌ID是否在Redis中
        String redisKey = "token:" + userId;
        Object storedTokenId = redisTemplate.opsForValue().get(redisKey);
        logger.info("Redis中的令牌ID: {}", storedTokenId);
        
        // 验证令牌
        boolean isValid = authService.validateToken(token);
        logger.info("令牌验证结果: {}", isValid);
        assertTrue(isValid, "令牌应有效");
        
        // 再次登录（已有账号）
        Map<String, Object> secondResult = authService.loginOrRegister(account, password, null);
        logger.info("第二次登录结果：{}", secondResult);
        
        // 验证是否成功
        assertTrue((Boolean) secondResult.get("success"), "第二次登录应该成功");
        assertFalse((Boolean) secondResult.get("isNewUser"), "第二次登录不应是新用户");
        assertNotNull(secondResult.get("token"), "应返回令牌");
        assertEquals(userId, secondResult.get("userId"), "用户ID应匹配");
        
        // 使用第二次登录返回的token，而不是第一次登录的token
        String secondToken = (String) secondResult.get("token");
        logger.info("第二次登录的token: {}", secondToken);
        
        // 获取当前用户
        User currentUser = authService.getCurrentUser(secondToken);
        logger.info("获取到的当前用户: {}", currentUser);
        assertNotNull(currentUser, "应能获取当前用户");
        
        if (currentUser != null) {
            assertEquals(userId, currentUser.getId(), "用户ID应匹配");
            assertEquals(username, currentUser.getUsername(), "用户名应匹配");
        }
        
        // 登出
        boolean logoutSuccess = authService.logout(secondToken);
        logger.info("登出结果: {}", logoutSuccess);
        assertTrue(logoutSuccess, "登出应成功");
        
        // 登出后令牌应无效
        boolean isStillValid = authService.validateToken(secondToken);
        logger.info("登出后令牌是否有效: {}", isStillValid);
        assertFalse(isStillValid, "登出后令牌应无效");
    }
    
    /**
     * 测试错误密码登录
     */
    @Test
    public void testLoginWithWrongPassword() {
        // 生成随机账号，避免冲突
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        String account = "test_account_" + randomSuffix;
        String password = "password123";
        String wrongPassword = "wrong_password";
        String username = "test_user_" + randomSuffix;
        
        logger.info("开始错误密码登录测试，账号：{}", account);
        
        // 先注册用户
        Map<String, Object> registerResult = authService.loginOrRegister(account, password, username);
        logger.info("注册结果：{}", registerResult);
        assertTrue((Boolean) registerResult.get("success"), "注册应该成功");
        
        // 使用错误密码登录
        Map<String, Object> loginResult = authService.loginOrRegister(account, wrongPassword, null);
        logger.info("错误密码登录结果：{}", loginResult);
        
        // 验证登录失败
        assertFalse((Boolean) loginResult.get("success"), "错误密码登录应失败");
        assertEquals("密码错误", loginResult.get("message"), "应返回密码错误信息");
    }
} 