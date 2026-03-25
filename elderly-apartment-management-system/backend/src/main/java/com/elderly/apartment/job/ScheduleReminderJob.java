package com.elderly.apartment.job;

import com.elderly.apartment.entity.Schedule;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.mapper.ScheduleMapper;
import com.elderly.apartment.mapper.UserMapper;
import com.elderly.apartment.websocket.ScheduleReminderMessage;
import com.elderly.apartment.websocket.ScheduleWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
public class ScheduleReminderJob {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ScheduleWebSocketHandler webSocketHandler;

    /**
     * 每分钟检查一次即将到期的日程提醒
     * 检查未来5分钟内需要提醒的日程
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void checkScheduleReminders() {
        log.debug("开始检查日程提醒...");

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalTime fiveMinutesLater = now.plusMinutes(5);

        // 查询今天需要提醒的日程
        List<Schedule> schedules = scheduleMapper.selectRemindableSchedules(today, now, fiveMinutesLater);

        if (schedules.isEmpty()) {
            log.debug("没有需要提醒的日程");
            return;
        }

        log.info("发现 {} 个需要提醒的日程", schedules.size());

        for (Schedule schedule : schedules) {
            sendReminderToUser(schedule);
        }
    }

    /**
     * 发送提醒给关联的用户
     */
    private void sendReminderToUser(Schedule schedule) {
        try {
            // 直接从user表查询老人信息（user_type=2）
            User user = userMapper.selectById(schedule.getElderlyId().intValue());
            if (user == null || !user.isElderly()) {
                log.warn("未找到住客信息或不是老人类型: {}", schedule.getElderlyId());
                return;
            }

            Long userId = user.getId().longValue();

            // 创建提醒消息
            ScheduleReminderMessage message = ScheduleReminderMessage.createReminder(schedule);

            // 发送WebSocket消息
            webSocketHandler.sendScheduleReminder(userId, message);

            log.info("已发送日程提醒: {} 给用户 {}", schedule.getTitle(), userId);

        } catch (Exception e) {
            log.error("发送日程提醒失败: {}", schedule.getId(), e);
        }
    }

    /**
     * 每小时清理一次已过期且已完成的日程提醒记录（可选）
     */
    @Scheduled(cron = "0 0 * * * ?") // 每小时执行一次
    public void cleanupExpiredSchedules() {
        log.debug("清理过期日程...");
        // 可以在这里添加清理逻辑
    }
}
