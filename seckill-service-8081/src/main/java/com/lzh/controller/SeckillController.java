package com.lzh.controller;

import org.springframework.web.bind.annotation.RestController;

import com.lzh.response.Result;
import com.lzh.service.ISeckillService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;



@Tag(name = "秒杀接口")
@RestController
public class SeckillController {
    @Autowired
    private ISeckillService seckillService;

    @GetMapping("/test")
    public Result test() {
        return Result.success("test");
    }
    
    /**
     * 秒杀
     * @param id 秒杀id
     * @return
     */
    @Operation(summary = "秒杀")
    @PostMapping("/id/{id}")
    public Result kill(
        @Parameter(name = "id", description = "秒杀id", required = true, in = ParameterIn.PATH)
        @PathVariable("id") Integer killId) {
        return seckillService.kill(killId);
    }
    
}
