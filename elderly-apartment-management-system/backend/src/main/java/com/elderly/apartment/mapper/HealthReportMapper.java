package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.HealthReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 体检报告Mapper
 */
@Mapper
public interface HealthReportMapper extends BaseMapper<HealthReport> {

    /**
     * 查询报告详情（包含明细）
     */
    @Select("SELECT r.*, u.real_name as elderlyName, u.room_number as roomNumber " +
            "FROM health_report r " +
            "LEFT JOIN user u ON r.elderly_id = u.id " +
            "WHERE r.id = #{id} AND r.deleted = 0")
    HealthReport selectReportWithDetail(@Param("id") Integer id);

    /**
     * 查询老人的健康档案列表（用于生成报告）
     */
    @Select("SELECT * FROM health_record " +
            "WHERE elderly_id = #{elderlyId} " +
            "AND check_date BETWEEN #{startDate} AND #{endDate} " +
            "AND deleted = 0 " +
            "ORDER BY check_date DESC")
    List<com.elderly.apartment.entity.HealthRecord> selectHealthRecordsForReport(
            @Param("elderlyId") Integer elderlyId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);
}
