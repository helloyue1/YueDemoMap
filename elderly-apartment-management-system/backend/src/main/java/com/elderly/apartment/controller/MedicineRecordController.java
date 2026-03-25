package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/medicine-records")
public class MedicineRecordController {

    @GetMapping
    public Result<Page<Map<String, Object>>> getMedicineRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String elderlyName,
            @RequestParam(required = false) String medicineName) {
        Page<Map<String, Object>> resultPage = new Page<>(page, size);
        List<Map<String, Object>> records = new ArrayList<>();
        
        Map<String, Object> record1 = new HashMap<>();
        record1.put("id", 1);
        record1.put("elderlyId", 1);
        record1.put("elderlyName", "张大爷");
        record1.put("medicineId", 1);
        record1.put("medicineName", "阿司匹林");
        record1.put("dosage", "1片");
        record1.put("frequency", "每日一次");
        record1.put("administerTime", LocalDateTime.now());
        record1.put("administerBy", "李护士");
        record1.put("status", 1);
        records.add(record1);
        
        resultPage.setRecords(records);
        resultPage.setTotal(1);
        return Result.success(resultPage);
    }

    @PostMapping
    public Result<Map<String, Object>> createMedicineRecord(@RequestBody Map<String, Object> data) {
        data.put("id", 2);
        data.put("administerTime", LocalDateTime.now());
        return Result.success("用药记录创建成功", data);
    }
}
