package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.*;
import com.elderly.apartment.security.DataPermissionUtil;
import com.elderly.apartment.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/fee")
public class FeeController {

    @Autowired
    private FeeTypeService feeTypeService;

    @Autowired
    private FeeStandardService feeStandardService;

    @Autowired
    private FeeBillService feeBillService;

    @Autowired
    private FeePaymentService feePaymentService;

    @Autowired
    private FeeDetailService feeDetailService;

    @Autowired
    private FeeAccountService feeAccountService;

    // ==================== 费用类型管理 ====================

    @GetMapping("/types")
    public Result<Page<FeeType>> getFeeTypes(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<FeeType> result = feeTypeService.page(new Page<>(page, size));
        return Result.success(result);
    }

    @PostMapping("/types")
    public Result<String> addFeeType(@RequestBody FeeType feeType) {
        feeTypeService.save(feeType);
        return Result.success("添加成功", null);
    }

    @PutMapping("/types/{id}")
    public Result<String> updateFeeType(@PathVariable Long id, @RequestBody FeeType feeType) {
        feeType.setId(id);
        feeTypeService.updateById(feeType);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/types/{id}")
    public Result<String> deleteFeeType(@PathVariable Long id) {
        feeTypeService.removeById(id);
        return Result.success("删除成功", null);
    }

    // ==================== 费用标准管理 ====================

    @GetMapping("/standards")
    public Result<Page<FeeStandard>> getFeeStandards(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long feeTypeId) {
        LambdaQueryWrapper<FeeStandard> wrapper = new LambdaQueryWrapper<>();
        if (feeTypeId != null) {
            wrapper.eq(FeeStandard::getFeeTypeId, feeTypeId);
        }
        wrapper.eq(FeeStandard::getStatus, 1);
        wrapper.orderByDesc(FeeStandard::getCreateTime);
        Page<FeeStandard> result = feeStandardService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    @PostMapping("/standards")
    public Result<String> addFeeStandard(@RequestBody FeeStandard feeStandard) {
        feeStandardService.save(feeStandard);
        return Result.success("添加成功", null);
    }

    @PutMapping("/standards/{id}")
    public Result<String> updateFeeStandard(@PathVariable Long id, @RequestBody FeeStandard feeStandard) {
        feeStandard.setId(id);
        feeStandardService.updateById(feeStandard);
        return Result.success("更新成功", null);
    }

    // ==================== 账单管理 ====================

    @GetMapping("/bills")
    public Result<Page<FeeBill>> getBills(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String elderlyName,
            @RequestParam(required = false) String billMonth,
            @RequestParam(required = false) Integer status) {
        // 数据权限检查：老人只能查看自己的账单
        Integer accessibleElderlyId = DataPermissionUtil.getAccessibleElderlyId();
        if (accessibleElderlyId != null) {
            // 老人角色，只能查看自己的账单
            LambdaQueryWrapper<FeeBill> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FeeBill::getElderlyId, accessibleElderlyId);
            if (billMonth != null && !billMonth.isEmpty()) {
                wrapper.eq(FeeBill::getBillMonth, billMonth);
            }
            if (status != null) {
                wrapper.eq(FeeBill::getStatus, status);
            }
            wrapper.orderByDesc(FeeBill::getCreateTime);
            Page<FeeBill> result = feeBillService.page(new Page<>(page, size), wrapper);
            return Result.success(result);
        }
        
        Page<FeeBill> result = feeBillService.getBillList(page, size, elderlyName, billMonth, status);
        return Result.success(result);
    }

    @GetMapping("/bills/{id}")
    public Result<FeeBill> getBillDetail(@PathVariable Long id) {
        FeeBill bill = feeBillService.getBillDetail(id);
        return Result.success(bill);
    }

    @PostMapping("/bills")
    public Result<String> addBill(@RequestBody FeeBill bill) {
        feeBillService.saveBill(bill);
        return Result.success("添加成功", null);
    }

    @DeleteMapping("/bills/{id}")
    public Result<String> deleteBill(@PathVariable Long id) {
        feeBillService.removeById(id);
        return Result.success("删除成功", null);
    }

    @PostMapping("/bills/generate")
    public Result<String> generateBills(@RequestBody Map<String, String> params) {
        String billMonth = params.get("billMonth");
        if (billMonth == null || billMonth.isEmpty()) {
            billMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        feeBillService.generateMonthlyBills(billMonth);
        return Result.success("生成成功", null);
    }

    @GetMapping("/bills/{id}/details")
    public Result<List<FeeDetail>> getBillDetails(@PathVariable Long id) {
        List<FeeDetail> details = feeDetailService.getDetailsByBillId(id);
        return Result.success(details);
    }

    // ==================== 缴费管理 ====================

    @GetMapping("/payments")
    public Result<Page<FeePayment>> getPayments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String elderlyName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<FeePayment> result = feePaymentService.getPaymentList(page, size, elderlyName, startDate, endDate);
        return Result.success(result);
    }

    @PostMapping("/payments")
    public Result<String> createPayment(@RequestBody FeePayment payment) {
        feePaymentService.createPayment(payment);
        return Result.success("缴费成功", null);
    }

    @GetMapping("/payments/{id}")
    public Result<FeePayment> getPaymentDetail(@PathVariable Long id) {
        FeePayment payment = feePaymentService.getById(id);
        return Result.success(payment);
    }

    // ==================== 账户管理 ====================

    @GetMapping("/accounts/{elderlyId}")
    public Result<FeeAccount> getAccount(@PathVariable Integer elderlyId) {
        // 数据权限检查
        if (!DataPermissionUtil.hasPermissionForElderly(elderlyId)) {
            return Result.error("无权访问该账户信息");
        }
        
        FeeAccount account = feeAccountService.getByElderlyId(elderlyId);
        return Result.success(account);
    }

    // ==================== 老人端接口 ====================

    /**
     * 获取当前登录老人的账户信息
     */
    @GetMapping("/my/account")
    public Result<FeeAccount> getMyAccount() {
        Integer elderlyId = DataPermissionUtil.getElderlyIdForCurrentUser();
        if (elderlyId == null) {
            return Result.error("当前用户不是老人角色");
        }
        
        FeeAccount account = feeAccountService.getByElderlyId(elderlyId);
        return Result.success(account);
    }

    /**
     * 获取当前登录老人的账单列表
     */
    @GetMapping("/my/bills")
    public Result<Page<FeeBill>> getMyBills(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String billMonth,
            @RequestParam(required = false) Integer status) {
        Integer elderlyId = DataPermissionUtil.getElderlyIdForCurrentUser();
        if (elderlyId == null) {
            return Result.error("当前用户不是老人角色");
        }
        
        LambdaQueryWrapper<FeeBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeBill::getElderlyId, elderlyId);
        if (billMonth != null && !billMonth.isEmpty()) {
            wrapper.eq(FeeBill::getBillMonth, billMonth);
        }
        if (status != null) {
            wrapper.eq(FeeBill::getStatus, status);
        }
        wrapper.orderByDesc(FeeBill::getCreateTime);
        Page<FeeBill> result = feeBillService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    // ==================== 统计报表 ====================

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @RequestParam(required = false) String startMonth,
            @RequestParam(required = false) String endMonth) {
        Map<String, Object> statistics = feeBillService.getStatistics(startMonth, endMonth);
        return Result.success(statistics);
    }

    @GetMapping("/debtors")
    public Result<List<Map<String, Object>>> getDebtors() {
        List<Map<String, Object>> debtors = feeBillService.getDebtorList();
        return Result.success(debtors);
    }
}
