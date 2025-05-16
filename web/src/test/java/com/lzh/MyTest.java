package com.lzh;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class MyTest {
    @Autowired
    Snowflake snowflake;

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
}
