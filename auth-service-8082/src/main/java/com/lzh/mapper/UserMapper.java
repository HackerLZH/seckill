package com.lzh.mapper;

import com.lzh.entity.Address;
import com.lzh.entity.UserInfo;
import com.lzh.entity.UserLoginVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username}")
    UserInfo getUserByName(String username);

    @Select("insert into user (username, password, create_time, role) values (#{username}, #{password}, #{createTime}, #{role})")
    void save(UserInfo user);

    @Update("update user set login_time = #{loginTime} where id = #{userId}")
    int update(UserLoginVO loginUser);

    @Update("<script>" +
            "update user" +
            "<set>" +
            "  <if test='age != null'>age = #{age},</if>" +
            "  <if test='sex != null'>sex = #{sex},</if>" +
            "  <if test='email != null'>email = #{email},</if>" +
            "  <if test='phone != null'>phone = #{phone},</if>" +
            "  <if test='defaultAddressId != null'>default_address_id = #{defaultAddressId},</if>" +
            "</set>" +
            "where username = #{username}" +
            "</script>")
    int updateUserInfo(UserInfo userInfo);

    @Select("select a.* " +
            "from address a " +
            "inner join user u on a.user_id = u.id " +
            "where u.default_address_id = #{default_address_id}")
    List<Address> findAllAddressById(Integer default_address_id);

    @Insert("insert into address(user_id,text,name,phone) values(#{userId},#{text},#{name},#{phone})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id") // 主键回填
    void insertAddress(Address address);
}
