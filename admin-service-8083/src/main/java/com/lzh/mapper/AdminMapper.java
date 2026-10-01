package com.lzh.mapper;

import com.lzh.entity.UserDTO1;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminMapper {

    @Select("select id,username,password,create_time,is_active,role "
                + " from user "
                + " where username like \"test%\" "
                + " order by id desc "
                + " limit 10")
    List<UserDTO1> findLast10TestUsers();

    @Insert("insert into user(username,password,create_time) values(#{name},'123456',now())")
    void addUserByUsername(String name);

}
