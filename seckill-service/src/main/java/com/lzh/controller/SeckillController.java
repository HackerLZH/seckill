package com.lzh.controller;

import org.springframework.web.bind.annotation.RestController;

import com.lzh.response.Result;
import com.lzh.service.ISeckillService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;



@Tag(name = "Seck Controller")
@RestController
@RequestMapping("/seckill")
public class SeckillController {
    @Autowired
    private ISeckillService seckillService;
    @Operation(summary = "TEST")
    @GetMapping("/test")
    public Result test() {
        return Result.success("test");
    }
    
    /**
     * 秒杀
     * @param id 秒杀id
     * @return
     * //TODO: 不通过HttpServletRequest获取用户id
     */
    @Operation(summary = "kill goods")
    @PostMapping("/{id}")
    public Result kill(@PathVariable("id") Integer killId, HttpServletRequest request) {
        Integer userId = Integer.parseInt(request.getHeader("userId"));
        return seckillService.kill(killId, userId);
    }
    
}
