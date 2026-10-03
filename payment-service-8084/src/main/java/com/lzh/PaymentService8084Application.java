package com.lzh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class PaymentService8084Application {

    public static void main(String[] args) {
        SpringApplication.run(PaymentService8084Application.class, args);
}

}
