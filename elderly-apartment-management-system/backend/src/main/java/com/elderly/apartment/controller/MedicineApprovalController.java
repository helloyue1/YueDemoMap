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
@RequestMapping("/medicine-approvals")
public class MedicineApprovalController {

    @GetMapping
    public Result<Page<Map<String, Object>>> getMedicineApprovalList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        Page<Map<String, Object>> resultPage = new Page<>(page, size);
        List<Map<String, Object>> records = new ArrayList<>();
        
        Map<String, Object> approval1 = new HashMap<>();
        approval1.put("id", 1);
        approval1.put("elderlyId", 1);
        approval1.put("elderlyName", "张大爷");
        approval1.put("medicineId", 1);
        approval1.put("medicineName", "阿司匹林");
        approval1.put("applyReason", "需要长期服用");
        approval1.put("applyTime", LocalDateTime.now());
        approval1.put("applicant", "李护士");
        approval1.put("status", 0);
        records.add(approval1);
        
        resultPage.setRecords(records);
        resultPage.setTotal(1);
        return Result.success(resultPage);
    }

    @PostMapping("/{id}/approve")
    public Result<Void> approveMedicine(@PathVariable Integer id, @RequestBody Map<String, Object> data) {
        return Result.success("审批通过", null);
    }

    @PostMapping("/{id}/reject")
    public Result<Void> rejectMedicine(@PathVariable Integer id, @RequestBody Map<String, Object> data) {
        return Result.success("审批拒绝", null);
    }
}
