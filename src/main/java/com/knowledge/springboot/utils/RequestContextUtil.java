package com.knowledge.springboot.utils;

import com.knowledge.springboot.service.impl.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求上下文工具类
 * 用于从请求中获取当前用户信息
 */
@Component
public class RequestContextUtil {

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenUtil tokenUtil;

    /**
     * 获取当前请求的用户ID
     * @return 当前用户ID，如果未登录则返回null
     */
    public String getCurrentUserId() {
        System.out.println("获取userId。。。。");
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();

        // 从请求头中获取令牌
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)) {
            return null;
        }

        // 提取Bearer token
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证令牌并获取用户ID
        if (authService.validateToken(token)) {
            return tokenUtil.getUserIdFromToken(token);
        }

        return null;
    }
}
