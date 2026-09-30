package com.lzh.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.lzh.entity.UserDTO1;

@Mapper
public interface AdminMapper {

    @Select("select id,username,password,create_time,is_active,role "
                + " from user "
                + " where username like \"test%\" "
                + " order by id desc "
                + " limit 10")
    List<UserDTO1> findLast10Users();

    @Insert("insert into user(username,password,create_time) values(#{name},'123456',now())")
    void addUserByUsername(String name);

}
