package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("schedule")
public class Schedule {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("elderly_id")
    private Long elderlyId;

    private String title;

    @TableField("schedule_date")
    private LocalDate scheduleDate;

    @TableField("start_time")
    private LocalTime startTime;

    @TableField("end_time")
    private LocalTime endTime;

    private String location;

    private String color;

    private String description;

    private Integer type;

    @TableField("source_id")
    private Long sourceId;

    @TableField("remind_minutes")
    private Integer remindMinutes;

    @TableField("is_repeated")
    private Integer isRepeated;

    @TableField("repeat_type")
    private String repeatType;

    @TableField("repeat_end_date")
    private LocalDate repeatEndDate;

    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    // 日程类型常量
    public static final int TYPE_PERSONAL = 1;
    public static final int TYPE_ACTIVITY = 2;
    public static final int TYPE_CARE = 3;
    public static final int TYPE_MEAL = 4;

    // 状态常量
    public static final int STATUS_CANCELLED = 0;
    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_COMPLETED = 2;
}
