package com.knowledge.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 安全配置类
 * 提供密码加密器并禁用默认的安全认证
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 密码加密器
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 安全过滤链
     * 允许所有请求不需要认证
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 禁用CSRF保护
        http.csrf().disable()
            // 允许所有请求
            .authorizeRequests().anyRequest().permitAll();
        
        return http.build();
    }
} 