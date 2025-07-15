package com.lzh.service;

import org.apache.ibatis.javassist.bytecode.stackmap.BasicBlock.Catch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.cloud.nacos.annotation.NacosConfig;
import com.lzh.feign.AuthFeign;
import com.lzh.mapper.AdminMapper;
import com.lzh.response.Result;
import com.lzh.utils.RedisUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminServiceImpl implements IAdminService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private AuthFeign authFeign;
    @Autowired
    private RedisUtil redisUtil;
    
    @Override
    public Result getRegisterUsers() {
        return Result.success(adminMapper.findLast10Users());
    }
	@Override
	public Result addUsers(Integer beginId, Integer endId) {
        for (int i = beginId; i <= endId; ++i) {
            adminMapper.addUserByUsername("test" + i);
            log.info("user{} saves", i);
        }
        return Result.success();
	}
    @Override
    public Result loginUsers(Integer beginId, Integer endId) {
        for (int i = beginId; i <= endId; ++i) {
            authFeign.login("test" + i, "123456");
            log.info("user{} login", i);
        }
        return Result.success();
    }
    @Override
    public Result getLoginUsers() {
        return Result.success(redisUtil.search("USER:TOKEN:*"));
    }

    @NacosConfig(group = "DEFAULT_GROUP", dataId = "admin-service.yml", key = "tokens.output")
    private String tokensOutput;
    @Override
    public Result writeTokens() {
        Map<String, Object> map = (Map<String, Object>)getLoginUsers().getData();
        try(BufferedWriter br = new BufferedWriter(new FileWriter(tokensOutput))) {
            map.keySet().forEach(key -> {
                try {
                    br.write(key);
                    br.newLine();
                } catch (IOException e) {
                }
            });
        } catch (IOException ie) {}
        return Result.success();
    }
}
