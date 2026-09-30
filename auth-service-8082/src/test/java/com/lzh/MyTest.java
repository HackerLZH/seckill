package com.lzh;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.lzh.entity.UserInfo;
import com.lzh.mapper.UserMapper;
import com.lzh.utils.RedisUtil;

@SpringBootTest
public class MyTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void testLogin() {
        UserInfo user = userMapper.getUserByName("lzh");
        System.out.println(user);
    }

    @Test
    public void test_Redis_set() {
        UserInfo user = userMapper.getUserByName("lzh");
        redisUtil.set("user", user, 7 * 24 * 60);
    }

    @Test
    public void test_Redis_get() {
        UserInfo user = (UserInfo)redisUtil.get("user");
        System.out.println(user);
    }
}
