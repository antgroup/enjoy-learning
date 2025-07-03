package com.knowledge.springboot.interceptor;

import com.alibaba.fastjson.JSON;
import com.knowledge.springboot.common.BaseResponse;
import com.knowledge.springboot.common.ErrorCode;
import com.knowledge.springboot.common.ResultUtils;
import com.knowledge.springboot.service.impl.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证拦截器
 * 用于验证用户登录状态
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取令牌
        String token = request.getHeader("Authorization");

        // 检查令牌是否存在
        if (StringUtils.isBlank(token)) {
            handleAuthError(response, ErrorCode.USER_LOGIN_ERROR);
            return false;
        }

        // 提取Bearer token
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证令牌
        if (!authService.validateToken(token)) {
            handleAuthError(response, ErrorCode.USER_LOGIN_ERROR);
            return false;
        }

        // 刷新令牌有效期
        authService.refreshToken(token);

        return true;
    }

    /**
     * 处理认证错误
     */
    private void handleAuthError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        BaseResponse<Object> errorResponse = ResultUtils.error(errorCode);
        response.getWriter().write(JSON.toJSONString(errorResponse));
    }
}
