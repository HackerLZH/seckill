package com.lzh.enums;

public enum Role {
    U("普通用户"),
    A("管理员");

    private String value;

    Role (String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
