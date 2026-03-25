package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.HealthData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 健康数据数据访问层
 */
@Mapper
public interface HealthDataMapper extends BaseMapper<HealthData> {

    /**
     * 根据老人ID查询健康数据列表
     */
    @Select("SELECT h.*, u.real_name as elderly_name, u.room_number " +
            "FROM health_data h " +
            "LEFT JOIN user u ON h.elderly_id = u.id " +
            "WHERE h.elderly_id = #{elderlyId} AND h.deleted = 0 " +
            "ORDER BY h.record_time DESC")
    List<HealthData> selectByElderlyId(@Param("elderlyId") Integer elderlyId);

    /**
     * 查询最新的健康数据
     */
    @Select("SELECT h.*, u.real_name as elderly_name, u.room_number " +
            "FROM health_data h " +
            "LEFT JOIN user u ON h.elderly_id = u.id " +
            "WHERE h.deleted = 0 " +
            "ORDER BY h.record_time DESC " +
            "LIMIT #{limit}")
    List<HealthData> selectLatest(@Param("limit") Integer limit);
}
