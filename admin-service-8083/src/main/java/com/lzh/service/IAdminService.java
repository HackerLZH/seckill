package com.lzh.service;

import com.lzh.entity.UserDTO1;

import java.util.List;
import java.util.Map;

public interface IAdminService {

    List<UserDTO1> getTestUsers();

	void addUsers(Integer beginId, Integer endId);

    void loginUsers(Integer beginId, Integer endId);

    Map<String, Object> getLoginUsers();

    void writeTokens();

}
