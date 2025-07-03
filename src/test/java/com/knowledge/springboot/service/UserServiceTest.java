package com.knowledge.springboot.service;

import com.knowledge.springboot.domain.User;
import com.knowledge.springboot.repository.UserRepository;
import com.knowledge.springboot.service.impl.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务层测试
 */
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 测试创建用户
     */
    @Test
    public void testCreateUser() {
        // 创建随机测试数据
        String username = "test_user_" + UUID.randomUUID().toString().substring(0, 8);
        String account = "test_account_" + UUID.randomUUID().toString().substring(0, 8);
        String password = "password123";

        // 创建用户
        User createdUser = userService.createUser(username, password, account);

        // 验证用户已创建
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals(username, createdUser.getUsername());
        assertEquals(account, createdUser.getAccount());
        assertNotEquals(password, createdUser.getPasswordHash()); // 密码应该被哈希

        // 清理：删除测试用户
        userRepository.deleteById(createdUser.getId());
    }

    /**
     * 测试通过用户名查找用户
     */
    @Test
    public void testFindByUsername() {
        // 创建随机测试数据
        String username = "test_user_" + UUID.randomUUID().toString().substring(0, 8);
        String account = "test_account_" + UUID.randomUUID().toString().substring(0, 8);
        String password = "password123";

        // 创建用户
        User createdUser = userService.createUser(username, password, account);

        // 通过用户名查找
        User foundUser = userService.findByUsername(username);

        // 验证查找结果
        assertNotNull(foundUser);
        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals(username, foundUser.getUsername());

        // 验证Redis缓存
        String cacheKey = "user:username:" + username;
        User cachedUser = (User) redisTemplate.opsForValue().get(cacheKey);
        assertNotNull(cachedUser, "用户应该被缓存到Redis");
        assertEquals(username, cachedUser.getUsername());

        // 清理：删除测试用户和缓存
        userRepository.deleteById(createdUser.getId());
        redisTemplate.delete(cacheKey);
    }

    /**
     * 测试通过账号查找用户
     */
    @Test
    public void testFindByAccount() {
        // 创建随机测试数据
        String username = "test_user_" + UUID.randomUUID().toString().substring(0, 8);
        String account = "test_account_" + UUID.randomUUID().toString().substring(0, 8);
        String password = "password123";

        // 创建用户
        User createdUser = userService.createUser(username, password, account);

        // 通过账号查找
        User foundUser = userService.findByAccount(account);

        // 验证查找结果
        assertNotNull(foundUser);
        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals(account, foundUser.getAccount());

        // 验证Redis缓存
        String cacheKey = "user:account:" + account;
        User cachedUser = (User) redisTemplate.opsForValue().get(cacheKey);
        assertNotNull(cachedUser, "用户应该被缓存到Redis");
        assertEquals(account, cachedUser.getAccount());

        // 清理：删除测试用户和缓存
        userRepository.deleteById(createdUser.getId());
        redisTemplate.delete(cacheKey);
    }

    /**
     * 测试用户认证
     */
    @Test
    public void testAuthenticate() {
        // 创建随机测试数据
        String username = "test_user_" + UUID.randomUUID().toString().substring(0, 8);
        String account = "test_account_" + UUID.randomUUID().toString().substring(0, 8);
        String password = "password123";

        // 创建用户
        User createdUser = userService.createUser(username, password, account);

        // 测试正确密码
        boolean authenticated = userService.authenticate(username, password);
        assertTrue(authenticated, "应该通过正确密码认证");

        // 测试错误密码
        boolean notAuthenticated = userService.authenticate(username, "wrongpassword");
        assertFalse(notAuthenticated, "不应该通过错误密码认证");

        // 测试账号认证
        boolean accountAuthenticated = userService.authenticateByAccount(account, password);
        assertTrue(accountAuthenticated, "应该通过账号和正确密码认证");

        // 清理：删除测试用户
        userRepository.deleteById(createdUser.getId());
    }
} 