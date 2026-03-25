package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.ActivityFeedback;
import com.elderly.apartment.service.ActivityFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activity-feedbacks")
public class ActivityFeedbackController {

    @Autowired
    private ActivityFeedbackService activityFeedbackService;

    @GetMapping
    public Result<Page<ActivityFeedback>> getFeedbackList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer activityId,
            @RequestParam(required = false) Integer elderlyId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer minScore) {

        Page<ActivityFeedback> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ActivityFeedback> wrapper = new LambdaQueryWrapper<>();

        if (activityId != null) {
            wrapper.eq(ActivityFeedback::getActivityId, activityId);
        }

        if (elderlyId != null) {
            wrapper.eq(ActivityFeedback::getElderlyId, elderlyId);
        }

        if (status != null) {
            wrapper.eq(ActivityFeedback::getStatus, status);
        }

        if (minScore != null) {
            wrapper.ge(ActivityFeedback::getSatisfactionScore, minScore);
        }

        wrapper.eq(ActivityFeedback::getDeleted, 0);
        wrapper.orderByDesc(ActivityFeedback::getFeedbackTime);

        Page<ActivityFeedback> result = activityFeedbackService.page(pageParam, wrapper);
        result.getRecords().forEach(this::enrichFeedback);

        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<ActivityFeedback> getFeedbackById(@PathVariable Integer id) {
        ActivityFeedback feedback = activityFeedbackService.getById(id);
        if (feedback == null) {
            return Result.error("反馈记录不存在");
        }
        enrichFeedback(feedback);
        return Result.success(feedback);
    }

    @PostMapping
    public Result<ActivityFeedback> createFeedback(@RequestBody ActivityFeedback feedback) {
        // 检查是否已反馈
        long existingFeedback = activityFeedbackService.lambdaQuery()
                .eq(ActivityFeedback::getActivityId, feedback.getActivityId())
                .eq(ActivityFeedback::getElderlyId, feedback.getElderlyId())
                .eq(ActivityFeedback::getDeleted, 0)
                .count();

        if (existingFeedback > 0) {
            return Result.error("该老人已提交过反馈");
        }

        // 计算综合评分
        if (feedback.getOverallScore() == null) {
            feedback.setOverallScore(activityFeedbackService.calculateOverallScore(feedback.getActivityId()));
        }

        feedback.setFeedbackTime(LocalDateTime.now());
        feedback.setStatus(0);

        boolean saved = activityFeedbackService.save(feedback);
        if (saved) {
            return Result.success("反馈提交成功", feedback);
        }
        return Result.error("反馈提交失败");
    }

    @PutMapping("/{id}")
    public Result<ActivityFeedback> updateFeedback(@PathVariable Integer id, @RequestBody ActivityFeedback feedback) {
        ActivityFeedback existing = activityFeedbackService.getById(id);
        if (existing == null) {
            return Result.error("反馈记录不存在");
        }

        feedback.setId(id);
        boolean updated = activityFeedbackService.updateById(feedback);
        if (updated) {
            return Result.success("更新成功", feedback);
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteFeedback(@PathVariable Integer id) {
        boolean deleted = activityFeedbackService.removeById(id);
        if (deleted) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }

    @PostMapping("/{id}/reply")
    public Result<ActivityFeedback> replyFeedback(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        ActivityFeedback feedback = activityFeedbackService.getById(id);
        if (feedback == null) {
            return Result.error("反馈记录不存在");
        }

        String reply = (String) params.get("handlerReply");
        Integer handlerId = (Integer) params.get("handlerId");
        String handlerName = (String) params.get("handlerName");

        feedback.setHandlerReply(reply);
        feedback.setHandlerId(handlerId);
        feedback.setHandlerName(handlerName);
        feedback.setReplyTime(LocalDateTime.now());
        feedback.setStatus(2); // 已回复

        activityFeedbackService.updateById(feedback);
        return Result.success("回复成功", feedback);
    }

    @PostMapping("/{id}/handle")
    public Result<ActivityFeedback> handleFeedback(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        ActivityFeedback feedback = activityFeedbackService.getById(id);
        if (feedback == null) {
            return Result.error("反馈记录不存在");
        }

        Integer handlerId = (Integer) params.get("handlerId");
        String handlerName = (String) params.get("handlerName");

        feedback.setHandlerId(handlerId);
        feedback.setHandlerName(handlerName);
        feedback.setStatus(1); // 已处理

        activityFeedbackService.updateById(feedback);
        return Result.success("处理成功", feedback);
    }

    @GetMapping("/statistics/{id}")
    public Result<Map<String, Object>> getFeedbackStatistics(@PathVariable Integer id) {
        // 首先尝试将ID作为活动ID来获取统计数据
        Map<String, Object> stats = activityFeedbackService.getActivityFeedbackStats(id);
        
        // 如果没有找到统计数据，尝试将ID作为反馈ID来查找对应的活动
        if (stats == null || stats.isEmpty()) {
            ActivityFeedback feedback = activityFeedbackService.getById(id);
            if (feedback != null) {
                Integer activityId = feedback.getActivityId();
                stats = activityFeedbackService.getActivityFeedbackStats(activityId);
                id = activityId;
            } else {
                return Result.error("未找到相关的反馈记录或活动");
            }
        }

        // 获取评分分布
        List<Map<String, Object>> distribution = activityFeedbackService.getScoreDistribution(id);
        stats.put("scoreDistribution", distribution);

        // 各维度平均分
        LambdaQueryWrapper<ActivityFeedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityFeedback::getActivityId, id);
        wrapper.eq(ActivityFeedback::getDeleted, 0);

        List<ActivityFeedback> feedbacks = activityFeedbackService.list(wrapper);

        double avgSatisfaction = feedbacks.stream()
                .filter(f -> f.getSatisfactionScore() != null)
                .mapToInt(ActivityFeedback::getSatisfactionScore)
                .average()
                .orElse(0);

        double avgOrganization = feedbacks.stream()
                .filter(f -> f.getOrganizationScore() != null)
                .mapToInt(ActivityFeedback::getOrganizationScore)
                .average()
                .orElse(0);

        double avgContent = feedbacks.stream()
                .filter(f -> f.getContentScore() != null)
                .mapToInt(ActivityFeedback::getContentScore)
                .average()
                .orElse(0);

        double avgStaff = feedbacks.stream()
                .filter(f -> f.getStaffScore() != null)
                .mapToInt(ActivityFeedback::getStaffScore)
                .average()
                .orElse(0);

        Map<String, Double> dimensionScores = new HashMap<>();
        dimensionScores.put("satisfaction", Math.round(avgSatisfaction * 100.0) / 100.0);
        dimensionScores.put("organization", Math.round(avgOrganization * 100.0) / 100.0);
        dimensionScores.put("content", Math.round(avgContent * 100.0) / 100.0);
        dimensionScores.put("staff", Math.round(avgStaff * 100.0) / 100.0);

        stats.put("dimensionScores", dimensionScores);

        // 推荐率
        long recommendCount = feedbacks.stream()
                .filter(f -> f.getIsWouldRecommend() != null && f.getIsWouldRecommend() == 1)
                .count();

        if (!feedbacks.isEmpty()) {
            double recommendRate = (double) recommendCount / feedbacks.size() * 100;
            stats.put("recommendRate", Math.round(recommendRate * 100.0) / 100.0);
        } else {
            stats.put("recommendRate", 0.0);
        }

        return Result.success(stats);
    }

    private void enrichFeedback(ActivityFeedback feedback) {
        if (feedback == null) return;

        // 设置状态文本
        feedback.setStatusText(getStatusText(feedback.getStatus()));
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待处理";
            case 1: return "已处理";
            case 2: return "已回复";
            default: return "未知";
        }
    }
}
