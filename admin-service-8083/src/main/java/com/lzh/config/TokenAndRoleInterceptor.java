package com.lzh.config;

import com.lzh.entity.UserInfo;
import com.lzh.enums.Role;
import com.lzh.utils.Constants;
import com.lzh.utils.RedisUtil;
import com.lzh.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

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
        try {
            String token = request.getHeader(Constants.TOKEN_HEADER);
            log.info(token);
            // token不存在，说明是无需鉴权请求，放行
            if (Objects.isNull(token)) {
                return true;
            }
            UserInfo user = (UserInfo)redisUtil.get(Constants.TOKEN_KEY + token);
            log.info("{}访问", user.getUsername());
            if (user.getRole().equals(Role.U)) {
                log.info("{} is not admin", user.getUsername());
                return false;
            }
            UserHolder.saveUser(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
