package com.knowledge.springboot.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求DTO
 */
@Data
public class LoginRequest {

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    private String account;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 用户名（可选）
     * 仅在首次登录即注册时使用
     */
    private String username;
} 