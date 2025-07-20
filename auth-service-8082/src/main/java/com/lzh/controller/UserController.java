package com.lzh.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.feiniaojin.gracefulresponse.api.ValidationStatusCode;
import com.lzh.entity.UserLoginVO;
import com.lzh.entity.UserRegisterDTO;
import com.lzh.service.IUserService;
import com.lzh.utils.Constants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "认证接口")
@RestController
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping("/test")
    public Map<String, Object> test() {
        return Collections.singletonMap("test", "test");
    }

    @ValidationStatusCode(code = Constants.GracefulCode.NOT_BLANK_CODE)
    @Operation(summary = "用户登录")
    @GetMapping("/login") // 目前的springboot版本@RequestParam必须带上参数
    public UserLoginVO login(
        @Parameter(name = "username", description = "用户名", required = true, in = ParameterIn.QUERY)
        @NotBlank(message = Constants.GracefulCode.NOT_BLANK_MESSAGE)
        @RequestParam("username") String username, 
        @Parameter(name = "password", description = "密码", required = true, in = ParameterIn.QUERY)
        @NotBlank(message = Constants.GracefulCode.NOT_BLANK_MESSAGE)
        @RequestParam("password") String password) {

        return userService.login(username, password);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register") // 小心swagger的RequestBody，不要导错了！
    public void register(@RequestBody UserRegisterDTO userdto) {
        userService.register(userdto);
    }  

    @Operation(summary = "用户注销")
    @PostMapping("/logout")
    public void logout(
        @Parameter(name = Constants.TOKEN_HEADER, description = "请求token",required = true,in = ParameterIn.HEADER)
        @RequestHeader(Constants.TOKEN_HEADER) String token) {
        userService.logout(token);
    }  
}
