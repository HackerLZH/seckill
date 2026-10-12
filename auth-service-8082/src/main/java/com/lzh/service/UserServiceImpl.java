package com.lzh.service;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.cloud.nacos.annotation.NacosConfig;
import com.feiniaojin.gracefulresponse.GracefulResponse;
import com.lzh.config.CacheConfig;
import com.lzh.entity.*;
import com.lzh.enums.Role;
import com.lzh.mapper.UserMapper;
import com.lzh.utils.Constants;
import com.lzh.utils.JwtUtil;
import com.lzh.utils.RedisUtil;
import com.lzh.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl implements IUserService{
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CacheManager cacheManager;

    @NacosConfig(dataId = "common.yml", group = "EXT_GROUP", key = "token.timeout")
    private Integer tokenTimeout; // 分钟
    @Override
    public UserLoginVO login(String username, String password) {
        // 判断是否已登录
        if (!Objects.isNull(UserHolder.getUser())) {
            // 前端localStorage意外清空
            log.warn("{}重新登录", username);
//            GracefulResponse.raiseException(Constants.LOGIN_DUPICATE);
        }
        // 用户名是否存在
        UserInfo user = userMapper.getUserByName(username);
        if (Objects.isNull(user)) {
            log.error("无效用户{}", username);
            GracefulResponse.raiseException(Constants.NO_USER);
        }

        // 密码是否正确
        if (!password.equals(user.getPassword())) {
            log.error("密码错误：{}", password);
            GracefulResponse.raiseException(Constants.WRONG_PASSWORD);
        }
        try {
            // 生成token
            String token = JwtUtil.createToken(username, password);
            LocalDateTime now = LocalDateTime.now();
            // 更新login time
            user.setLoginTime(now);
            // redis保存token
            redisUtil.set(Constants.TOKEN_KEY + token, user, tokenTimeout, TimeUnit.MINUTES);
            log.info("{} {}登录: {}", now, username, token);

            UserLoginVO loginUser = UserLoginVO.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .token(token)
                    .role(user.getRole())
                    .loginTime(now)
                    .expireTime(now.plusMinutes(tokenTimeout))
                    .build();
            if (userMapper.update(loginUser) == 1) {
                return loginUser;
            }
            log.error("更新用户登录时间失败");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void logout(String token) {
        try {
            // 清理redis
            UserInfo user = (UserInfo) redisUtil.get(Constants.TOKEN_KEY + token);
            if (!Objects.isNull(user)) {
                redisUtil.expire(Constants.TOKEN_KEY + token, 0);
                log.info("{}登出", user.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(UserDTO userdto) {

        // 字段不能为空
        // if (StringUtils.isBlank(userdto.getUsername()) || StringUtils.isBlank(userdto.getPassword()) || StringUtils.isBlank(userdto.getConfirmPassword())) {
        //     GracefulResponse.raiseException(Constants.NOT_BLANK);
        // }
        // 用户名不能重复
        if (!Objects.isNull(userMapper.getUserByName(userdto.getUsername()))) {
            GracefulResponse.raiseException(Constants.USER_EXIST);
        }
        try {
            // TODO: 密码校验

            // 密码一致
            // if (!userdto.getPassword().equals(userdto.getConfirmPassword())) {
            //     GracefulResponse.raiseException(Constants.PASSWORD_INCONSISTENT);
            // }
            // TODO: 密码加密

            // 保存用户
            UserInfo user = new UserInfo();
            BeanUtil.copyProperties(userdto, user);
            user.setCreateTime(LocalDateTime.now());
            if (user.getUsername().startsWith("@admin@")) {
                user.setRole(Role.A);
            } else {
                user.setRole(Role.U);
            }
            userMapper.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Cacheable(value = CacheConfig.CACHE_AUTH, key = "'UserInfo:' + #p0")
    @Override
    public UserInfo getUserInfo(String username) {
        return userMapper.getUserByName(username);
    }

    @CacheEvict(value = CacheConfig.CACHE_AUTH, key = "'UserInfo:' + #p0.username")
    @Override
    public boolean updateUserInfo(UserInfo userInfo) {
        try {
            int result = userMapper.updateUserInfo(userInfo);
            if (result > 0) {
                log.info("用户信息更新成功: {}", userInfo.getUsername());
                return true;
            }
        } catch (Exception e) {
            log.error("更新用户信息异常", e);
        }
        return false;
    }

    @Cacheable(value = CacheConfig.CACHE_AUTH, key = "'address:' + #p0")
    @Override
    public List<Address> getUserAddress(Integer default_address_id) {
        try {
            return userMapper.findAllAddressById(default_address_id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    @Override
    public void addAddress(AddressAddDTO addressAddDTO) {
        try {
            UserInfo user = userMapper.getUserByName(addressAddDTO.getUsername());
            Address address = Address.builder()
                    .userId(user.getId())
                    .text(addressAddDTO.getDetailAddress())
                    .name(addressAddDTO.getReceiverName())
                    .phone(addressAddDTO.getReceiverPhone())
                    .build();
            userMapper.insertAddress(address);
            // 第一条为默认地址
            if (userMapper.findAllAddressById(address.getId()).size() == 1) {
                userMapper.updateUserInfo(UserInfo.builder()
                        .username(addressAddDTO.getUsername())
                        .defaultAddressId(address.getId())
                        .build()
                );
            }
            // 清除地址缓存
            Objects.requireNonNull(cacheManager.getCache(CacheConfig.CACHE_AUTH))
                    .evict("address:" + user.getDefaultAddressId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
