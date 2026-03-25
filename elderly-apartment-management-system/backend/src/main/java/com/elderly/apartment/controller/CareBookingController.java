package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.CareBooking;
import com.elderly.apartment.service.CareBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/care-booking")
public class CareBookingController {

    @Autowired
    private CareBookingService careBookingService;

    @GetMapping
    public Result<IPage<CareBooking>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Map<String, Object> params) {
        IPage<CareBooking> result = careBookingService.getBookingPage(page, size, params);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<CareBooking> getById(@PathVariable Long id) {
        CareBooking booking = careBookingService.getById(id);
        return Result.success(booking);
    }

    @PostMapping
    public Result<CareBooking> create(@RequestBody CareBooking booking) {
        boolean success = careBookingService.save(booking);
        if (success) {
            // 返回创建的订单对象，包含生成的ID和订单号
            return Result.success(booking);
        } else {
            return Result.error("创建预约失败");
        }
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody CareBooking booking) {
        booking.setId(id);
        boolean success = careBookingService.updateById(booking);
        return Result.success(success);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = careBookingService.removeById(id);
        return Result.success(success);
    }

    @PutMapping("/{id}/confirm")
    public Result<Boolean> confirm(@PathVariable Long id) {
        boolean success = careBookingService.confirmBooking(id);
        return Result.success(success);
    }

    @PutMapping("/{id}/complete")
    public Result<Boolean> complete(@PathVariable Long id) {
        boolean success = careBookingService.completeBooking(id);
        return Result.success(success);
    }

    @PutMapping("/{id}/cancel")
    public Result<Map<String, Object>> cancel(@PathVariable Long id) {
        CareBooking booking = careBookingService.getById(id);
        if (booking == null) {
            return Result.error("订单不存在");
        }

        // 检查是否可以取消
        String paymentStatus = booking.getPaymentStatus();
        String status = booking.getStatus();

        boolean canCancel = "pending".equals(paymentStatus) ||
                ("paid".equals(paymentStatus) && "pending".equals(status));

        if (!canCancel) {
            return Result.error("该订单状态不允许取消");
        }

        boolean success = careBookingService.cancelBooking(id);

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("refunded", "paid".equals(paymentStatus));
        result.put("message", success ? ("paid".equals(paymentStatus) ? "订单已取消并自动退款" : "订单已取消") : "取消失败");

        return Result.success(result);
    }

    @PutMapping("/{id}/assign")
    public Result<Boolean> assignNurse(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        Long nurseId = request.get("nurseId");
        System.out.println("分配护理员请求: id=" + id + ", nurseId=" + nurseId + ", request=" + request);
        if (nurseId == null) {
            return Result.error("护理员ID不能为空");
        }
        boolean success = careBookingService.assignNurse(id, nurseId);
        System.out.println("分配护理员结果: " + success);
        return Result.success(success);
    }

    @PostMapping("/{id}/pay")
    public Result<Boolean> payBooking(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        String paymentMethod = (String) request.get("paymentMethod");
        Double amount = request.get("amount") instanceof Number ? ((Number) request.get("amount")).doubleValue() : null;
        
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            return Result.error("支付方式不能为空");
        }
        if (amount == null || amount <= 0) {
            return Result.error("支付金额必须大于0");
        }
        
        boolean success = careBookingService.payBooking(id, paymentMethod, amount);
        if (success) {
            return Result.success(true);
        } else {
            return Result.error("支付失败，请检查订单状态或余额");
        }
    }
}
