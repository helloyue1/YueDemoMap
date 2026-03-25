package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 体检报告明细实体类
 */
@Data
@TableName("health_report_detail")
public class HealthReportDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 报告ID
     */
    @TableField("report_id")
    private Integer reportId;

    /**
     * 检查项目
     */
    @TableField("check_item")
    private String checkItem;

    /**
     * 检查值
     */
    @TableField("check_value")
    private String checkValue;

    /**
     * 单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 参考范围
     */
    @TableField("reference_range")
    private String referenceRange;

    /**
     * 结果：1-正常，2-异常
     */
    @TableField("result")
    private Integer result;

    /**
     * 趋势：1-上升，2-下降，3-平稳
     */
    @TableField("trend")
    private Integer trend;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
