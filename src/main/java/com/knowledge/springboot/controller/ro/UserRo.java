package com.knowledge.springboot.controller.ro;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author jxd
 * @date 2023/4/4 19:59
 */
@Data
public class UserRo {
    @NotBlank(message = "用户名不能为空")
    public String username;
    @NotBlank(message = "性别不能为空")
    public String sex;
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱不合法")
    public String email;
}
