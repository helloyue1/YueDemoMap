package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 健康档案实体类
 */
@Data
@TableName("health_record")
public class HealthRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 老人ID
     */
    @TableField("elderly_id")
    private Integer elderlyId;

    /**
     * 收缩压（高压）
     */
    @TableField("systolic_pressure")
    private Integer systolicPressure;

    /**
     * 舒张压（低压）
     */
    @TableField("diastolic_pressure")
    private Integer diastolicPressure;

    /**
     * 血糖（mmol/L）
     */
    @TableField("blood_sugar")
    private Double bloodSugar;

    /**
     * 心率（次/分钟）
     */
    @TableField("heart_rate")
    private Integer heartRate;

    /**
     * 体温（℃）
     */
    @TableField("temperature")
    private Double temperature;

    /**
     * 体重（kg）
     */
    @TableField("weight")
    private Double weight;

    /**
     * 身高（cm）
     */
    @TableField("height")
    private Double height;

    /**
     * 血氧饱和度（%）
     */
    @TableField("blood_oxygen")
    private Integer bloodOxygen;

    /**
     * 健康状态：1-良好，2-一般，3-较差，4-危急
     */
    @TableField("health_status")
    private Integer healthStatus;

    /**
     * 既往病史
     */
    @TableField("medical_history")
    private String medicalHistory;

    /**
     * 过敏史
     */
    @TableField("allergy_history")
    private String allergyHistory;

    /**
     * 用药记录
     */
    @TableField("medication")
    private String medication;

    /**
     * 诊断结果
     */
    @TableField("diagnosis")
    private String diagnosis;

    /**
     * 检查日期
     */
    @TableField("check_date")
    private LocalDate checkDate;

    /**
     * 检查医生
     */
    @TableField("doctor")
    private String doctor;

    /**
     * 备注
     */
    @TableField("notes")
    private String notes;

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
    private Integer elderlyAge;

    @TableField(exist = false)
    private Integer elderlyGender;

    @TableField(exist = false)
    private String roomNumber;
}
