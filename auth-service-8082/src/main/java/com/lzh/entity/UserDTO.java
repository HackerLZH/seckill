package com.lzh.entity;

import com.feiniaojin.gracefulresponse.api.ValidationStatusCode;
import com.lzh.utils.Constants;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户注册时接收
 */

//TODO 拦不住
@ValidationStatusCode(code = "500")
@Data
@Schema(description = "用户登录注册时接收的实体")
public class UserDTO {

    @NotBlank(message = Constants.GracefulCode.NOT_BLANK_MESSAGE)
    @Schema(description = "用户名", type = "String")
    private String username;

    @NotBlank(message = Constants.GracefulCode.NOT_BLANK_MESSAGE)
    @Schema(description = "密码", type = "String")
    private String password;

}
