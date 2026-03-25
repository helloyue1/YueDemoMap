package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.EmergencyContact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmergencyContactMapper extends BaseMapper<EmergencyContact> {

    @Select("SELECT * FROM emergency_contact WHERE status = 1 AND deleted = 0 ORDER BY sort_order ASC, create_time DESC")
    List<EmergencyContact> selectActiveContacts();

    @Select("SELECT * FROM emergency_contact WHERE deleted = 0 ORDER BY sort_order ASC, create_time DESC")
    List<EmergencyContact> selectAllOrderBySort();

    @Select("SELECT * FROM emergency_contact WHERE contact_type = #{type} AND status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<EmergencyContact> selectByType(@Param("type") String type);
}
