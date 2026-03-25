package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.CareRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CareRecordMapper extends BaseMapper<CareRecord> {

    @Select("SELECT * FROM care_record WHERE elderly_id = #{elderlyId} AND deleted = 0 ORDER BY care_time DESC")
    List<CareRecord> selectByElderlyId(@Param("elderlyId") Integer elderlyId);

    @Select("SELECT * FROM care_record WHERE caregiver_id = #{caregiverId} AND deleted = 0 ORDER BY care_time DESC")
    List<CareRecord> selectByCaregiverId(@Param("caregiverId") Integer caregiverId);

    @Select("SELECT * FROM care_record WHERE care_plan_id = #{carePlanId} AND deleted = 0 ORDER BY care_time DESC")
    List<CareRecord> selectByCarePlanId(@Param("carePlanId") Integer carePlanId);

    @Select("SELECT * FROM care_record WHERE status = #{status} AND deleted = 0 ORDER BY care_time DESC")
    List<CareRecord> selectByStatus(@Param("status") Integer status);

    @Select("SELECT * FROM care_record WHERE care_type = #{careType} AND deleted = 0 ORDER BY care_time DESC")
    List<CareRecord> selectByCareType(@Param("careType") Integer careType);

    @Select("SELECT * FROM care_record WHERE DATE(care_time) = CURDATE() AND deleted = 0 ORDER BY care_time DESC")
    List<CareRecord> selectTodayRecords();

    @Select("SELECT * FROM care_record WHERE care_time BETWEEN #{startTime} AND #{endTime} AND deleted = 0 ORDER BY care_time DESC")
    List<CareRecord> selectByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Update("UPDATE care_record SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM care_record WHERE caregiver_id = #{caregiverId} AND status = 3 AND deleted = 0")
    Long countCompletedByCaregiver(@Param("caregiverId") Integer caregiverId);

    @Select("SELECT COUNT(*) FROM care_record WHERE elderly_id = #{elderlyId} AND status = 3 AND deleted = 0")
    Long countCompletedByElderly(@Param("elderlyId") Integer elderlyId);
}
