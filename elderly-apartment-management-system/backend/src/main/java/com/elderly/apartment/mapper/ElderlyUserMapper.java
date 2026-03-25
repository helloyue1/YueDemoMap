package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.ElderlyUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ElderlyUserMapper extends BaseMapper<ElderlyUser> {

    /**
     * 根据用户ID查询老人用户关联
     */
    @Select("SELECT * FROM elderly_user WHERE user_id = #{userId}")
    ElderlyUser selectByUserId(@Param("userId") Integer userId);

    /**
     * 根据老人ID查询老人用户关联
     */
    @Select("SELECT * FROM elderly_user WHERE elderly_id = #{elderlyId}")
    ElderlyUser selectByElderlyId(@Param("elderlyId") Integer elderlyId);
}
