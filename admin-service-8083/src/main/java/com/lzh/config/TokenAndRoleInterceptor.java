package com.lzh.config;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.jayway.jsonpath.internal.filter.LogicalExpressionNode;
import com.lzh.entity.UserInfo;
import com.lzh.enums.Role;
import com.lzh.utils.UserHolder;
import com.lzh.utils.Constants;
import com.lzh.utils.RedisUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 录入Token对应用户信息，只对用户角色A放行
 */
@Slf4j
@Component
public class TokenAndRoleInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisUtil redisUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(Constants.TOKEN_HEADER);
        // token不存在，说明是无需鉴权请求，放行
        if (Objects.isNull(token)) {
            log.info("pass with no token.");
            return true;
        }
        UserInfo user = (UserInfo)redisUtil.get(Constants.TOKEN_KEY + token);
        if (user.getRole().equals(Role.U)) {
            log.info("{} is not admin", user.getUsername());
            return false;
        }
        UserHolder.saveUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
