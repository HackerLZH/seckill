package com.lzh.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户注册时接收
 */
@Data
@Schema(description = "用户注册时接收的实体")
public class UserRegisterDTO {
    @Schema(description = "用户名", type = "String")
    private String username;
    @Schema(description = "密码", type = "String")
    private String password;
    @Schema(description = "确认密码", type = "String")
    private String confirmPassword;
}
