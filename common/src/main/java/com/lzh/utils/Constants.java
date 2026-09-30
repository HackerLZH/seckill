package com.lzh.utils;

import com.feiniaojin.gracefulresponse.defaults.DefaultResponseStatus;

public final class Constants {
    private Constants() {}
    public final static String TOKEN_KEY = "USER:TOKEN:";
    public final static String TOKEN_HEADER = "SeckillAuthorization";

    // Graceful Code
    public static final class GracefulCode {
        private GracefulCode() {}
        public static final String NOT_BLANK_CODE = "520000";
        public static final String NOT_BLANK_MESSAGE = "字段不能为空";
        public static final String LOGIN_DUPICATE_CODE = "520001";
        public static final String LOGIN_DUPICATE_MESSAGE = "请勿重复登陆";
        public static final String NO_USER_CODE = "520002";
        public static final String NO_USER_MESSAGE = "用户不存在";
        public static final String WRONG_PASSWORD_CODE = "520003";
        public static final String WRONG_PASSWORD_MESSAGE = "密码错误";
        public static final String USER_EXIST_CODE = "520004";
        public static final String USER_EXIST_MESSAGE = "用户名已存在";
        public static final String PASSWORD_INCONSISTENT_CODE = "520005";
        public static final String PASSWORD_INCONSISTENT_MESSAGE = "密码不一致";
    }
    public static final DefaultResponseStatus NOT_BLANK = new DefaultResponseStatus(GracefulCode.NOT_BLANK_CODE, GracefulCode.NOT_BLANK_MESSAGE);
    public static final DefaultResponseStatus LOGIN_DUPICATE = new DefaultResponseStatus(GracefulCode.LOGIN_DUPICATE_CODE, GracefulCode.LOGIN_DUPICATE_MESSAGE);
    public static final DefaultResponseStatus NO_USER = new DefaultResponseStatus(GracefulCode.NO_USER_CODE, GracefulCode.NO_USER_MESSAGE);
    public static final DefaultResponseStatus WRONG_PASSWORD = new DefaultResponseStatus(GracefulCode.WRONG_PASSWORD_CODE, GracefulCode.WRONG_PASSWORD_MESSAGE);
    public static final DefaultResponseStatus USER_EXIST = new DefaultResponseStatus(GracefulCode.USER_EXIST_CODE, GracefulCode.USER_EXIST_MESSAGE);
    public static final DefaultResponseStatus PASSWORD_INCONSISTENT = new DefaultResponseStatus(GracefulCode.PASSWORD_INCONSISTENT_CODE, GracefulCode.PASSWORD_INCONSISTENT_MESSAGE);
}
