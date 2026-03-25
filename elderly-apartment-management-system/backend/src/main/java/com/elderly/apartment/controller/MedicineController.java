package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/medicines")
public class MedicineController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Result<Page<Map<String, Object>>> getMedicineList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type) {
        Page<Map<String, Object>> resultPage = new Page<>(page, size);
        List<Map<String, Object>> records = new ArrayList<>();
        
        Map<String, Object> medicine1 = new HashMap<>();
        medicine1.put("id", 1);
        medicine1.put("name", "阿司匹林");
        medicine1.put("type", "西药");
        medicine1.put("specification", "100mg*30片");
        medicine1.put("manufacturer", "拜耳医药");
        records.add(medicine1);
        
        Map<String, Object> medicine2 = new HashMap<>();
        medicine2.put("id", 2);
        medicine2.put("name", "降压药");
        medicine2.put("type", "西药");
        medicine2.put("specification", "50mg*20片");
        medicine2.put("manufacturer", "辉瑞制药");
        records.add(medicine2);
        
        resultPage.setRecords(records);
        resultPage.setTotal(2);
        return Result.success(resultPage);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getMedicineById(@PathVariable Integer id) {
        Map<String, Object> medicine = new HashMap<>();
        medicine.put("id", id);
        medicine.put("name", "阿司匹林");
        medicine.put("type", "西药");
        medicine.put("specification", "100mg*30片");
        medicine.put("manufacturer", "拜耳医药");
        return Result.success(medicine);
    }

    @PostMapping
    public Result<Map<String, Object>> createMedicine(@RequestBody Map<String, Object> data) {
        data.put("id", 3);
        return Result.success("药品创建成功", data);
    }

    @PutMapping("/{id}")
    public Result<Map<String, Object>> updateMedicine(@PathVariable Integer id, @RequestBody Map<String, Object> data) {
        data.put("id", id);
        return Result.success("药品更新成功", data);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMedicine(@PathVariable Integer id) {
        return Result.success("药品删除成功", null);
    }
}
