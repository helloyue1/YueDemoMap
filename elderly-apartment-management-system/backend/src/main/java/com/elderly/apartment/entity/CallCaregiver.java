package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("call_caregiver")
public class CallCaregiver implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("elderly_id")
    private Integer elderlyId;

    @TableField("elderly_name")
    private String elderlyName;

    @TableField("room_number")
    private String roomNumber;

    @TableField("phone")
    private String phone;

    @TableField("call_type")
    private String callType;

    @TableField("call_reason")
    private String callReason;

    @TableField("urgency_level")
    private Integer urgencyLevel;

    @TableField("assigned_nurse_id")
    private Integer assignedNurseId;

    @TableField("assigned_nurse_name")
    private String assignedNurseName;

    @TableField("response_time")
    private LocalDateTime responseTime;

    @TableField("complete_time")
    private LocalDateTime completeTime;

    @TableField("status")
    private Integer status;

    @TableField("remark")
    private String remark;

    @TableField("create_by")
    private Integer createBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @TableField(exist = false)
    private String callTypeText;

    @TableField(exist = false)
    private String statusText;

    @TableField(exist = false)
    private String urgencyLevelText;

    public String getCallTypeText() {
        if (callType == null) return "";
        switch (callType) {
            case "emergency": return "紧急呼叫";
            case "urgent": return "紧急";
            case "normal": return "普通";
            case "consult": return "咨询";
            default: return "其他";
        }
    }

    public String getStatusText() {
        if (status == null) return "";
        switch (status) {
            case 1: return "待响应";
            case 2: return "已响应";
            case 3: return "处理中";
            case 4: return "已完成";
            case 5: return "已取消";
            default: return "未知";
        }
    }

    public String getUrgencyLevelText() {
        if (urgencyLevel == null) return "";
        switch (urgencyLevel) {
            case 1: return "低";
            case 2: return "中";
            case 3: return "高";
            case 4: return "紧急";
            default: return "未知";
        }
    }
}
