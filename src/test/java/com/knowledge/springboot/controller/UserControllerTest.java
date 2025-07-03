package com.knowledge.springboot.controller;

import com.knowledge.springboot.common.ErrorCode;
import com.knowledge.springboot.domain.User;
import com.knowledge.springboot.service.impl.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户控制器测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Resource
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;

    @Test
    void getUserById() throws Exception {
        // 创建一个测试用户
        final User user = new User();
        user.setId("test_id_123");
        user.setUsername("test_username");
        user.setAccount("test_account");
        
        // 模拟服务层返回用户
        when(userService.findById(anyString())).thenReturn(user);

        // 验证控制器响应
        mockMvc.perform(get("/user").param("userId", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.OK.getCode()))
                .andExpect(jsonPath("$.data.id").value(user.getId()))
                .andExpect(jsonPath("$.data.username").value(user.getUsername()))
                .andExpect(jsonPath("$.data.account").value(user.getAccount()));
    }
}
