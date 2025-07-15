package com.lzh.service;

import com.lzh.response.Result;

public interface IAdminService {

    Result getRegisterUsers();

	Result addUsers(Integer beginId, Integer endId);

    Result loginUsers(Integer beginId, Integer endId);

    Result getLoginUsers();

    Result writeTokens();

}
