package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@TableName("activity_feedback")
public class ActivityFeedback implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("activity_id")
    private Integer activityId;

    @TableField("activity_title")
    private String activityTitle;

    @TableField("signup_id")
    private Integer signupId;

    @TableField("elderly_id")
    private Integer elderlyId;

    @TableField("elderly_name")
    private String elderlyName;

    @TableField("satisfaction_score")
    private Integer satisfactionScore;

    @TableField("organization_score")
    private Integer organizationScore;

    @TableField("content_score")
    private Integer contentScore;

    @TableField("staff_score")
    private Integer staffScore;

    @TableField("overall_score")
    private BigDecimal overallScore;

    @TableField("is_would_recommend")
    private Integer isWouldRecommend;

    @TableField("feedback_content")
    private String feedbackContent;

    @TableField("improvement_suggestions")
    private String improvementSuggestions;

    @TableField("favorite_part")
    private String favoritePart;

    @TableField("feedback_time")
    private LocalDateTime feedbackTime;

    @TableField("status")
    private Integer status;

    @TableField("handler_id")
    private Integer handlerId;

    @TableField("handler_name")
    private String handlerName;

    @TableField("handler_reply")
    private String handlerReply;

    @TableField("reply_time")
    private LocalDateTime replyTime;

    @TableField("notes")
    private String notes;

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
    private Activity activity;

    @TableField(exist = false)
    private User user;
}
