package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.FamilyMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FamilyMemberMapper extends BaseMapper<FamilyMember> {

    @Select("SELECT fm.*, u.real_name as elderly_name, u.room_number " +
            "FROM family_member fm " +
            "LEFT JOIN user u ON fm.elderly_id = u.id " +
            "WHERE fm.elderly_id = #{elderlyId} AND fm.deleted = 0")
    List<FamilyMember> selectByElderlyId(@Param("elderlyId") Long elderlyId);

    @Select("SELECT fm.*, u.real_name as elderly_name, u.room_number " +
            "FROM family_member fm " +
            "LEFT JOIN user u ON fm.elderly_id = u.id " +
            "WHERE fm.id = #{id} AND fm.deleted = 0")
    FamilyMember selectByIdWithElderly(@Param("id") Long id);

    @Select("SELECT fm.*, u.real_name as elderly_name, u.room_number " +
            "FROM family_member fm " +
            "LEFT JOIN user u ON fm.elderly_id = u.id " +
            "WHERE fm.deleted = 0")
    List<FamilyMember> selectAllWithElderly();
}
