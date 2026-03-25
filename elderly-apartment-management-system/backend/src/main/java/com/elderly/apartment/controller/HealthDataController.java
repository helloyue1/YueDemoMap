package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.HealthData;
import com.elderly.apartment.entity.Room;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.service.HealthDataService;
import com.elderly.apartment.service.RoomService;
import com.elderly.apartment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 健康数据控制器（日常健康监测记录）
 */
@RestController
@RequestMapping("/health-data")
public class HealthDataController {

    /**
     * 获取所有健康数据（用于健康档案记录页面）
     */
    @GetMapping("/all")
    public Result<List<HealthData>> getAllHealthData() {
        LambdaQueryWrapper<HealthData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthData::getDeleted, 0)
               .orderByDesc(HealthData::getRecordTime);
        List<HealthData> list = healthDataService.list(wrapper);

        // 关联查询老人信息
        list = list.stream()
                .map(this::fillElderlyInfo)
                .collect(Collectors.toList());

        return Result.success(list);
    }

    @Autowired
    private HealthDataService healthDataService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    /**
     * 获取健康数据列表
     */
    @GetMapping
    public Result<Page<HealthData>> getHealthDataList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String elderlyName,
            @RequestParam(required = false) Integer recordType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        Page<HealthData> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<HealthData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthData::getDeleted, 0);

        // 根据记录类型筛选
        if (recordType != null) {
            wrapper.eq(HealthData::getRecordType, recordType);
        }

        // 根据日期范围筛选
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(HealthData::getRecordTime, LocalDateTime.parse(startDate + "T00:00:00"));
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(HealthData::getRecordTime, LocalDateTime.parse(endDate + "T23:59:59"));
        }

        wrapper.orderByDesc(HealthData::getRecordTime);
        Page<HealthData> result = healthDataService.page(pageParam, wrapper);

        // 关联查询老人信息
        List<HealthData> records = result.getRecords().stream()
                .map(this::fillElderlyInfo)
                .collect(Collectors.toList());
        result.setRecords(records);

        return Result.success(result);
    }

    /**
     * 根据ID获取健康数据
     */
    @GetMapping("/{id}")
    public Result<HealthData> getHealthDataById(@PathVariable Integer id) {
        HealthData data = healthDataService.getById(id);
        if (data == null || data.getDeleted() == 1) {
            return Result.error("健康记录不存在");
        }
        return Result.success(fillElderlyInfo(data));
    }

    /**
     * 获取老人的健康数据列表
     */
    @GetMapping("/elderly/{elderlyId}")
    public Result<List<HealthData>> getHealthDataByElderlyId(@PathVariable Integer elderlyId) {
        User user = userService.getById(elderlyId);
        if (user == null) {
            return Result.error("住客信息不存在");
        }

        List<HealthData> records = healthDataService.getByElderlyId(elderlyId);

        // 填充老人信息
        records = records.stream()
                .map(record -> {
                    record.setElderlyName(user.getRealName());
                    return record;
                })
                .collect(Collectors.toList());

        return Result.success(records);
    }

    /**
     * 创建健康数据记录
     */
    @PostMapping
    public Result<HealthData> createHealthData(@RequestBody HealthData data) {
        // 验证住客是否存在
        User user = userService.getById(data.getElderlyId());
        if (user == null) {
            return Result.error("住客信息不存在");
        }

        // 设置默认记录时间
        if (data.getRecordTime() == null) {
            data.setRecordTime(LocalDateTime.now());
        }

        // 处理血压格式
        if (data.getSystolicPressure() != null && data.getDiastolicPressure() != null) {
            data.setBloodPressure(data.getSystolicPressure() + "/" + data.getDiastolicPressure());
        }

        // 生成记录内容摘要
        data.setRecordContent(generateRecordContent(data));

        healthDataService.save(data);
        return Result.success(fillElderlyInfo(data));
    }

    /**
     * 更新健康数据记录
     */
    @PutMapping("/{id}")
    public Result<HealthData> updateHealthData(@PathVariable Integer id, @RequestBody HealthData data) {
        HealthData existing = healthDataService.getById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("健康记录不存在");
        }

        data.setId(id);

        // 处理血压格式
        if (data.getSystolicPressure() != null && data.getDiastolicPressure() != null) {
            data.setBloodPressure(data.getSystolicPressure() + "/" + data.getDiastolicPressure());
        }

        // 重新生成记录内容摘要
        if (data.getRecordType() != null || data.getSystolicPressure() != null ||
            data.getBloodSugar() != null || data.getHeartRate() != null) {
            data.setRecordContent(generateRecordContent(data));
        }

        healthDataService.updateById(data);
        return Result.success(fillElderlyInfo(healthDataService.getById(id)));
    }

    /**
     * 删除健康数据记录
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteHealthData(@PathVariable Integer id) {
        HealthData data = healthDataService.getById(id);
        if (data == null || data.getDeleted() == 1) {
            return Result.error("健康记录不存在");
        }

        healthDataService.removeById(id);
        return Result.success(null);
    }

    /**
     * 获取健康数据统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getHealthDataStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 今日记录数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        LambdaQueryWrapper<HealthData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthData::getDeleted, 0)
               .between(HealthData::getRecordTime, todayStart, todayEnd);
        long todayCount = healthDataService.count(wrapper);
        stats.put("todayCount", todayCount);

        // 本周记录数
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthData::getDeleted, 0)
               .ge(HealthData::getRecordTime, weekStart);
        long weekCount = healthDataService.count(wrapper);
        stats.put("weekCount", weekCount);

        // 总记录数
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthData::getDeleted, 0);
        long totalCount = healthDataService.count(wrapper);
        stats.put("totalCount", totalCount);

        // 各类型记录统计
        Map<String, Long> typeStats = new HashMap<>();
        for (int i = 1; i <= 7; i++) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HealthData::getDeleted, 0)
                   .eq(HealthData::getRecordType, i);
            long count = healthDataService.count(wrapper);
            typeStats.put(getRecordTypeText(i), count);
        }
        stats.put("typeStats", typeStats);

        return Result.success(stats);
    }

    /**
     * 填充老人信息
     */
    private HealthData fillElderlyInfo(HealthData data) {
        if (data.getElderlyId() != null) {
            User user = userService.getById(data.getElderlyId());
            if (user != null && user.getUserType() != null && user.getUserType() == 2) {
                data.setElderlyName(user.getRealName());
                // 直接从 user 对象获取房间号
                data.setRoomNumber(user.getRoomNumber());
            } else {
                data.setElderlyName("未知老人(ID:" + data.getElderlyId() + ")");
            }
        }
        return data;
    }

    /**
     * 生成记录内容摘要
     */
    private String generateRecordContent(HealthData data) {
        StringBuilder content = new StringBuilder();
        content.append(getRecordTypeText(data.getRecordType())).append("：");

        switch (data.getRecordType()) {
            case 1: // 血压测量
                if (data.getBloodPressure() != null) {
                    content.append("血压 ").append(data.getBloodPressure()).append(" mmHg");
                }
                break;
            case 2: // 血糖监测
                if (data.getBloodSugar() != null) {
                    content.append("血糖 ").append(data.getBloodSugar()).append(" mmol/L");
                }
                break;
            case 3: // 体重测量
                if (data.getWeight() != null) {
                    content.append("体重 ").append(data.getWeight()).append(" kg");
                }
                break;
            case 4: // 心率测量
                if (data.getHeartRate() != null) {
                    content.append("心率 ").append(data.getHeartRate()).append(" 次/分钟");
                }
                break;
            case 5: // 体温测量
                if (data.getBodyTemperature() != null) {
                    content.append("体温 ").append(data.getBodyTemperature()).append(" ℃");
                }
                break;
            case 6: // 血氧测量
                if (data.getBloodOxygen() != null) {
                    content.append("血氧 ").append(data.getBloodOxygen()).append(" %");
                }
                break;
            case 7: // 综合记录
                if (data.getBloodPressure() != null) {
                    content.append("血压 ").append(data.getBloodPressure()).append(" mmHg, ");
                }
                if (data.getHeartRate() != null) {
                    content.append("心率 ").append(data.getHeartRate()).append(" 次/分钟");
                }
                break;
            default:
                content.append("健康数据记录");
        }

        return content.toString();
    }

    /**
     * 获取记录类型文本
     */
    private String getRecordTypeText(Integer type) {
        if (type == null) return "未知";
        switch (type) {
            case 1: return "血压测量";
            case 2: return "血糖监测";
            case 3: return "体重测量";
            case 4: return "心率测量";
            case 5: return "体温测量";
            case 6: return "血氧测量";
            case 7: return "综合记录";
            default: return "未知";
        }
    }
}
