package com.lzh.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Address {
    private Integer id;
    private Integer userId;
    private String text; // 详细地址
    private String name; // 收货人姓名
    private String phone; // 收货人电话
}
