package com.lzh.controller;

import com.lzh.config.CacheConfig;
import com.lzh.entity.GoodsKillOrderVO;
import com.lzh.entity.GoodsKillVO;
import com.lzh.enums.OrderStatus;
import com.lzh.service.ISeckillService;
import com.lzh.utils.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@Tag(name = "秒杀接口")
@RestController
public class SeckillController {
    @Autowired
    private ISeckillService seckillService;

    @Autowired
    private CacheManager cacheManager;

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
    public String kill(
        @Parameter(name = "id", description = "秒杀id", required = true, in = ParameterIn.PATH)
        @PathVariable("id") Integer killId) {
        return seckillService.kill(killId);
    }

    @Operation(summary = "我的订单")
    @GetMapping("/kill/orders")
    public List<GoodsKillOrderVO> getKillOrders() {
        return seckillService.getKillOrders(UserHolder.getUser().getId());
    }

    @Operation(summary = "删除秒杀订单")
    @PostMapping("/delete/kill/{orderId}")
    public void deleteKillOrder(@PathVariable("orderId") Long orderId) {
        seckillService.deleteKillOrder(orderId);
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
    @PostMapping("/internal/postprocess")
    public void postprocess(@RequestParam("orderId") Long orderId) {
        seckillService.postProcess(orderId);
    }

    @CacheEvict(value = CacheConfig.CACHE_PRODUCTS, key = "'kill_all'")
    @Operation(summary = "强制清除秒杀商品缓存（内部路径）")
    @PostMapping("/internal/evict/kill/cache")
    public void evictKillCache() {
        log.info("管理员更新秒杀商品=>清空缓存");
    }

    @Operation(summary = "扫码成功，保存二维码地址（内部路径）")
    @PostMapping("/internal/insert/qrcode")
    public void insertQrCode(@RequestParam("orderId") Long orderId, @RequestParam("qrcode") String qrCode) {
        seckillService.insertQrCode(orderId, qrCode);
    }
}
