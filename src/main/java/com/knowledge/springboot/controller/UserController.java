package com.knowledge.springboot.controller;

import com.knowledge.springboot.common.Statusful;
import com.knowledge.springboot.common.error.user.UserInputParamError;
import com.knowledge.springboot.common.error.user.UserLoginError;
import com.knowledge.springboot.common.util.ValidatorUtils;
import com.knowledge.springboot.controller.ro.UserRo;
import com.knowledge.springboot.domain.User;
import com.knowledge.springboot.service.impl.UserService;
import com.knowledge.springboot.utils.RequestContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    @Autowired
    private RequestContextUtil requestContextUtil;

    @GetMapping("/test")
    public Object test() {
        log.info("/user/test");
        return Statusful.ok("test");
    }

    /**
     * 获取当前登录用户信息
     * @return 用户对象
     */
    @GetMapping
    public Object getCurrentUser() {
        String userId = requestContextUtil.getCurrentUserId();
        if (userId == null) {
            return Statusful.error(new UserLoginError());
        }

        final User user = userService.findById(userId);
        if (user == null) {
            return Statusful.error(new UserInputParamError("用户不存在"));
        }

        return Statusful.ok(user);
    }

    /**
     * 创建用户
     * @param user 用户数据
     * @return 结果
     */
    @PostMapping
    public Object createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return Statusful.ok(createdUser);
    }

    @PostMapping("/testValidate")
    public Object testValidate(@RequestBody UserRo userRo) {
        ValidatorUtils.validateEntity(userRo);
        return Statusful.ok(userRo);
    }
}
