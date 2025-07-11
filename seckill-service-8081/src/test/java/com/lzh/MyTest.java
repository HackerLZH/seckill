package com.lzh;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.lzh.entity.UserInfo;
import com.lzh.utils.RedisUtil;

import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class MyTest {
    @Autowired
    Snowflake snowflake;
    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void test() {    
        for (int i = 0; i < 10; ++i) {
            log.info(String.valueOf(snowflake.nextId()));
        }
    }

    @Test
    public void test_Chinese() {
        log.info("中文");
    }

    @Test
    public void test_LocaDateTime() {
        log.info("{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @Test
    public void test_Redis_get() {
        UserInfo user = (UserInfo)redisUtil.get("user");
        System.out.println(user);
    }
}
