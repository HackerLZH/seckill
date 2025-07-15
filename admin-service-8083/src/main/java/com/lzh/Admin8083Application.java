package com.lzh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class Admin8083Application {
    public static void main(String[] args) {
        SpringApplication.run(Admin8083Application.class, args);
    }
}