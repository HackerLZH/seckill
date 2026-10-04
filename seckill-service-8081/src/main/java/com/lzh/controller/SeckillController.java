package com.lzh.controller;

import com.lzh.entity.GoodsKillOrderVO;
import com.lzh.entity.GoodsKillVO;
import com.lzh.enums.OrderStatus;
import com.lzh.service.ISeckillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
     * @param {id} 秒杀id
     * @return
     */
    @Operation(summary = "秒杀")
    @PostMapping("/kill/{id}")
    public void kill(
        @Parameter(name = "id", description = "秒杀id", required = true, in = ParameterIn.PATH)
        @PathVariable("id") Integer killId) {
        seckillService.kill(killId);
    }

    @Operation(summary = "我的订单")
    @GetMapping("/kill/orders")
    public List<GoodsKillOrderVO> getKillOrders() {
        return seckillService.getKillOrders();
    }

    @Operation(summary = "获取秒杀商品")
    @GetMapping("/products")
    public GoodsKillVO products() {
        return seckillService.getProducts();
    }

    @Operation(summary = "订单号有效性检验（内部路径）")
    @GetMapping("/internal/check/order")
    public boolean checkOrder(Long orderId, Integer userId) {
        return seckillService.checkOrder(orderId, userId, OrderStatus.WAIT);
    }

    @Operation(summary = "支付成功，后处理（内部路径）")
    @PostMapping("/postprocess")
    public void postprocess(@RequestParam("orderId") Long orderId) {
        seckillService.postProcess(orderId);
    }
}
