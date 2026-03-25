package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.HealthRecord;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.service.HealthRecordService;
import com.elderly.apartment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 健康档案控制器
 */
@RestController
@RequestMapping(value = {"/health-record", "/api/health-record"})
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @Autowired
    private UserService userService;

    /**
     * 获取所有健康档案（用于健康档案记录页面）
     */
    @GetMapping("/all")
    public Result<List<HealthRecord>> getAllHealthRecords() {
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getDeleted, 0)
               .orderByDesc(HealthRecord::getCheckDate);
        List<HealthRecord> list = healthRecordService.list(wrapper);

        // 关联查询老人信息
        list = list.stream()
                .map(this::fillElderlyInfo)
                .collect(Collectors.toList());

        return Result.success(list);
    }

    /**
     * 获取健康档案列表
     */
    @GetMapping
    public Result<Page<HealthRecord>> getHealthRecordList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String elderlyName,
            @RequestParam(required = false) Integer healthStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        Page<HealthRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getDeleted, 0);

        // 根据健康状态筛选
        if (healthStatus != null) {
            wrapper.eq(HealthRecord::getHealthStatus, healthStatus);
        }

        // 根据日期范围筛选
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(HealthRecord::getCheckDate, LocalDate.parse(startDate));
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(HealthRecord::getCheckDate, LocalDate.parse(endDate));
        }

        wrapper.orderByDesc(HealthRecord::getCheckDate);
        Page<HealthRecord> result = healthRecordService.page(pageParam, wrapper);

        // 关联查询老人信息
        List<HealthRecord> records = result.getRecords().stream()
                .map(this::fillElderlyInfo)
                .collect(Collectors.toList());
        result.setRecords(records);

        return Result.success(result);
    }

    /**
     * 根据ID获取健康档案
     */
    @GetMapping("/{id}")
    public Result<HealthRecord> getHealthRecordById(@PathVariable Integer id) {
        HealthRecord record = healthRecordService.getById(id);
        if (record == null || record.getDeleted() == 1) {
            return Result.error("健康档案不存在");
        }
        return Result.success(fillElderlyInfo(record));
    }

    /**
     * 获取老人的健康档案列表
     */
    @GetMapping("/elderly/{elderlyId}")
    public Result<List<HealthRecord>> getHealthRecordsByElderlyId(@PathVariable Integer elderlyId) {
        User user = userService.getById(elderlyId);
        if (user == null) {
            return Result.error("住客信息不存在");
        }

        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getElderlyId, elderlyId)
               .eq(HealthRecord::getDeleted, 0)
               .orderByDesc(HealthRecord::getCheckDate);
        List<HealthRecord> records = healthRecordService.list(wrapper);

        // 填充老人信息
        records = records.stream()
                .map(record -> {
                    record.setElderlyName(user.getRealName());
                    record.setElderlyAge(user.getAge());
                    record.setElderlyGender(user.getGender());
                    return record;
                })
                .collect(Collectors.toList());

        return Result.success(records);
    }

    /**
     * 创建健康档案
     */
    @PostMapping
    public Result<HealthRecord> createHealthRecord(@RequestBody HealthRecord record) {
        // 验证住客是否存在
        User user = userService.getById(record.getElderlyId());
        if (user == null) {
            return Result.error("住客信息不存在");
        }

        // 设置默认检查日期
        if (record.getCheckDate() == null) {
            record.setCheckDate(LocalDate.now());
        }

        healthRecordService.save(record);

        // 更新老人的健康状态
        user.setHealthStatus(String.valueOf(record.getHealthStatus()));
        userService.updateById(user);

        return Result.success(fillElderlyInfo(record));
    }

    /**
     * 更新健康档案
     */
    @PutMapping("/{id}")
    public Result<HealthRecord> updateHealthRecord(@PathVariable Integer id, @RequestBody HealthRecord record) {
        HealthRecord existing = healthRecordService.getById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("健康档案不存在");
        }

        record.setId(id);
        healthRecordService.updateById(record);

        // 如果健康状态发生变化，更新老人的健康状态
        if (record.getHealthStatus() != null && !record.getHealthStatus().equals(existing.getHealthStatus())) {
            User user = userService.getById(existing.getElderlyId());
            if (user != null) {
                user.setHealthStatus(String.valueOf(record.getHealthStatus()));
                userService.updateById(user);
            }
        }

        return Result.success(fillElderlyInfo(healthRecordService.getById(id)));
    }

    /**
     * 删除健康档案
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteHealthRecord(@PathVariable Integer id) {
        HealthRecord record = healthRecordService.getById(id);
        if (record == null || record.getDeleted() == 1) {
            return Result.error("健康档案不存在");
        }

        healthRecordService.removeById(id);
        return Result.success(null);
    }

    /**
     * 获取健康档案统计
     */
    @GetMapping("/statistics")
    public Result<java.util.Map<String, Object>> getHealthStatistics() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        // 各健康状态人数统计
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getDeleted, 0);
        List<HealthRecord> records = healthRecordService.list(wrapper);

        long goodCount = records.stream().filter(r -> r.getHealthStatus() != null && r.getHealthStatus() == 1).count();
        long normalCount = records.stream().filter(r -> r.getHealthStatus() != null && r.getHealthStatus() == 2).count();
        long poorCount = records.stream().filter(r -> r.getHealthStatus() != null && r.getHealthStatus() == 3).count();
        long criticalCount = records.stream().filter(r -> r.getHealthStatus() != null && r.getHealthStatus() == 4).count();

        stats.put("good", goodCount);
        stats.put("normal", normalCount);
        stats.put("poor", poorCount);
        stats.put("critical", criticalCount);
        stats.put("total", records.size());

        // 本月新增档案数
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LambdaQueryWrapper<HealthRecord> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.eq(HealthRecord::getDeleted, 0)
                   .ge(HealthRecord::getCheckDate, firstDayOfMonth);
        long monthCount = healthRecordService.count(monthWrapper);
        stats.put("monthNew", monthCount);

        return Result.success(stats);
    }

    /**
     * 填充老人信息
     */
    private HealthRecord fillElderlyInfo(HealthRecord record) {
        if (record.getElderlyId() != null) {
            User user = userService.getById(record.getElderlyId());
            if (user != null && user.getUserType() != null && user.getUserType() == 2) {
                record.setElderlyName(user.getRealName());
                record.setElderlyAge(user.getAge());
                record.setElderlyGender(user.getGender());
                record.setRoomNumber(user.getRoomNumber());
            } else {
                record.setElderlyName("未知老人(ID:" + record.getElderlyId() + ")");
            }
        }
        return record;
    }
}
