package com.lzh.controller;

import org.springframework.web.bind.annotation.RestController;

import com.lzh.entity.UserDTO1;
import com.lzh.service.IAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;



@Tag(name = "管理员接口")
@RestController
public class AdminController {
    @Autowired
    private IAdminService adminService;

    @Operation(summary = "test")
    @GetMapping("/test")
    public Map<String, Object> test() {
        return Collections.singletonMap("data", "test");
    }
    
    @Operation(summary = "获取所有注册用户")
    @GetMapping("/register_users")
    public List<UserDTO1> getRegisterUsers() {
        return adminService.getRegisterUsers();
    }

    @Operation(summary = "获取所有登录用户")
    @GetMapping("/login_users")
    public Map<String, Object> getUsers() {
        return adminService.getLoginUsers();
    }

    @Operation(summary = "批量插入测试用户")
    @PostMapping("/add/{begin}/{end}")
    public void addUsers(@PathVariable("begin") Integer beginId, @PathVariable("end") Integer endId) {
        adminService.addUsers(beginId, endId);
    }

    @Operation(summary = "批量登录测试用户")
    @GetMapping("/login/{begin}/{end}")
    public void loginUsers(@PathVariable("begin") Integer beginId, @PathVariable("end") Integer endId) {
        adminService.loginUsers(beginId, endId);
    }
    
    @Operation(summary = "写出所有登录用户token")
    @GetMapping("/writetokens")
    public void writeToken() {
        adminService.writeTokens();
    }
}
