package com.lzh.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.cloud.nacos.annotation.NacosConfig;
import com.feiniaojin.gracefulresponse.GracefulResponse;
import com.lzh.entity.UserInfo;
import com.lzh.entity.UserLoginVO;
import com.lzh.entity.UserRegisterDTO;
import com.lzh.mapper.UserMapper;
import com.lzh.utils.UserHolder;
import com.lzh.utils.Constants;
import com.lzh.utils.JwtUtil;
import com.lzh.utils.RedisUtil;

import cn.hutool.core.bean.BeanUtil;

@Service
public class UserServiceImpl implements IUserService{
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;

    @NacosConfig(dataId = "common.yml", group = "EXT_GROUP", key = "token.timeout")
    private Integer tokenTimeout;
    @Override
    public UserLoginVO login(String username, String password) {
        // 判断是否已登录
        if (!Objects.isNull(UserHolder.getUser())) {
            GracefulResponse.raiseException(Constants.LOGIN_DUPICATE);
        }
        // 用户名是否存在
        UserInfo user = userMapper.getUserByName(username);
        if (Objects.isNull(user)) {
            GracefulResponse.raiseException(Constants.NO_USER);
        }

        // 密码是否正确
        if (!password.equals(user.getPassword())) {
            GracefulResponse.raiseException(Constants.WRONG_PASSWORD);
        }

        // 生成token
        String token = JwtUtil.createToken(username, password);

        // redis保存token
        redisUtil.set(Constants.TOKEN_KEY + token, user, tokenTimeout);
        
        return UserLoginVO.builder().userId(user.getId()).token(token).build();
    }

    @Override
    public void logout(String token) {
        // 清理redis
        redisUtil.expire(Constants.TOKEN_KEY + token, 0);
    }

    @Override
    public void register(UserRegisterDTO userdto) {
        // 字段不能为空
        if (StringUtils.isBlank(userdto.getUsername()) || StringUtils.isBlank(userdto.getPassword()) || StringUtils.isBlank(userdto.getConfirmPassword())) {
            GracefulResponse.raiseException(Constants.NOT_BLANK);
        }
        // 用户名不能重复
        if (!Objects.isNull(userMapper.getUserByName(userdto.getUsername()))) {
            GracefulResponse.raiseException(Constants.USER_EXIST);
        }
        // TODO: 密码校验

        // 密码一致
        if (!userdto.getPassword().equals(userdto.getConfirmPassword())) {
            GracefulResponse.raiseException(Constants.PASSWORD_INCONSISTENT);
        }
        // TODO: 密码加密

        // 保存用户
        UserInfo user = new UserInfo();
        BeanUtil.copyProperties(userdto, user);
        user.setCreateTime(LocalDateTime.now());
        userMapper.save(user);
    }

}
