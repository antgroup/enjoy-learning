package com.knowledge.springboot.repository;

import com.knowledge.springboot.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据库连接测试
 * 用于测试MongoDB和Redis的连接
 */
@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 测试MongoDB连接
     */
    @Test
    public void testMongoDBConnection() {
        // 创建一个随机用户名，避免重复
        String testUsername = "test_user_" + UUID.randomUUID().toString().substring(0, 8);
        String testAccount = "test_account_" + UUID.randomUUID().toString().substring(0, 8);
        
        // 创建测试用户
        User testUser = User.createTestUser(testUsername, testAccount);
        
        // 保存到MongoDB
        User savedUser = userRepository.save(testUser);
        assertNotNull(savedUser.getId(), "保存用户后应该有ID");
        
        // 查询保存的用户
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(foundUser, "应该能够找到保存的用户");
        assertEquals(testUsername, foundUser.getUsername(), "用户名应该匹配");
        
        // 查询所有用户
        List<User> allUsers = userRepository.findAll();
        assertTrue(allUsers.size() > 0, "应该至少有一个用户");
        
        // 根据用户名查询
        User userByUsername = userRepository.findByUsername(testUsername).orElse(null);
        assertNotNull(userByUsername, "应该能够通过用户名找到用户");
        
        // 清理：删除测试用户
        userRepository.deleteById(savedUser.getId());
        
        System.out.println("MongoDB连接测试成功，能够进行CRUD操作");
    }

    /**
     * 测试Redis连接
     */
    @Test
    public void testRedisConnection() {
        // 创建测试键值
        String testKey = "test:key:" + UUID.randomUUID().toString().substring(0, 8);
        User testUser = User.createTestUser("redis_test_user", "redis_test_account");
        
        // 写入Redis
        redisTemplate.opsForValue().set(testKey, testUser, 1, TimeUnit.MINUTES);
        
        // 读取Redis
        User cachedUser = (User) redisTemplate.opsForValue().get(testKey);
        assertNotNull(cachedUser, "应该能够从Redis获取到刚才存入的用户");
        assertEquals("redis_test_user", cachedUser.getUsername(), "Redis中存储的用户名应该匹配");
        
        // 检查键是否存在
        Boolean hasKey = redisTemplate.hasKey(testKey);
        assertTrue(hasKey, "Redis应该包含测试键");
        
        // 删除键
        redisTemplate.delete(testKey);
        Boolean keyExists = redisTemplate.hasKey(testKey);
        assertFalse(keyExists, "删除后Redis中应该不存在该键");
        
        System.out.println("Redis连接测试成功，能够进行读写操作");
    }
} 