package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.CallCaregiver;
import com.elderly.apartment.service.CallCaregiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/call-caregiver")
public class CallCaregiverController {

    @Autowired
    private CallCaregiverService callCaregiverService;

    @GetMapping("/list")
    public Result<List<CallCaregiver>> getAllCalls() {
        List<CallCaregiver> list = callCaregiverService.getAllCallCaregivers();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<IPage<CallCaregiver>> getCallPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String elderlyName,
            @RequestParam(required = false) String callType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer urgencyLevel) {
        Page<CallCaregiver> pageObj = new Page<>(page, size);
        IPage<CallCaregiver> result = callCaregiverService.getCallCaregiverPage(pageObj, elderlyName, callType, status, urgencyLevel);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<CallCaregiver> getCallById(@PathVariable Integer id) {
        CallCaregiver callCaregiver = callCaregiverService.getById(id);
        return Result.success(callCaregiver);
    }

    @GetMapping("/elderly/{elderlyId}")
    public Result<List<CallCaregiver>> getCallsByElderlyId(@PathVariable Integer elderlyId) {
        List<CallCaregiver> list = callCaregiverService.getCallCaregiversByElderlyId(elderlyId);
        return Result.success(list);
    }

    @GetMapping("/status/{status}")
    public Result<List<CallCaregiver>> getCallsByStatus(@PathVariable Integer status) {
        List<CallCaregiver> list = callCaregiverService.getCallCaregiversByStatus(status);
        return Result.success(list);
    }

    @GetMapping("/nurse/{nurseId}")
    public Result<List<CallCaregiver>> getCallsByNurseId(@PathVariable Integer nurseId) {
        List<CallCaregiver> list = callCaregiverService.getCallCaregiversByNurseId(nurseId);
        return Result.success(list);
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        int pendingCount = callCaregiverService.getPendingCount();
        int todayCount = callCaregiverService.getTodayCount();
        return Result.success(Map.of("pendingCount", pendingCount, "todayCount", todayCount));
    }

    @PostMapping
    public Result<CallCaregiver> createCall(@RequestBody CallCaregiver callCaregiver) {
        boolean success = callCaregiverService.createCallCaregiver(callCaregiver);
        if (success) {
            return Result.success("呼叫护工创建成功", callCaregiver);
        }
        return Result.error("呼叫护工创建失败");
    }

    @PutMapping("/{id}")
    public Result<CallCaregiver> updateCall(@PathVariable Integer id, @RequestBody CallCaregiver callCaregiver) {
        callCaregiver.setId(id);
        boolean success = callCaregiverService.updateCallCaregiver(callCaregiver);
        if (success) {
            return Result.success("呼叫护工更新成功", callCaregiver);
        }
        return Result.error("呼叫护工更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCall(@PathVariable Integer id) {
        boolean success = callCaregiverService.deleteCallCaregiver(id);
        if (success) {
            return Result.success("呼叫护工删除成功", null);
        }
        return Result.error("呼叫护工删除失败");
    }

    @PutMapping("/{id}/assign")
    public Result<Void> assignNurse(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        Integer nurseId = (Integer) params.get("nurseId");
        String nurseName = (String) params.get("nurseName");
        boolean success = callCaregiverService.assignNurse(id, nurseId, nurseName);
        if (success) {
            return Result.success("分配护士成功", null);
        }
        return Result.error("分配护士失败");
    }

    @PutMapping("/{id}/respond")
    public Result<Void> respondCall(@PathVariable Integer id) {
        boolean success = callCaregiverService.respondToCall(id);
        if (success) {
            return Result.success("响应呼叫成功", null);
        }
        return Result.error("响应呼叫失败");
    }

    @PutMapping("/{id}/complete")
    public Result<Void> completeCall(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        String remark = (String) params.get("remark");
        boolean success = callCaregiverService.completeCall(id, remark);
        if (success) {
            return Result.success("完成处理成功", null);
        }
        return Result.error("完成处理失败");
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancelCall(@PathVariable Integer id) {
        boolean success = callCaregiverService.cancelCall(id);
        if (success) {
            return Result.success("取消呼叫成功", null);
        }
        return Result.error("取消呼叫失败");
    }
}
