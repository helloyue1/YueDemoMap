package com.elderly.apartment.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleReminderMessage {

    /**
     * 消息类型: REMINDER - 提醒, UPDATE - 更新, DELETE - 删除
     */
    private String type;

    /**
     * 日程ID
     */
    private Long scheduleId;

    /**
     * 日程标题
     */
    private String title;

    /**
     * 日程日期
     */
    private LocalDate scheduleDate;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 地点
     */
    private String location;

    /**
     * 描述
     */
    private String description;

    /**
     * 提醒时间（提前多少分钟）
     */
    private Integer remindMinutes;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建提醒消息
     */
    public static ScheduleReminderMessage createReminder(com.elderly.apartment.entity.Schedule schedule) {
        return ScheduleReminderMessage.builder()
                .type("REMINDER")
                .scheduleId(schedule.getId())
                .title(schedule.getTitle())
                .scheduleDate(schedule.getScheduleDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .location(schedule.getLocation())
                .description(schedule.getDescription())
                .remindMinutes(schedule.getRemindMinutes())
                .message("日程提醒: " + schedule.getTitle() + " 即将开始")
                .createTime(java.time.LocalDateTime.now().toString())
                .build();
    }
}
