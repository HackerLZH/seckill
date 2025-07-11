package com.lzh.enums;

/**
 * 对象激活状态枚举
 */
public enum ActiveStatus {
    ACTIVE("激活"), 
    INACTIVE("未激活");

    private String value;

    ActiveStatus(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
