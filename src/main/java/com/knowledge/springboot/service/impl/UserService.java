package com.knowledge.springboot.service.impl;

import com.knowledge.springboot.domain.User;
import com.knowledge.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    // Redis缓存过期时间（24小时）
    private static final long CACHE_EXPIRATION = 24;

    /**
     * 创建用户
     * @param user 用户对象
     * @return 创建的用户
     */
    public User createUser(User user) {
        if (Objects.isNull(user)) {
            return null;
        }
        
        // 设置创建时间和更新时间
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(new Date());
        }
        user.setUpdatedAt(new Date());
        
        return userRepository.save(user);
    }
    
    /**
     * 创建用户（提供用户名、密码和账号）
     * @param username 用户名
     * @param password 密码
     * @param account 账号
     * @return 创建的用户
     */
    public User createUser(String username, String password, String account) {
        User user = new User();
        user.setUsername(username);
        // 对密码进行加密
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setAccount(account);
        user.setStatus("active");
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        
        return userRepository.save(user);
    }

    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户对象
     */
    public User findById(String id) {
        if (Objects.isNull(id) || id.isEmpty()) {
            return null;
        }
        
        // 尝试从Redis缓存获取
        String cacheKey = "user:id:" + id;
        User cachedUser = (User) redisTemplate.opsForValue().get(cacheKey);
        if (cachedUser != null) {
            return cachedUser;
        }
        
        // 从数据库查询
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            // 将结果存入Redis缓存
            User user = userOpt.get();
            redisTemplate.opsForValue().set(cacheKey, user, CACHE_EXPIRATION, TimeUnit.HOURS);
            return user;
        }
        
        return null;
    }
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    public User findByUsername(String username) {
        if (Objects.isNull(username) || username.isEmpty()) {
            return null;
        }
        
        // 尝试从Redis缓存获取
        String cacheKey = "user:username:" + username;
        User cachedUser = (User) redisTemplate.opsForValue().get(cacheKey);
        if (cachedUser != null) {
            return cachedUser;
        }
        
        // 从数据库查询
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            // 将结果存入Redis缓存
            User user = userOpt.get();
            redisTemplate.opsForValue().set(cacheKey, user, CACHE_EXPIRATION, TimeUnit.HOURS);
            return user;
        }
        
        return null;
    }
    
    /**
     * 根据账号查找用户
     * @param account 账号
     * @return 用户对象
     */
    public User findByAccount(String account) {
        if (Objects.isNull(account) || account.isEmpty()) {
            return null;
        }
        
        // 尝试从Redis缓存获取
        String cacheKey = "user:account:" + account;
        User cachedUser = (User) redisTemplate.opsForValue().get(cacheKey);
        if (cachedUser != null) {
            return cachedUser;
        }
        
        // 从数据库查询
        Optional<User> userOpt = userRepository.findByAccount(account);
        if (userOpt.isPresent()) {
            // 将结果存入Redis缓存
            User user = userOpt.get();
            redisTemplate.opsForValue().set(cacheKey, user, CACHE_EXPIRATION, TimeUnit.HOURS);
            return user;
        }
        
        return null;
    }
    
    /**
     * 用户认证（使用用户名和密码）
     * @param username 用户名
     * @param password 密码
     * @return 认证是否成功
     */
    public boolean authenticate(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            return false;
        }
        
        // 验证密码
        return passwordEncoder.matches(password, user.getPasswordHash());
    }
    
    /**
     * 用户认证（使用账号和密码）
     * @param account 账号
     * @param password 密码
     * @return 认证是否成功
     */
    public boolean authenticateByAccount(String account, String password) {
        User user = findByAccount(account);
        if (user == null) {
            return false;
        }
        
        // 验证密码
        return passwordEncoder.matches(password, user.getPasswordHash());
    }
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否成功
     */
    public boolean deleteUser(String id) {
        if (Objects.isNull(id) || id.isEmpty()) {
            return false;
        }
        
        // 从Redis中删除用户缓存
        String idCacheKey = "user:id:" + id;
        redisTemplate.delete(idCacheKey);
        
        // 查询用户以获取用户名和账号
        User user = findById(id);
        if (user != null) {
            String usernameCacheKey = "user:username:" + user.getUsername();
            String accountCacheKey = "user:account:" + user.getAccount();
            // 删除用户名和账号对应的缓存
            redisTemplate.delete(usernameCacheKey);
            redisTemplate.delete(accountCacheKey);
        }
        
        // 从MongoDB中删除用户
        userRepository.deleteById(id);
        return true;
    }
}
