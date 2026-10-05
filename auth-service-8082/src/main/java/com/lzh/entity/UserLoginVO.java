package com.lzh.entity;

import com.lzh.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class UserLoginVO {
    private Integer userId;
    private String username;
    private String token;
    private Role role;
    private LocalDateTime loginTime;
    private LocalDateTime expireTime;
}
