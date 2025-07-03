package com.knowledge.springboot.repository;

import com.knowledge.springboot.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    /**
     * 根据账号查找用户
     * @param account 账号
     * @return 用户对象
     */
    Optional<User> findByAccount(String account);
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据状态查找用户列表
     * @param status 状态
     * @return 用户列表
     */
    List<User> findByStatus(String status);
} 