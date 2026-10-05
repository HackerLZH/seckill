package com.lzh.service;

import com.alibaba.cloud.nacos.annotation.NacosConfig;
import com.lzh.entity.GoodsDTO;
import com.lzh.entity.GoodsKillDTO;
import com.lzh.entity.UserDTO1;
import com.lzh.feign.AuthFeign;
import com.lzh.feign.SeckillFeign;
import com.lzh.mapper.AdminMapper;
import com.lzh.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AdminServiceImpl implements IAdminService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private AuthFeign authFeign;
    @Autowired
    private SeckillFeign seckillFeign;
    @Autowired
    private RedisUtil redisUtil;
    
    @Override
    public List<UserDTO1> getTestUsers() {
        return adminMapper.findLast10TestUsers();
    }

    @Transactional
	@Override
	public void addUsers(Integer beginId, Integer endId) {
        try {
            for (int i = beginId; i <= endId; ++i) {
                adminMapper.addUserByUsername("test" + i);
                log.info("user{} saves", i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void loginUsers(Integer beginId, Integer endId) {
        try {
            for (int i = beginId; i <= endId; ++i) {
                authFeign.login("test" + i, "123456");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public Map<String, Object> getLoginUsers() {
        try {
            log.info("获取已登录用户");
            return redisUtil.search("USER:TOKEN:*");
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @NacosConfig(group = "DEFAULT_GROUP", dataId = "admin-service.yml", key = "tokens.output")
    private String tokensOutput;

    @Override
    public void writeTokens() {
        log.info("写入文件：所有登录token");
        try(BufferedWriter br = new BufferedWriter(new FileWriter(tokensOutput))) {
            getLoginUsers().keySet().forEach(key -> {
                try {
                    br.write(key);
                    br.newLine();
                } catch (IOException e) {
                }
            });
        } catch (IOException ie) {}
    }

    @Override
    public void addGoods(GoodsDTO goodsDTO) {
        try {
            adminMapper.saveGoods(goodsDTO);
            log.info("添加商品成功：{}", goodsDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String SECKILL_STOCK_KEY = "seckill:stock:";
    private static final String SECKILL_ORIGIN_STOCK_KEY = "seckill:origin:stock:";
    // 秒杀用户
    private static final String SECKILL_ORDER_KEY = "seckill:order:";

    @Override
    public void addKillGoods(GoodsKillDTO goodsKillDTO) {
        try {
            adminMapper.saveKillGoods(goodsKillDTO);
            if (goodsKillDTO.getStartTime().isBefore(LocalDateTime.now()) && goodsKillDTO.getEndTime().isAfter(LocalDateTime.now())) {
                // 秒杀已经开始，写redis
                redisUtil.set(SECKILL_STOCK_KEY + goodsKillDTO.getId()
                        , goodsKillDTO.getStock()
                        // 剩余秒数作为ttl
                        , Duration.between(LocalDateTime.now(), goodsKillDTO.getEndTime()).getSeconds()
                        , TimeUnit.SECONDS
                );
                redisUtil.set(SECKILL_ORIGIN_STOCK_KEY + goodsKillDTO.getId()
                        , goodsKillDTO.getStock()
                        // 剩余秒数作为ttl
                        , Duration.between(LocalDateTime.now(), goodsKillDTO.getEndTime()).getSeconds()
                        , TimeUnit.SECONDS
                );
            }
            seckillFeign.evictKillCache();
            log.info("添加秒杀商品成功：{}", goodsKillDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
