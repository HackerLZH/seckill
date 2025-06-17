package com.lzh.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lzh.entity.User;
import com.lzh.entity.UserRegisterDTO;
import com.lzh.mapper.UserMapper;
import com.lzh.response.Result;
import com.lzh.util.UserHolder;
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

    @Override
    public Result login(String username, String password) {
        // 判断是否已登录
        if (!Objects.isNull(UserHolder.getUser())) {
            return Result.fail("请勿重复登录");
        }
        // 字段不能为空
        if (Objects.isNull(username) || Objects.isNull(password)) {
            return Result.fail("用户名或密码不能为空");
        }
        // 用户名是否存在
        User user = userMapper.getUserByName(username);
        if (Objects.isNull(user)) {
            return Result.fail("用户不存在");
        }

        // 密码是否正确
        if (!password.equals(user.getPassword())) {
            return Result.fail("密码错误");
        }

        // 生成token
        String token = JwtUtil.createToken(username, password);

        // TODO: LocalDateTime类型没有序列化
        // redis保存token
        redisUtil.set(Constants.TOKEN_KEY + token, user, Constants.TOKEN_TIMEOUT);
        
        return Result.success(token);
    }

    @Override
    public Result logout(String token) {
        // 清理redis
        redisUtil.expire(Constants.TOKEN_KEY + token, 0);
        return Result.success();
    }

    @Override
    public Result register(UserRegisterDTO userdto) {
        // 字段不能为空
        if (Objects.isNull(userdto.getUsername()) || Objects.isNull(userdto.getPassword()) || Objects.isNull(userdto.getConfirmPassword())) {
            return Result.fail("用户名或密码不能为空");
        }
        // 用户名不能重复
        if (!Objects.isNull(userMapper.getUserByName(userdto.getUsername()))) {
            return Result.fail("用户名已存在");
        }
        // TODO: 密码校验

        // 密码一致
        if (!userdto.getPassword().equals(userdto.getConfirmPassword())) {
            return Result.fail("密码不一致");
        }
        // TODO: 密码加密

        // 保存用户
        User user = new User();
        BeanUtil.copyProperties(userdto, user);
        user.setCreateTime(LocalDateTime.now());
        userMapper.save(user);
        return Result.success(user);
    }

}
