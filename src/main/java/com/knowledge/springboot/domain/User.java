package com.knowledge.springboot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String account;

    private String passwordHash;

    private String status = "active";

    private Date createdAt;

    private Date updatedAt;

    // 构造一个简单的测试用户
    public static User createTestUser(String username, String account) {
        User user = new User();
        user.setUsername(username);
        user.setAccount(account);
        user.setPasswordHash("test_password_hash");
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        return user;
    }
}
