package com.lzh.controller;

import com.lzh.entity.GoodsDTO;
import com.lzh.entity.GoodsKillDTO;
import com.lzh.entity.UserDTO1;
import com.lzh.service.IAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;


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
    
    @Operation(summary = "获取10个测试用户")
    @GetMapping("/ten_test_users")
    public List<UserDTO1> getTestUsers() {
        return adminService.getTestUsers();
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

    @Operation(summary = "添加商品")
    @PostMapping("/add/goods")
    public void addGoods(@RequestBody GoodsDTO goodsDTO) {
        adminService.addGoods(goodsDTO);
    }

    @Operation(summary = "添加秒杀商品")
    @PostMapping("/add/kill/goods")
    public void addKillGoods(@RequestBody GoodsKillDTO goodsKillDTO) {
        adminService.addKillGoods(goodsKillDTO);
    }
}
