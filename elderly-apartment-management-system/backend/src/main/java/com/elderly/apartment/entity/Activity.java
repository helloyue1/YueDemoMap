package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("activity")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("activity_type")
    private Integer activityType;

    @TableField("activity_type_name")
    private String activityTypeName;

    @TableField("category_id")
    private Integer categoryId;

    @TableField("activity_date")
    private LocalDate activityDate;

    @TableField("start_time")
    private LocalTime startTime;

    @TableField("end_time")
    private LocalTime endTime;

    @TableField("location")
    private String location;

    @TableField("max_participants")
    private Integer maxParticipants;

    @TableField("current_participants")
    private Integer currentParticipants;

    @TableField("checked_in_count")
    private Integer checkedInCount;

    @TableField("min_participants")
    private Integer minParticipants;

    @TableField("organizer")
    private String organizer;

    @TableField("organizer_phone")
    private String organizerPhone;

    @TableField("organizer_id")
    private Integer organizerId;

    @TableField("image_url")
    private String imageUrl;

    @TableField("image_urls")
    private String imageUrls;

    @TableField("requirements")
    private String requirements;

    @TableField("suitable_for")
    private String suitableFor;

    @TableField("materials_needed")
    private String materialsNeeded;

    @TableField("registration_start_time")
    private LocalDateTime registrationStartTime;

    @TableField("registration_end_time")
    private LocalDateTime registrationEndTime;

    @TableField("status")
    private Integer status;

    @TableField("is_recommended")
    private Integer isRecommended;

    @TableField("is_public")
    private Integer isPublic;

    @TableField("view_count")
    private Integer viewCount;

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
    private String activityTypeText;

    @TableField(exist = false)
    private Double participationRate;

    @TableField(exist = false)
    private Boolean canSignup;

    @TableField(exist = false)
    private Boolean isFull;
}
