package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.*;
import com.elderly.apartment.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private CareRecordService careRecordService;

    @Autowired
    private FeeBillService feeBillService;

    @Autowired
    private HealthRecordService healthRecordService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        Long elderlyCount = userService.count(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, 2)
                .eq(User::getStatus, 1));
        stats.put("elderlyCount", elderlyCount);
        
        Long roomCount = roomService.count();
        stats.put("roomCount", roomCount);
        
        Long occupiedRoomCount = roomService.count(new LambdaQueryWrapper<Room>()
                .in(Room::getStatus, Arrays.asList(1, 3)));
        stats.put("occupiedRoomCount", occupiedRoomCount);
        
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Long activityCount = activityService.count(new LambdaQueryWrapper<Activity>()
                .likeRight(Activity::getActivityDate, currentMonth)
                .eq(Activity::getStatus, 1));
        stats.put("activityCount", activityCount);
        
        LocalDate today = LocalDate.now();
        Long careCount = careRecordService.count(new LambdaQueryWrapper<CareRecord>()
                .ge(CareRecord::getCareTime, today.atStartOfDay()));
        stats.put("careCount", careCount);
        
        Long unpaidBillCount = feeBillService.count(new LambdaQueryWrapper<FeeBill>()
                .eq(FeeBill::getStatus, 0));
        stats.put("unpaidBillCount", unpaidBillCount);
        
        return Result.success(stats);
    }

    @GetMapping("/room-stats")
    public Result<Map<String, Object>> getRoomStats() {
        Map<String, Object> stats = new HashMap<>();
        
        Long totalCount = roomService.count();
        Long emptyCount = roomService.count(new LambdaQueryWrapper<Room>()
                .eq(Room::getStatus, 0));
        Long fullCount = roomService.count(new LambdaQueryWrapper<Room>()
                .eq(Room::getStatus, 1));
        Long partialCount = roomService.count(new LambdaQueryWrapper<Room>()
                .eq(Room::getStatus, 3));
        
        stats.put("total", totalCount);
        stats.put("empty", emptyCount);
        stats.put("full", fullCount);
        stats.put("partial", partialCount);
        stats.put("occupancyRate", totalCount > 0 ? 
            Math.round((double)(fullCount + partialCount) / totalCount * 100) : 0);
        
        return Result.success(stats);
    }

    @GetMapping("/health-stats")
    public Result<Map<String, Object>> getHealthStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<User> allElderly = userService.list(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, 2)
                .eq(User::getStatus, 1));
        
        int healthy = 0, attention = 0, critical = 0;
        for (User user : allElderly) {
            String healthStatus = user.getHealthStatus();
            if (healthStatus == null || "1".equals(healthStatus)) {
                healthy++;
            } else if ("2".equals(healthStatus)) {
                attention++;
            } else if ("3".equals(healthStatus)) {
                critical++;
            }
        }
        
        stats.put("healthy", healthy);
        stats.put("attention", attention);
        stats.put("critical", critical);
        stats.put("total", allElderly.size());
        
        return Result.success(stats);
    }

    @GetMapping("/fee-stats")
    public Result<Map<String, Object>> getFeeStats() {
        Map<String, Object> stats = new HashMap<>();
        
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        List<FeeBill> unpaidBills = feeBillService.list(new LambdaQueryWrapper<FeeBill>()
                .eq(FeeBill::getStatus, 0));
        
        double totalUnpaid = unpaidBills.stream()
                .mapToDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount().doubleValue() : 0)
                .sum();
        
        List<FeeBill> paidBills = feeBillService.list(new LambdaQueryWrapper<FeeBill>()
                .eq(FeeBill::getStatus, 2)
                .likeRight(FeeBill::getBillMonth, currentMonth.substring(0, 7)));
        
        double totalPaid = paidBills.stream()
                .mapToDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount().doubleValue() : 0)
                .sum();
        
        stats.put("totalUnpaid", totalUnpaid);
        stats.put("totalPaid", totalPaid);
        stats.put("unpaidCount", unpaidBills.size());
        stats.put("paidCount", paidBills.size());
        
        return Result.success(stats);
    }

    @GetMapping("/recent-activities")
    public Result<List<Map<String, Object>>> getRecentActivities() {
        LocalDate today = LocalDate.now();
        List<Activity> activities = activityService.list(new LambdaQueryWrapper<Activity>()
                .ge(Activity::getActivityDate, today)
                .eq(Activity::getStatus, 1)
                .orderByAsc(Activity::getActivityDate)
                .last("LIMIT 5"));
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Activity activity : activities) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", activity.getId());
            item.put("name", activity.getTitle());
            LocalDateTime activityTime = LocalDateTime.of(activity.getActivityDate(), activity.getStartTime());
            item.put("time", activityTime);
            item.put("location", activity.getLocation());
            item.put("participants", activity.getCurrentParticipants());
            result.add(item);
        }
        
        return Result.success(result);
    }

    @GetMapping("/today-care-tasks")
    public Result<List<Map<String, Object>>> getTodayCareTasks() {
        LocalDate today = LocalDate.now();
        List<CareRecord> records = careRecordService.list(new LambdaQueryWrapper<CareRecord>()
                .ge(CareRecord::getCareTime, today.atStartOfDay())
                .orderByAsc(CareRecord::getCareTime)
                .last("LIMIT 10"));
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (CareRecord record : records) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", record.getId());
            item.put("elderlyId", record.getElderlyId());
            item.put("time", record.getCareTime());
            item.put("task", record.getCareContent());
            item.put("status", record.getStatus() != null && record.getStatus() == 3 ? "已完成" : "待完成");
            item.put("elderlyName", record.getElderlyName());
            result.add(item);
        }
        
        return Result.success(result);
    }

    @GetMapping("/monthly-summary")
    public Result<Map<String, Object>> getMonthlySummary() {
        Map<String, Object> summary = new HashMap<>();
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        Long newCheckIns = userService.count(new LambdaQueryWrapper<User>()
                .likeRight(User::getCreateTime, currentMonth)
                .eq(User::getUserType, 2)
                .eq(User::getStatus, 1));
        summary.put("newCheckIns", newCheckIns);
        
        Long activities = activityService.count(new LambdaQueryWrapper<Activity>()
                .likeRight(Activity::getActivityDate, currentMonth));
        summary.put("activities", activities);
        
        Long healthRecords = healthRecordService.count(new LambdaQueryWrapper<HealthRecord>()
                .likeRight(HealthRecord::getCreateTime, currentMonth));
        summary.put("healthRecords", healthRecords);
        
        Long careRecords = careRecordService.count(new LambdaQueryWrapper<CareRecord>()
                .likeRight(CareRecord::getCareTime, currentMonth));
        summary.put("careRecords", careRecords);
        
        return Result.success(summary);
    }
}
