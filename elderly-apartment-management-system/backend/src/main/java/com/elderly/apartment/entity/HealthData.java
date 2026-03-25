package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 健康数据实体类（日常健康监测记录）
 */
@Data
@TableName("health_data")
public class HealthData implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 老人ID
     */
    @TableField("elderly_id")
    private Integer elderlyId;

    /**
     * 记录类型：1-血压测量, 2-血糖监测, 3-体重测量, 4-心率测量, 5-体温测量, 6-血氧测量, 7-综合记录
     */
    @TableField("record_type")
    private Integer recordType;

    /**
     * 收缩压（高压）mmHg
     */
    @TableField("systolic_pressure")
    private Integer systolicPressure;

    /**
     * 舒张压（低压）mmHg
     */
    @TableField("diastolic_pressure")
    private Integer diastolicPressure;

    /**
     * 血压（格式：收缩压/舒张压）
     */
    @TableField("blood_pressure")
    private String bloodPressure;

    /**
     * 心率（次/分钟）
     */
    @TableField("heart_rate")
    private Integer heartRate;

    /**
     * 体温（℃）
     */
    @TableField("body_temperature")
    private Double bodyTemperature;

    /**
     * 血糖（mmol/L）
     */
    @TableField("blood_sugar")
    private Double bloodSugar;

    /**
     * 血氧饱和度（%）
     */
    @TableField("blood_oxygen")
    private Integer bloodOxygen;

    /**
     * 体重（kg）
     */
    @TableField("weight")
    private Double weight;

    /**
     * 记录内容摘要
     */
    @TableField("record_content")
    private String recordContent;

    /**
     * 健康备注
     */
    @TableField("health_note")
    private String healthNote;

    /**
     * 记录人ID
     */
    @TableField("recorder_id")
    private Integer recorderId;

    /**
     * 记录人姓名
     */
    @TableField("recorder_name")
    private String recorderName;

    /**
     * 记录时间
     */
    @TableField("record_time")
    private LocalDateTime recordTime;

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
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    // 非数据库字段，用于关联查询
    @TableField(exist = false)
    private String elderlyName;

    @TableField(exist = false)
    private String roomNumber;
}
