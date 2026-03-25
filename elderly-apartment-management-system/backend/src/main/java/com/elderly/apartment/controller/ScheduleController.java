package com.elderly.apartment.controller;

import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.Schedule;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.service.ScheduleService;
import com.elderly.apartment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private UserService userService;

    @GetMapping
    public Result<List<Map<String, Object>>> getSchedules(
            @RequestParam(required = false) String date,
            Authentication authentication) {

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 检查用户是否是老人类型(user_type=2)
        if (!user.isElderly()) {
            return Result.error("未找到老人信息");
        }

        LocalDate queryDate;
        if (date != null && !date.isEmpty()) {
            queryDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        } else {
            queryDate = LocalDate.now();
        }

        List<Schedule> schedules = scheduleService.getSchedulesByElderlyIdAndDate(user.getId().longValue(), queryDate);

        List<Map<String, Object>> result = schedules.stream().map(this::convertToMap).collect(Collectors.toList());

        return Result.success(result);
    }

    @GetMapping("/range")
    public Result<List<Map<String, Object>>> getSchedulesByRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            Authentication authentication) {

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 检查用户是否是老人类型(user_type=2)
        if (!user.isElderly()) {
            return Result.error("未找到老人信息");
        }

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        List<Schedule> schedules = scheduleService.getSchedulesByElderlyIdAndDateRange(user.getId().longValue(), start, end);

        List<Map<String, Object>> result = schedules.stream().map(this::convertToMap).collect(Collectors.toList());

        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getScheduleById(@PathVariable Long id) {
        Schedule schedule = scheduleService.getById(id);
        if (schedule == null) {
            return Result.error("日程不存在");
        }
        return Result.success(convertToMap(schedule));
    }

    @PostMapping
    public Result<Map<String, Object>> createSchedule(@RequestBody Schedule schedule, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 检查用户是否是老人类型(user_type=2)
        if (!user.isElderly()) {
            return Result.error("未找到老人信息");
        }

        schedule.setElderlyId(user.getId().longValue());
        schedule.setStatus(Schedule.STATUS_NORMAL);

        if (scheduleService.createSchedule(schedule)) {
            return Result.success("创建成功", convertToMap(schedule));
        } else {
            return Result.error("创建失败");
        }
    }

    @PutMapping("/{id}")
    public Result<Map<String, Object>> updateSchedule(@PathVariable Long id, @RequestBody Schedule schedule) {
        schedule.setId(id);
        if (scheduleService.updateSchedule(schedule)) {
            return Result.success("更新成功", convertToMap(scheduleService.getById(id)));
        } else {
            return Result.error("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteSchedule(@PathVariable Long id) {
        if (scheduleService.deleteSchedule(id)) {
            return Result.success("删除成功", null);
        } else {
            return Result.error("删除失败");
        }
    }

    private Map<String, Object> convertToMap(Schedule schedule) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", schedule.getId());
        map.put("title", schedule.getTitle());
        map.put("date", schedule.getScheduleDate().toString());
        map.put("startTime", schedule.getStartTime().toString());
        map.put("endTime", schedule.getEndTime().toString());
        map.put("time", schedule.getStartTime().toString() + "-" + schedule.getEndTime().toString());
        map.put("location", schedule.getLocation());
        map.put("color", schedule.getColor());
        map.put("description", schedule.getDescription());
        map.put("type", schedule.getType());
        map.put("status", schedule.getStatus());
        return map;
    }
}
