package com.knowledge.springboot.controller;

import com.knowledge.springboot.common.BaseResponse;
import com.knowledge.springboot.common.ErrorCode;
import com.knowledge.springboot.common.ResultUtils;
import com.knowledge.springboot.domain.User;
import com.knowledge.springboot.dto.LoginRequest;
import com.knowledge.springboot.service.impl.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * 处理用户登录和登出
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * 用户登录或注册接口
     * 如果用户不存在，则自动注册
     */
    @PostMapping("/login")
    public BaseResponse<Map<String, Object>> login(@RequestBody @Validated LoginRequest loginRequest) {
        String account = loginRequest.getAccount();
        String password = loginRequest.getPassword();
        String username = loginRequest.getUsername();

        logger.info("用户登录请求：{}", account);

        // 基本参数校验
        if (StringUtils.isAnyBlank(account, password)) {
            return ResultUtils.error(ErrorCode.USER_INPUT_PARAM_ERROR, "账号或密码不能为空");
        }

        // 调用服务登录或注册
        Map<String, Object> result = authService.loginOrRegister(account, password, username);

        // 判断结果
        if (!(Boolean) result.get("success")) {
            return ResultUtils.error(ErrorCode.USER_INPUT_PARAM_ERROR, (String) result.get("message"));
        }

        logger.info("用户 {} 登录成功", account);
        return ResultUtils.success(result);
    }

    /**
     * 用户登出接口
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(@RequestHeader("Authorization") String token) {
        if (StringUtils.isBlank(token)) {
            return ResultUtils.error(ErrorCode.USER_LOGIN_ERROR);
        }

        // 提取Bearer token
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        boolean success = authService.logout(token);
        return ResultUtils.success(success);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public BaseResponse<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        if (StringUtils.isBlank(token)) {
            return ResultUtils.error(ErrorCode.USER_LOGIN_ERROR);
        }

        // 提取Bearer token
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        User user = authService.getCurrentUser(token);
        if (user == null) {
            return ResultUtils.error(ErrorCode.USER_LOGIN_ERROR);
        }

        // 为安全起见，不返回密码哈希
        user.setPasswordHash(null);

        return ResultUtils.success(user);
    }
}
