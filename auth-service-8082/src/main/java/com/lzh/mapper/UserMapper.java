package com.lzh.mapper;

import com.lzh.entity.UserInfo;
import com.lzh.entity.UserLoginVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username}")
    UserInfo getUserByName(String username);

    @Select("insert into user (username, password, create_time, role) values (#{username}, #{password}, #{createTime}, #{role})")
    void save(UserInfo user);

    @Update("update user set login_time = #{loginTime} where id = #{userId}")
    int update(UserLoginVO loginUser);
}
