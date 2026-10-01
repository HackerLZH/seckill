package com.lzh.mapper;

import com.lzh.entity.GoodsDTO;
import com.lzh.entity.GoodsKillDTO;
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

    @Insert("insert into goods(name,price,stock,image,create_time,is_active) " +
            "values(#{name},#{price},#{stock},#{image},now(),#{isActive})")
    void saveGoods(GoodsDTO goodsDTO);

    @Insert("insert into goods_kill(goods_id,stock,start_time,end_time,create_time,is_active) " +
            "values(#{goodsId},#{stock},#{startTime},#{endTime},now(),#{isActive})")
    void saveKillGoods(GoodsKillDTO goodsKillDTO);
}
