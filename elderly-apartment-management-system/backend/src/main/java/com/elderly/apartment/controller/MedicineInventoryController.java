package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/medicine-inventory")
public class MedicineInventoryController {

    @GetMapping
    public Result<Page<Map<String, Object>>> getMedicineInventory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        Page<Map<String, Object>> resultPage = new Page<>(page, size);
        List<Map<String, Object>> records = new ArrayList<>();
        
        Map<String, Object> inventory1 = new HashMap<>();
        inventory1.put("id", 1);
        inventory1.put("medicineId", 1);
        inventory1.put("medicineName", "阿司匹林");
        inventory1.put("currentStock", 100);
        inventory1.put("warningLine", 20);
        inventory1.put("unit", "盒");
        records.add(inventory1);
        
        Map<String, Object> inventory2 = new HashMap<>();
        inventory2.put("id", 2);
        inventory2.put("medicineId", 2);
        inventory2.put("medicineName", "降压药");
        inventory2.put("currentStock", 50);
        inventory2.put("warningLine", 10);
        inventory2.put("unit", "盒");
        records.add(inventory2);
        
        resultPage.setRecords(records);
        resultPage.setTotal(2);
        return Result.success(resultPage);
    }

    @PutMapping("/{id}")
    public Result<Map<String, Object>> updateMedicineInventory(@PathVariable Integer id, @RequestBody Map<String, Object> data) {
        data.put("id", id);
        return Result.success("库存更新成功", data);
    }
}
