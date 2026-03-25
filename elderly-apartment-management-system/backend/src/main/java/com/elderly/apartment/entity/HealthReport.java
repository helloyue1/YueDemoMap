package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 体检报告实体类
 */
@Data
@TableName("health_report")
public class HealthReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 老人ID
     */
    @TableField("elderly_id")
    private Integer elderlyId;

    /**
     * 报告类型：1-月度，2-季度，3-年度
     */
    @TableField("report_type")
    private Integer reportType;

    /**
     * 报告日期
     */
    @TableField("report_date")
    private LocalDate reportDate;

    /**
     * 统计开始日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 统计结束日期
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 健康评分(0-100)
     */
    @TableField("health_score")
    private Integer healthScore;

    /**
     * 总体评估
     */
    @TableField("overall_assessment")
    private String overallAssessment;

    /**
     * 健康建议
     */
    @TableField("recommendations")
    private String recommendations;

    /**
     * 审核医生ID
     */
    @TableField("doctor_id")
    private Integer doctorId;

    /**
     * 审核医生姓名
     */
    @TableField("doctor_name")
    private String doctorName;

    /**
     * 审核时间
     */
    @TableField("audit_time")
    private LocalDateTime auditTime;

    /**
     * 审核意见
     */
    @TableField("audit_opinion")
    private String auditOpinion;

    /**
     * 状态：0-待审核，1-已审核
     */
    @TableField("status")
    private Integer status;

    /**
     * PDF文件URL
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    // 非数据库字段
    @TableField(exist = false)
    private String elderlyName;

    @TableField(exist = false)
    private String roomNumber;

    @TableField(exist = false)
    private List<HealthReportDetail> details;
}
