package com.lzh.service;

import com.lzh.entity.*;

import java.util.List;


public interface IUserService {
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    UserLoginVO login(String username, String password);

    /**
     * 用户注册
     * @param userdto
     * @return
     */
    void register(UserDTO userdto);

    /**
     * 用户注销
     * @return
     */
    void logout(String token);

    /**
     * 用户信息
     * @param username
     * @return
     */
    UserInfo getUserInfo(String username);

    /**
     * 更新用户信息
     * @param userInfo
     * @return 更新是否成功
     */
    boolean updateUserInfo(UserInfo userInfo);

    /**
     * 获取用户地址
     * @param default_address_id 默认地址
     * @return
     */
    List<Address> getUserAddress(Integer default_address_id);

    /**
     * 添加地址
     * @param addressAddDTO
     */
    void addAddress(AddressAddDTO addressAddDTO);
}
