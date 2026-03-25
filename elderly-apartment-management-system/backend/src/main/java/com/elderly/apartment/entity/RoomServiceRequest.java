package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("room_service_request")
public class RoomServiceRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("request_no")
    private String requestNo;

    @TableField("room_id")
    private Integer roomId;

    @TableField("elderly_id")
    private Integer elderlyId;

    @TableField("user_id")
    private Integer userId;

    @TableField("service_type")
    private String serviceType;

    @TableField("service_subtype")
    private String serviceSubtype;

    @TableField("description")
    private String description;

    @TableField("urgency")
    private String urgency;

    @TableField("preferred_time")
    private String preferredTime;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("status")
    private String status;

    @TableField("handler_id")
    private Integer handlerId;

    @TableField("handler_name")
    private String handlerName;

    @TableField("handle_notes")
    private String handleNotes;

    @TableField("handle_time")
    private LocalDateTime handleTime;

    @TableField("complete_time")
    private LocalDateTime completeTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    // 非数据库字段
    @TableField(exist = false)
    private String roomNumber;

    @TableField(exist = false)
    private String elderlyName;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private RoomServiceEvaluation evaluation;

    @TableField(exist = false)
    private Boolean evaluated;

    @TableField(exist = false)
    private Integer evaluationRating;

    @TableField(exist = false)
    private String evaluationContent;
}
