package com.lzh.controller;

import org.springframework.web.bind.annotation.RestController;

import com.lzh.entity.GoodsKillVO;
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
    public void test() {
        int i = 1 / 0;
    }
    
    /**
     * 秒杀
     * @param id 秒杀id
     * @return
     */
    @Operation(summary = "秒杀")
    @PostMapping("/kill/{id}")
    public void kill(
        @Parameter(name = "id", description = "秒杀id", required = true, in = ParameterIn.PATH)
        @PathVariable("id") Integer killId) {
        seckillService.kill(killId);
    }
    
    @Operation(summary = "获取秒杀商品")
    @GetMapping("/products")
    public GoodsKillVO products() {
        return seckillService.getProducts();
    }
}
