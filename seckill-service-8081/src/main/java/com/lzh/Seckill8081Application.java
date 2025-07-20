package com.lzh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.feiniaojin.gracefulresponse.EnableGracefulResponse;

@EnableGracefulResponse
@SpringBootApplication
public class Seckill8081Application {
    public static void main(String[] args) {
        SpringApplication.run(Seckill8081Application.class, args);
    }
}