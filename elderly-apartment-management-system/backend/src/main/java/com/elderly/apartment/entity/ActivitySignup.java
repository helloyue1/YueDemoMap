package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("activity_signup")
public class ActivitySignup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("activity_id")
    private Integer activityId;

    @TableField("activity_title")
    private String activityTitle;

    @TableField("elderly_id")
    private Integer elderlyId;

    @TableField("elderly_name")
    private String elderlyName;

    @TableField("elderly_phone")
    private String elderlyPhone;

    @TableField("room_number")
    private String roomNumber;

    @TableField("emergency_contact_name")
    private String emergencyContactName;

    @TableField("emergency_contact_phone")
    private String emergencyContactPhone;

    @TableField("health_status")
    private String healthStatus;

    @TableField("special_requirements")
    private String specialRequirements;

    @TableField("signup_source")
    private Integer signupSource;

    @TableField("signup_time")
    private LocalDateTime signupTime;

    @TableField("status")
    private Integer status;

    @TableField("cancel_reason")
    private String cancelReason;

    @TableField("reject_reason")
    private String rejectReason;

    @TableField("checkin_time")
    private LocalDateTime checkinTime;

    @TableField("checkin_method")
    private Integer checkinMethod;

    @TableField("checked_in_by")
    private Integer checkedInBy;

    @TableField("is_waitlist")
    private Integer isWaitlist;

    @TableField("waitlist_order")
    private Integer waitlistOrder;

    @TableField("notes")
    private String notes;

    @TableField("create_by")
    private Integer createBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    // 非数据库字段
    @TableField(exist = false)
    private String statusText;

    @TableField(exist = false)
    private String signupSourceText;

    @TableField(exist = false)
    private String checkinMethodText;

    @TableField(exist = false)
    private Activity activity;

    @TableField(exist = false)
    private User user;
}
