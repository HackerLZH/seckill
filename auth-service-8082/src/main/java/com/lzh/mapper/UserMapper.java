package com.lzh.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.lzh.entity.UserInfo;

@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username}")
    UserInfo getUserByName(String username);

    @Select("insert into user (username, password, create_time) values (#{username}, #{password}, #{createTime})")
    void save(UserInfo user);

}
