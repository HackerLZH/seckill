package com.lzh.utils;

import java.util.Date;

import cn.hutool.jwt.JWT;

/**
 * Hutool-jwt工具类
 */
public class JwtUtil {
    // 密钥
    private static final String KEY = "lzh";

    // 过期时间 7天
    // private static final long EXPIRE = 7 * 24 * 3600;
    /**
     * 根据用户名生成token
     * @param username
     * @return
     */
    public static String createToken(String username) {
        return JWT.create()
                .setPayload("username", username)
                .setKey(KEY.getBytes())
                // .setExpiresAt(new Date(System.currentTimeMillis() + EXPIRE * 1000))
                .sign();
    }

    /**
     * 验证token
     * @param token
     * @return
     */
    public static boolean verify(String token) {
        return JWT.of(token).setKey(KEY.getBytes()).verify();
    }

    /**
     * 验证token并返回用户名
     * @param token
     * @return
     */
    // public static String verifyAndgetUsername(String token) {
    //     if (!verify(token)) {
    //         return null;
    //     }
    //     return JWT.of(token).getPayload("username").toString();
    // }
}
