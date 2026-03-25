package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.CareRecord;
import com.elderly.apartment.service.CareRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/care-record")
public class CareRecordController {

    @Autowired
    private CareRecordService careRecordService;

    @GetMapping
    public Result<IPage<CareRecord>> getCareRecordList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String elderlyName,
            @RequestParam(required = false) String caregiverName,
            @RequestParam(required = false) Integer careType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        IPage<CareRecord> pageParam = new Page<>(page, size);
        LocalDateTime startDateTime = parseDateTime(startTime);
        LocalDateTime endDateTime = parseDateTime(endTime);
        IPage<CareRecord> result = careRecordService.getCareRecordPage(pageParam, elderlyName, caregiverName, careType, status, startDateTime, endDateTime);
        return Result.success(result);
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        try {
            if (dateTimeStr.contains("Z")) {
                return java.time.Instant.parse(dateTimeStr)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();
            } else if (dateTimeStr.contains("T")) {
                return LocalDateTime.parse(dateTimeStr, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } else {
                return LocalDateTime.parse(dateTimeStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/{id}")
    public Result<CareRecord> getCareRecordById(@PathVariable Integer id) {
        CareRecord careRecord = careRecordService.getById(id);
        if (careRecord == null) {
            return Result.error("护理记录不存在");
        }
        return Result.success(careRecord);
    }

    @PostMapping
    public Result<CareRecord> createCareRecord(@RequestBody CareRecord careRecord) {
        boolean success = careRecordService.createCareRecord(careRecord);
        if (success) {
            return Result.success("护理记录创建成功", careRecord);
        }
        return Result.error("护理记录创建失败");
    }

    @PutMapping("/{id}")
    public Result<CareRecord> updateCareRecord(@PathVariable Integer id, @RequestBody CareRecord careRecord) {
        careRecord.setId(id);
        boolean success = careRecordService.updateCareRecord(careRecord);
        if (success) {
            return Result.success("护理记录更新成功", careRecord);
        }
        return Result.error("护理记录更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCareRecord(@PathVariable Integer id) {
        boolean success = careRecordService.removeById(id);
        if (success) {
            return Result.success("护理记录删除成功", null);
        }
        return Result.error("护理记录删除失败");
    }

    @GetMapping("/elderly/{elderlyId}")
    public Result<List<CareRecord>> getCareRecordsByElderly(@PathVariable Integer elderlyId) {
        List<CareRecord> list = careRecordService.getCareRecordsByElderlyId(elderlyId);
        return Result.success(list);
    }

    @GetMapping("/caregiver/{caregiverId}")
    public Result<List<CareRecord>> getCareRecordsByCaregiver(@PathVariable Integer caregiverId) {
        List<CareRecord> list = careRecordService.getCareRecordsByCaregiverId(caregiverId);
        return Result.success(list);
    }

    @GetMapping("/today")
    public Result<List<CareRecord>> getTodayRecords() {
        List<CareRecord> list = careRecordService.getTodayRecords();
        return Result.success(list);
    }

    @GetMapping("/care-plan/{carePlanId}")
    public Result<List<CareRecord>> getCareRecordsByCarePlan(@PathVariable Integer carePlanId) {
        List<CareRecord> list = careRecordService.getCareRecordsByCarePlanId(carePlanId);
        return Result.success(list);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateCareRecordStatus(@PathVariable Integer id, @RequestParam Integer status) {
        boolean success = careRecordService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功", null);
        }
        return Result.error("状态更新失败");
    }

    @GetMapping("/statistics/{caregiverId}")
    public Result<Map<String, Object>> getCareStatistics(@PathVariable Integer caregiverId) {
        Map<String, Object> statistics = careRecordService.getCareStatistics(caregiverId);
        return Result.success(statistics);
    }
}
