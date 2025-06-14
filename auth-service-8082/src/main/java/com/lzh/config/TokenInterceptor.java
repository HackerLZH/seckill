package com.lzh.config;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.lzh.entity.User;
import com.lzh.util.UserHolder;
import com.lzh.utils.Constants;
import com.lzh.utils.RedisUtil;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 录入Token对应用户信息
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisUtil redisUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(Constants.TOKEN_HEADER);
        // token不存在，说明是无需鉴权请求，放行
        if (Objects.isNull(token)) {
            return true;
        }
        // 获取登录用户信息
        User user = JSONUtil.toBean(redisUtil.get(Constants.TOKEN_KEY + token), User.class);
        UserHolder.saveUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
