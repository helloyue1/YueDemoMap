package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("room_service_evaluation")
public class RoomServiceEvaluation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("request_id")
    private Integer requestId;

    @TableField("room_id")
    private Integer roomId;

    @TableField("elderly_id")
    private Integer elderlyId;

    @TableField("user_id")
    private Integer userId;

    @TableField("rating")
    private Integer rating;

    @TableField("content")
    private String content;

    @TableField("tags")
    private String tags;

    @TableField("is_anonymous")
    private Integer isAnonymous;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    // 非数据库字段
    @TableField(exist = false)
    private String requestNo;

    @TableField(exist = false)
    private String serviceType;

    @TableField(exist = false)
    private String roomNumber;

    @TableField(exist = false)
    private String elderlyName;

    @TableField(exist = false)
    private String userName;
}
