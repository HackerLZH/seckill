package com.lzh.controller;

import org.springframework.web.bind.annotation.RestController;

import com.lzh.response.Result;
import com.lzh.service.IAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
    public Result test() {
        return Result.success();
    }
    
    @Operation(summary = "获取所有注册用户")
    @GetMapping("/register_users")
    public Result getRegisterUsers() {
        return adminService.getRegisterUsers();
    }

    @Operation(summary = "获取所有登录用户")
    @GetMapping("/login_users")
    public Result getUsers() {
        return adminService.getLoginUsers();
    }

    @Operation(summary = "批量插入测试用户")
    @PostMapping("/add/{begin}/{end}")
    public Result addUsers(@PathVariable("begin") Integer beginId, @PathVariable("end") Integer endId) {
        return adminService.addUsers(beginId, endId);
    }

    @Operation(summary = "批量登录测试用户")
    @GetMapping("/login/{begin}/{end}")
    public Result loginUsers(@PathVariable("begin") Integer beginId, @PathVariable("end") Integer endId) {
        return adminService.loginUsers(beginId, endId);
    }
    
    @Operation(summary = "写出所有登录用户token")
    @GetMapping("/writetokens")
    public Result writeToken() {
        return adminService.writeTokens();
    }
}
