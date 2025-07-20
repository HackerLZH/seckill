package com.lzh.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserLoginVO {
    private Integer userId;
    private String token;
}
