package com.lzh.controller;

import com.feiniaojin.gracefulresponse.api.ValidationStatusCode;
import com.lzh.entity.*;
import com.lzh.service.IUserService;
import com.lzh.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    @PostMapping("/login") // 目前的springboot版本@RequestParam必须带上参数
    public UserLoginVO login(@RequestBody UserDTO userdto) {
        return userService.login(userdto.getUsername(), userdto.getPassword());
    }

    @Operation(summary = "用户登录（内部调用）")
    @PostMapping("/internal/login")
    public void login(@RequestParam("username") String username, @RequestParam("password") String password) {
        userService.login(username, password);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register") // 小心swagger的RequestBody，不要导错了！
    public void register(@RequestBody UserDTO userdto) {
        userService.register(userdto);
    }

    @Operation(summary = "用户注销")
    @PostMapping("/logout")
    public void logout(
        @Parameter(name = Constants.TOKEN_HEADER, description = "请求token",required = true,in = ParameterIn.HEADER)
        @RequestHeader(Constants.TOKEN_HEADER) String token) {
        userService.logout(token);
    }

    @Operation(summary = "用户信息")
    @GetMapping("/info/{username}")
    public UserInfo info(@PathVariable("username") String username) {
        return userService.getUserInfo(username);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/info/update")
    public boolean updateUserInfo(@RequestBody UserInfo userInfo) {
        return userService.updateUserInfo(userInfo);
    }

    @Operation(summary = "查询用户地址")
    @GetMapping("/info/address/{default}")
    public List<Address> address(@PathVariable("default") Integer default_address_id) {
        return userService.getUserAddress(default_address_id);
    }

    @Operation(summary = "添加用户地址")
    @PostMapping("/info/address/add")
    public void addressAdd(@RequestBody AddressAddDTO addressAddDTO) {
        userService.addAddress(addressAddDTO);
    }
}
