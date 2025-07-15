package com.lzh.entity;

import java.time.LocalDateTime;

import com.lzh.enums.ActiveStatus;
import com.lzh.enums.Role;

import lombok.Data;

@Data
public class UserDTO1 {
    private Integer id;
    private String username;
    private String password;
    private LocalDateTime createTime;
    private ActiveStatus isActive;
    private Role role;
}
