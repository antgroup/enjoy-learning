package com.knowledge.springboot.config;

import com.knowledge.springboot.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册认证拦截器，并指定拦截的路径
        registry.addInterceptor(authInterceptor)
                // 拦截需要认证的路径
                .addPathPatterns("/api/**")
                // 排除登录相关路径
                .excludePathPatterns(
                        "/api/auth/login",
                        "/error"
                );
    }
}
