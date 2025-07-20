package com.lzh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.cloud.nacos.annotation.NacosConfig;
import com.lzh.entity.UserDTO1;
import com.lzh.feign.AuthFeign;
import com.lzh.mapper.AdminMapper;
import com.lzh.utils.RedisUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
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
    public List<UserDTO1> getRegisterUsers() {
        return adminMapper.findLast10Users();
    }

    @Transactional
	@Override
	public void addUsers(Integer beginId, Integer endId) {
        for (int i = beginId; i <= endId; ++i) {
            adminMapper.addUserByUsername("test" + i);
            log.info("user{} saves", i);
        }
	}
    @Override
    public void loginUsers(Integer beginId, Integer endId) {
        for (int i = beginId; i <= endId; ++i) {
            authFeign.login("test" + i, "123456");
            log.info("user{} login", i);
        }
    }
    @Override
    public Map<String, Object> getLoginUsers() {
        return redisUtil.search("USER:TOKEN:*");
    }

    @NacosConfig(group = "DEFAULT_GROUP", dataId = "admin-service.yml", key = "tokens.output")
    private String tokensOutput;
    @Override
    public void writeTokens() {
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
}
