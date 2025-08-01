package com.lzh.service;

import com.lzh.entity.UserLoginVO;
import com.lzh.entity.UserDTO;


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

}
