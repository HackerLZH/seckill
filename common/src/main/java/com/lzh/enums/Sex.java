package com.lzh.enums;

public enum Sex {
    MALE("男"),
    FEMALE("女"),
    UNKNOWN("未知");

    private String value;

    Sex (String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
