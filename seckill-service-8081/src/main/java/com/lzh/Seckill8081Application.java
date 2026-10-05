package com.lzh;

import com.feiniaojin.gracefulresponse.EnableGracefulResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@EnableGracefulResponse
@SpringBootApplication
public class Seckill8081Application {
    public static void main(String[] args) {
        SpringApplication.run(Seckill8081Application.class, args);
    }
}