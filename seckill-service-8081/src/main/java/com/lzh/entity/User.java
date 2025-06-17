package com.lzh.entity;

import cn.hutool.core.annotation.Alias;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Integer id;
    @Alias("username")
    private String name;
    private Integer age;
    private Character sex;
    private String email;
    private String phone;
    private String address;
    private Integer isActive;
}
