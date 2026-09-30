package com.lzh.enums;

/**
 * 订单状态
 */
public enum OrderStatus {
    WAIT("待支付"),
    PAID("已支付"),
    CANCEL("已取消");

    private String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
