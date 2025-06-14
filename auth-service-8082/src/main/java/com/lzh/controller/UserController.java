package com.lzh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lzh.entity.UserRegisterDTO;
import com.lzh.response.Result;
import com.lzh.service.IUserService;
import com.lzh.utils.Constants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "认证接口")
@RestController
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping("/test")
    public Result test() {
        return Result.success("测试成功");
    }

    @Operation(summary = "用户登录")
    @GetMapping("/login") // 目前的springboot版本@RequestParam必须带上参数
    public Result login(
        @Parameter(name = "username", description = "用户名", required = true, in = ParameterIn.QUERY)
        @RequestParam("username") String username, 
        @Parameter(name = "password", description = "密码", required = true, in = ParameterIn.QUERY)
        @RequestParam("password") String password) {

        return userService.login(username, password);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register") // 小心swagger的RequestBody，不要导错了！
    public Result register(@RequestBody UserRegisterDTO userdto) {
        log.info(userdto.toString());
        return userService.register(userdto);
    }

    // @Operation(summary = "用户注销")
    // @PostMapping("/logout")
    // public Result logout(HttpServletRequest request) {
    //     return userService.logout(request.getHeader(Constants.TOKEN_HEADER));
    // }    

    @Operation(summary = "用户注销")
    @PostMapping("/logout")
    public Result logout(
        @Parameter(name = Constants.TOKEN_HEADER, description = "请求token",required = true,in = ParameterIn.HEADER)
        @RequestHeader(Constants.TOKEN_HEADER) String token) {
        return userService.logout(token);
    }  
}
