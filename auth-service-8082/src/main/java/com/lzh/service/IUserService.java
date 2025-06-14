package com.lzh.service;

import com.lzh.entity.UserRegisterDTO;
import com.lzh.response.Result;

public interface IUserService {
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    Result login(String username, String password);

    /**
     * 用户注册
     * @param userdto
     * @return
     */
    Result register(UserRegisterDTO userdto);

    /**
     * 用户注销
     * @return
     */
    Result logout(String token);

}
