package com.lzh.entity;

import java.time.LocalDateTime;

import com.lzh.enums.ActiveStatus;
import com.lzh.enums.Sex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户完整信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Integer id;
    private String username;
    private String password;
    private Integer age;
    private Sex sex;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime createTime;
    private ActiveStatus isActive;
}
