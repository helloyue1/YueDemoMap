package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.HealthReport;

import java.util.Map;

/**
 * 体检报告服务接口
 */
public interface HealthReportService extends IService<HealthReport> {

    /**
     * 分页查询报告列表
     */
    Page<HealthReport> getReportPage(Integer page, Integer size, String elderlyName, 
                                      Integer reportType, String startDate, String endDate);

    /**
     * 生成体检报告
     */
    HealthReport generateReport(Integer elderlyId, Integer reportType, String reportDate);

    /**
     * 获取报告详情
     */
    HealthReport getReportDetail(Integer id);

    /**
     * 审核报告
     */
    boolean auditReport(Integer id, Integer doctorId, String doctorName, String auditOpinion);

    /**
     * 获取健康统计数据
     */
    Map<String, Object> getHealthStatistics(Integer elderlyId);
}
