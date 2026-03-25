package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.RoomServiceEvaluation;
import com.elderly.apartment.entity.RoomServiceRequest;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.security.CustomUserDetails;
import com.elderly.apartment.service.RoomServiceEvaluationService;
import com.elderly.apartment.service.RoomServiceRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/room-service")
public class RoomServiceController {

    @Autowired
    private RoomServiceRequestService requestService;

    @Autowired
    private RoomServiceEvaluationService evaluationService;

    @GetMapping("/requests")
    public Result<Page<RoomServiceRequest>> getRequestList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) Integer elderlyId,
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String urgency,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        Integer userId = getCurrentUserId();
        Page<RoomServiceRequest> result = requestService.getPageWithDetail(
                page, size, roomId, elderlyId, userId, serviceType, status, urgency, startDate, endDate);
        return Result.success(result);
    }

    @GetMapping("/requests/all")
    public Result<Page<RoomServiceRequest>> getAllRequests(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) Integer elderlyId,
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String urgency,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        Page<RoomServiceRequest> result = requestService.getPageWithDetail(
                page, size, roomId, elderlyId, null, serviceType, status, urgency, startDate, endDate);
        return Result.success(result);
    }

    @GetMapping("/requests/{id}")
    public Result<RoomServiceRequest> getRequestById(@PathVariable Integer id) {
        RoomServiceRequest request = requestService.getDetailById(id);
        if (request == null) {
            return Result.error("服务申请不存在");
        }
        return Result.success(request);
    }

    @PostMapping("/requests")
    public Result<RoomServiceRequest> createRequest(@RequestBody RoomServiceRequest request) {
        // 生成申请编号
        request.setRequestNo(requestService.generateRequestNo());

        // 设置申请人
        Integer userId = getCurrentUserId();
        request.setUserId(userId);

        // 设置初始状态
        request.setStatus("PENDING");

        requestService.save(request);
        return Result.success(request);
    }

    @PutMapping("/requests/{id}")
    public Result<RoomServiceRequest> updateRequest(@PathVariable Integer id, @RequestBody RoomServiceRequest request) {
        RoomServiceRequest existing = requestService.getById(id);
        if (existing == null) {
            return Result.error("服务申请不存在");
        }

        // 只能修改待处理的申请
        if (!"PENDING".equals(existing.getStatus())) {
            return Result.error("只能修改待处理的服务申请");
        }

        request.setId(id);
        requestService.updateById(request);
        return Result.success(requestService.getDetailById(id));
    }

    @DeleteMapping("/requests/{id}")
    public Result<Void> deleteRequest(@PathVariable Integer id) {
        RoomServiceRequest existing = requestService.getById(id);
        if (existing == null) {
            return Result.error("服务申请不存在");
        }

        // 只能删除待处理或已取消的申请
        if (!"PENDING".equals(existing.getStatus()) && !"CANCELLED".equals(existing.getStatus())) {
            return Result.error("只能删除待处理或已取消的服务申请");
        }

        requestService.removeById(id);
        return Result.success(null);
    }

    @PostMapping("/requests/{id}/assign")
    public Result<Void> assignHandler(@PathVariable Integer id,
                                       @RequestParam Integer handlerId,
                                       @RequestParam String handlerName) {
        boolean success = requestService.assignHandler(id, handlerId, handlerName);
        if (!success) {
            return Result.error("分配处理人失败");
        }
        return Result.success(null);
    }

    @PostMapping("/requests/{id}/start")
    public Result<Void> startHandle(@PathVariable Integer id, @RequestParam(required = false) String notes) {
        boolean success = requestService.startHandle(id, notes);
        if (!success) {
            return Result.error("开始处理失败");
        }
        return Result.success(null);
    }

    @PostMapping("/requests/{id}/complete")
    public Result<Void> completeRequest(@PathVariable Integer id, @RequestParam(required = false) String notes) {
        boolean success = requestService.completeRequest(id, notes);
        if (!success) {
            return Result.error("完成服务失败");
        }
        return Result.success(null);
    }

    @PostMapping("/requests/{id}/cancel")
    public Result<Void> cancelRequest(@PathVariable Integer id, @RequestParam(required = false) String reason) {
        boolean success = requestService.cancelRequest(id, reason);
        if (!success) {
            return Result.error("取消服务失败");
        }
        return Result.success(null);
    }

    @GetMapping("/requests/room/{roomId}/recent")
    public Result<List<RoomServiceRequest>> getRecentByRoom(@PathVariable Integer roomId,
                                                             @RequestParam(defaultValue = "5") Integer limit) {
        List<RoomServiceRequest> list = requestService.getRecentByRoomId(roomId, limit);
        return Result.success(list);
    }

    @GetMapping("/evaluations")
    public Result<Page<RoomServiceEvaluation>> getEvaluationList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) Integer elderlyId,
            @RequestParam(required = false) Integer requestId,
            @RequestParam(required = false) Integer minRating) {

        Page<RoomServiceEvaluation> result = evaluationService.getPageWithDetail(
                page, size, roomId, elderlyId, requestId, minRating);
        return Result.success(result);
    }

    @GetMapping("/evaluations/{id}")
    public Result<RoomServiceEvaluation> getEvaluationById(@PathVariable Integer id) {
        RoomServiceEvaluation evaluation = evaluationService.getDetailById(id);
        if (evaluation == null) {
            return Result.error("评价不存在");
        }
        return Result.success(evaluation);
    }

    @PostMapping("/evaluations")
    public Result<RoomServiceEvaluation> createEvaluation(@RequestBody RoomServiceEvaluation evaluation) {
        try {
            Integer userId = getCurrentUserId();
            evaluation.setUserId(userId);

            RoomServiceEvaluation created = evaluationService.createEvaluation(evaluation);
            return Result.success(created);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/evaluations/room/{roomId}/stats")
    public Result<Map<String, Object>> getRoomEvaluationStats(@PathVariable Integer roomId) {
        Map<String, Object> stats = evaluationService.getRoomRatingStats(roomId);
        List<Map<String, Object>> distribution = evaluationService.getRoomRatingDistribution(roomId);

        Map<String, Object> result = new HashMap<>();
        result.put("stats", stats);
        result.put("distribution", distribution);
        return Result.success(result);
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 服务类型统计
        List<Map<String, Object>> typeStats = requestService.getTypeStats();
        stats.put("typeStats", typeStats);

        // 状态统计
        List<Map<String, Object>> statusStats = requestService.getStatusStats();
        stats.put("statusStats", statusStats);

        // 待处理数量
        Integer pendingCount = requestService.getPendingCount();
        stats.put("pendingCount", pendingCount);

        // 评价统计
        Map<String, Object> ratingStats = evaluationService.getOverallRatingStats();
        if (ratingStats != null) {
            stats.put("avgRating", ratingStats.get("avg_rating"));
            stats.put("totalEvaluations", ratingStats.get("total_count"));
            stats.put("goodCount", ratingStats.get("good_count"));
            stats.put("badCount", ratingStats.get("bad_count"));
        }

        return Result.success(stats);
    }

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUser().getId();
        }
        return null;
    }
}
