package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.HealthReportDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 体检报告明细Mapper
 */
@Mapper
public interface HealthReportDetailMapper extends BaseMapper<HealthReportDetail> {

    /**
     * 根据报告ID查询明细列表
     */
    @Select("SELECT * FROM health_report_detail WHERE report_id = #{reportId}")
    List<HealthReportDetail> selectByReportId(@Param("reportId") Integer reportId);
}
