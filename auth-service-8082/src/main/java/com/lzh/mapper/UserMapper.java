package com.lzh.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.lzh.entity.User;

@Mapper
public interface UserMapper {
    @Select("select * from auth_user where username = #{username}")
    User getUserByName(String username);

    @Select("insert into auth_user (username, password, create_time) values (#{username}, #{password}, #{createTime})")
    void save(User user);

}
