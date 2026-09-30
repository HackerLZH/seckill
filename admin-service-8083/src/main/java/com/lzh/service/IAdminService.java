package com.lzh.service;

import java.util.List;
import java.util.Map;

import com.lzh.entity.UserDTO1;

public interface IAdminService {

    List<UserDTO1> getRegisterUsers();

	void addUsers(Integer beginId, Integer endId);

    void loginUsers(Integer beginId, Integer endId);

    Map<String, Object> getLoginUsers();

    void writeTokens();

}
