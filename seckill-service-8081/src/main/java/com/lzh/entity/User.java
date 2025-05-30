package com.lzh.entity;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Integer id;
    private String name;
    private String password;
    private Integer age;
    private Character sex;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime createTime; 
    private Integer isActive;
}
