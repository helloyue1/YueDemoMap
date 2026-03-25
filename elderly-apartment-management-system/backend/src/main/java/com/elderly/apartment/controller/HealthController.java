package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private UserService userService;

    @GetMapping("/archives")
    public Result<List<User>> getHealthArchiveList() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, 2);
        wrapper.isNotNull(User::getHealthStatus);
        List<User> result = userService.list(wrapper);
        return Result.success(result);
    }

    @PostMapping("/archives")
    public Result<User> createHealthArchive(@RequestBody User user) {
        userService.save(user);
        return Result.success(user);
    }

    @PutMapping("/archives/{id}")
    public Result<User> updateHealthArchive(@PathVariable Integer id, @RequestBody User user) {
        User existing = userService.getById(id);
        if (existing == null) {
            return Result.error("住客信息不存在");
        }
        
        user.setId(id);
        userService.updateById(user);
        return Result.success(user);
    }

    @GetMapping("/records")
    public Result<List<User>> getHealthRecordList(
            @RequestParam(required = false) Integer elderlyId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, 2);
        
        if (elderlyId != null) {
            wrapper.eq(User::getId, elderlyId);
        }
        
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(User::getCreateTime, startDate);
        }
        
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(User::getCreateTime, endDate);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        List<User> result = userService.list(wrapper);
        return Result.success(result);
    }

    @GetMapping("/{elderlyId}/health-records")
    public Result<List<User>> getHealthRecords(@PathVariable Integer elderlyId) {
        User user = userService.getById(elderlyId);
        if (user == null) {
            return Result.error("住客信息不存在");
        }
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, elderlyId);
        wrapper.eq(User::getUserType, 2);
        wrapper.isNotNull(User::getHealthStatus);
        List<User> result = userService.list(wrapper);
        return Result.success(result);
    }

    @PostMapping("/{elderlyId}/health-records")
    public Result<User> createHealthRecord(@PathVariable Integer elderlyId, @RequestBody User user) {
        User existing = userService.getById(elderlyId);
        if (existing == null) {
            return Result.error("住客信息不存在");
        }
        
        user.setId(elderlyId);
        userService.updateById(user);
        return Result.success(user);
    }

    @PutMapping("/{elderlyId}/health-records/{recordId}")
    public Result<User> updateHealthRecord(@PathVariable Integer elderlyId, 
                                               @PathVariable Integer recordId, 
                                               @RequestBody User user) {
        User existing = userService.getById(elderlyId);
        if (existing == null) {
            return Result.error("住客信息不存在");
        }
        
        user.setId(elderlyId);
        userService.updateById(user);
        return Result.success(user);
    }

    @DeleteMapping("/{elderlyId}/health-records/{recordId}")
    public Result<Void> deleteHealthRecord(@PathVariable Integer elderlyId, @PathVariable Integer recordId) {
        User existing = userService.getById(elderlyId);
        if (existing == null) {
            return Result.error("住客信息不存在");
        }
        
        userService.removeById(recordId);
        return Result.success(null);
    }

    @GetMapping("/checkup-reports")
    public Result<List<User>> getCheckupReportList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, 2);
        
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(User::getCreateTime, startDate);
        }
        
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(User::getCreateTime, endDate);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        List<User> result = userService.list(wrapper);
        return Result.success(result);
    }
}
