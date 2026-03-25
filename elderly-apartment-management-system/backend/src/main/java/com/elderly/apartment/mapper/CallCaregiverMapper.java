package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.CallCaregiver;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CallCaregiverMapper extends BaseMapper<CallCaregiver> {

    @Select("SELECT * FROM call_caregiver WHERE elderly_id = #{elderlyId} AND deleted = 0 ORDER BY create_time DESC")
    List<CallCaregiver> selectByElderlyId(@Param("elderlyId") Integer elderlyId);

    @Select("SELECT * FROM call_caregiver WHERE status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<CallCaregiver> selectByStatus(@Param("status") Integer status);

    @Select("SELECT * FROM call_caregiver WHERE assigned_nurse_id = #{nurseId} AND deleted = 0 ORDER BY create_time DESC")
    List<CallCaregiver> selectByNurseId(@Param("nurseId") Integer nurseId);

    @Select("SELECT * FROM call_caregiver WHERE deleted = 0 ORDER BY CASE status " +
            "WHEN 1 THEN 0 WHEN 2 THEN 1 WHEN 3 THEN 2 WHEN 4 THEN 3 ELSE 4 END, create_time DESC")
    List<CallCaregiver> selectAllOrderByStatus();
}
