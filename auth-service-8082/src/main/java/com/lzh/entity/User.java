package com.lzh.entity;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 认证服务的User实体
 */
@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private LocalDateTime createTime; 
}
