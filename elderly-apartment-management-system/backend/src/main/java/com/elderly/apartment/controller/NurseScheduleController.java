package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/nurse-schedule")
public class NurseScheduleController {

    @GetMapping
    public Result<Page<Map<String, Object>>> getNurseScheduleList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String nurseName,
            @RequestParam(required = false) String date) {
        Page<Map<String, Object>> resultPage = new Page<>(page, size);
        List<Map<String, Object>> records = new ArrayList<>();
        
        Map<String, Object> schedule1 = new HashMap<>();
        schedule1.put("id", 1);
        schedule1.put("nurseId", 1);
        schedule1.put("nurseName", "李护士");
        schedule1.put("scheduleDate", LocalDate.now());
        schedule1.put("shift", "早班");
        schedule1.put("startTime", "08:00");
        schedule1.put("endTime", "16:00");
        schedule1.put("area", "A区");
        schedule1.put("status", "正常");
        records.add(schedule1);
        
        resultPage.setRecords(records);
        resultPage.setTotal(1);
        return Result.success(resultPage);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getNurseScheduleById(@PathVariable Integer id) {
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("id", id);
        schedule.put("nurseId", 1);
        schedule.put("nurseName", "李护士");
        schedule.put("scheduleDate", LocalDate.now());
        schedule.put("shift", "早班");
        schedule.put("startTime", "08:00");
        schedule.put("endTime", "16:00");
        schedule.put("area", "A区");
        schedule.put("status", "正常");
        return Result.success(schedule);
    }

    @PostMapping
    public Result<Map<String, Object>> createNurseSchedule(@RequestBody Map<String, Object> data) {
        data.put("id", 2);
        data.put("createTime", LocalDateTime.now());
        return Result.success("排班创建成功", data);
    }

    @PutMapping("/{id}")
    public Result<Map<String, Object>> updateNurseSchedule(@PathVariable Integer id, @RequestBody Map<String, Object> data) {
        data.put("id", id);
        return Result.success("排班更新成功", data);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteNurseSchedule(@PathVariable Integer id) {
        return Result.success("排班删除成功", null);
    }

    @PostMapping("/generate")
    public Result<Void> generateSchedule(@RequestBody Map<String, Object> data) {
        return Result.success("排班生成成功", null);
    }
}
