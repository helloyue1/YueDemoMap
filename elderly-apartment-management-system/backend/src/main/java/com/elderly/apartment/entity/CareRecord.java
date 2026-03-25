package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("care_record")
public class CareRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("elderly_id")
    private Integer elderlyId;

    @TableField("elderly_name")
    private String elderlyName;

    @TableField("room_number")
    private String roomNumber;

    @TableField("care_plan_id")
    private Integer carePlanId;

    @TableField("caregiver_id")
    private Integer caregiverId;

    @TableField("caregiver_name")
    private String caregiverName;

    @TableField("care_type")
    private Integer careType;

    @TableField("care_content")
    private String careContent;

    @TableField("care_duration")
    private Integer careDuration;

    @TableField("care_effect")
    private Integer careEffect;

    @TableField("elderly_condition")
    private String elderlyCondition;

    @TableField("abnormal_situation")
    private String abnormalSituation;

    @TableField("notes")
    private String notes;

    @TableField("care_time")
    private LocalDateTime careTime;

    @TableField("status")
    private Integer status;

    @TableField("image_urls")
    private String imageUrls;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    // 非数据库字段
    @TableField(exist = false)
    private String careTypeText;

    @TableField(exist = false)
    private String statusText;

    @TableField(exist = false)
    private String careEffectText;

    public String getCareTypeText() {
        if (careType == null) return "";
        switch (careType) {
            case 1: return "晨间护理";
            case 2: return "晚间护理";
            case 3: return "饮食护理";
            case 4: return "康复训练";
            case 5: return "用药协助";
            case 6: return "其他";
            default: return "未知";
        }
    }

    public String getStatusText() {
        if (status == null) return "";
        switch (status) {
            case 1: return "待执行";
            case 2: return "执行中";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知";
        }
    }

    public String getCareEffectText() {
        if (careEffect == null) return "";
        switch (careEffect) {
            case 1: return "良好";
            case 2: return "一般";
            case 3: return "较差";
            default: return "未知";
        }
    }
}
