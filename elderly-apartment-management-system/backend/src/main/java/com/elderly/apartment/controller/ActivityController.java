package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.Activity;
import com.elderly.apartment.entity.ActivityCategory;
import com.elderly.apartment.service.ActivityCategoryService;
import com.elderly.apartment.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityCategoryService activityCategoryService;

    @GetMapping("/{id}")
    public Result<Activity> getActivityById(@PathVariable Integer id) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        // 增加浏览次数
        activity.setViewCount(activity.getViewCount() + 1);
        activityService.updateById(activity);

        enrichActivity(activity);
        return Result.success(activity);
    }

    @GetMapping
    public Result<Page<Activity>> getActivityList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer activityType,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Boolean isRecommended) {

        Page<Activity> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();

        if (title != null && !title.isEmpty()) {
            wrapper.like(Activity::getTitle, title);
        }

        // 支持通过 categoryId 或 activityType 查询
        if (categoryId != null) {
            wrapper.eq(Activity::getCategoryId, categoryId);
        } else if (activityType != null) {
            wrapper.eq(Activity::getActivityType, activityType);
        }

        if (status != null) {
            wrapper.eq(Activity::getStatus, status);
        }

        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(Activity::getActivityDate, LocalDate.parse(startDate));
        }

        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(Activity::getActivityDate, LocalDate.parse(endDate));
        }

        if (isRecommended != null) {
            wrapper.eq(Activity::getIsRecommended, isRecommended ? 1 : 0);
        }

        wrapper.eq(Activity::getDeleted, 0);
        wrapper.orderByDesc(Activity::getCreateTime);

        Page<Activity> result = activityService.page(pageParam, wrapper);

        // 丰富活动信息
        result.getRecords().forEach(this::enrichActivity);

        return Result.success(result);
    }

    @GetMapping("/upcoming")
    public Result<List<Activity>> getUpcomingActivities(
            @RequestParam(defaultValue = "5") Integer limit) {

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        // 查询今天及以后的活动
        wrapper.ge(Activity::getActivityDate, today);
        // 排除已取消和已结束的活动
        wrapper.notIn(Activity::getStatus, 4, 5);
        wrapper.eq(Activity::getDeleted, 0);
        wrapper.orderByAsc(Activity::getActivityDate);
        wrapper.last("LIMIT " + limit);

        List<Activity> activities = activityService.list(wrapper);
        // 过滤掉已经开始的活动，并丰富信息
        activities = activities.stream()
                .filter(a -> {
                    if (a.getActivityDate() == null) return false;
                    LocalDateTime activityStart = a.getStartTime() != null ?
                            LocalDateTime.of(a.getActivityDate(), a.getStartTime()) :
                            a.getActivityDate().atStartOfDay();
                    // 只返回未开始的活动
                    return now.isBefore(activityStart);
                })
                .peek(this::enrichActivity)
                .collect(java.util.stream.Collectors.toList());

        return Result.success(activities);
    }

    @GetMapping("/recommended")
    public Result<List<Activity>> getRecommendedActivities(
            @RequestParam(defaultValue = "5") Integer limit) {

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getIsRecommended, 1);
        // 查询今天及以后的活动
        wrapper.ge(Activity::getActivityDate, today);
        // 排除已取消和已结束的活动
        wrapper.notIn(Activity::getStatus, 4, 5);
        wrapper.eq(Activity::getDeleted, 0);
        wrapper.orderByDesc(Activity::getCreateTime);
        wrapper.last("LIMIT " + limit);

        List<Activity> activities = activityService.list(wrapper);
        // 过滤掉已经开始的活动，并丰富信息
        activities = activities.stream()
                .filter(a -> {
                    if (a.getActivityDate() == null) return false;
                    LocalDateTime activityStart = a.getStartTime() != null ?
                            LocalDateTime.of(a.getActivityDate(), a.getStartTime()) :
                            a.getActivityDate().atStartOfDay();
                    // 只返回未开始的活动
                    return now.isBefore(activityStart);
                })
                .peek(this::enrichActivity)
                .collect(java.util.stream.Collectors.toList());

        return Result.success(activities);
    }

    @PostMapping
    public Result<Activity> createActivity(@RequestBody Activity activity) {
        // 设置默认值
        if (activity.getCurrentParticipants() == null) {
            activity.setCurrentParticipants(0);
        }
        if (activity.getCheckedInCount() == null) {
            activity.setCheckedInCount(0);
        }
        if (activity.getViewCount() == null) {
            activity.setViewCount(0);
        }
        if (activity.getStatus() == null) {
            activity.setStatus(0);
        }

        // 自动计算状态
        updateActivityStatus(activity);

        boolean saved = activityService.save(activity);
        if (saved) {
            return Result.success("活动创建成功", activity);
        }
        return Result.error("活动创建失败");
    }

    @PutMapping("/{id}")
    public Result<Activity> updateActivity(@PathVariable Integer id, @RequestBody Activity activity) {
        Activity existing = activityService.getById(id);
        if (existing == null) {
            return Result.error("活动不存在");
        }

        activity.setId(id);
        updateActivityStatus(activity);

        boolean updated = activityService.updateById(activity);
        if (updated) {
            return Result.success("活动更新成功", activity);
        }
        return Result.error("活动更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteActivity(@PathVariable Integer id) {
        boolean deleted = activityService.removeById(id);
        if (deleted) {
            return Result.success("活动删除成功", null);
        }
        return Result.error("活动删除失败");
    }

    @PostMapping("/{id}/cancel")
    public Result<Activity> cancelActivity(@PathVariable Integer id) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }

        activity.setStatus(5);
        activityService.updateById(activity);

        return Result.success("活动已取消", activity);
    }

    @PostMapping("/{id}/publish")
    public Result<Activity> publishActivity(@PathVariable Integer id) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }

        activity.setStatus(1);
        activityService.updateById(activity);

        return Result.success("活动已发布", activity);
    }

    @PutMapping("/{id}/status")
    public Result<Activity> updateActivityStatus(@PathVariable Integer id, @RequestBody java.util.Map<String, Integer> params) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }

        Integer status = params.get("status");
        if (status == null || status < 0 || status > 5) {
            return Result.error("状态值无效，有效范围：0-5");
        }

        activity.setStatus(status);
        boolean updated = activityService.updateById(activity);
        if (updated) {
            String statusText = getStatusText(status);
            return Result.success("状态已更新为" + statusText, activity);
        }
        return Result.error("状态更新失败");
    }

    @GetMapping("/statistics/overview")
    public Result<Map<String, Object>> getActivityStatistics() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        // 总活动数（不包括已删除的）
        long totalActivities = activityService.lambdaQuery()
                .eq(Activity::getDeleted, 0)
                .count();
        stats.put("totalActivities", totalActivities);

        // 本月活动数
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        long monthActivities = activityService.lambdaQuery()
                .ge(Activity::getActivityDate, firstDayOfMonth)
                .eq(Activity::getDeleted, 0)
                .count();
        stats.put("monthActivities", monthActivities);

        // 进行中活动数（基于实际时间判断）
        long ongoingActivities = activityService.lambdaQuery()
                .eq(Activity::getDeleted, 0)
                .le(Activity::getActivityDate, today)
                .list()
                .stream()
                .filter(a -> {
                    if (a.getActivityDate() == null) return false;
                    LocalDateTime activityStart = a.getStartTime() != null ?
                            LocalDateTime.of(a.getActivityDate(), a.getStartTime()) :
                            a.getActivityDate().atStartOfDay();
                    LocalDateTime activityEnd = a.getEndTime() != null ?
                            LocalDateTime.of(a.getActivityDate(), a.getEndTime()) :
                            activityStart.plusHours(2);
                    return now.isAfter(activityStart) && now.isBefore(activityEnd);
                })
                .count();
        stats.put("ongoingActivities", ongoingActivities);

        // 报名中活动数（基于实际时间判断）
        long registeringActivities = activityService.lambdaQuery()
                .eq(Activity::getDeleted, 0)
                .ge(Activity::getActivityDate, today)
                .and(w -> w.isNull(Activity::getRegistrationEndTime)
                        .or().gt(Activity::getRegistrationEndTime, now))
                .list()
                .stream()
                .filter(a -> {
                    // 检查报名开始时间
                    if (a.getRegistrationStartTime() != null && now.isBefore(a.getRegistrationStartTime())) {
                        return false;
                    }
                    // 检查活动是否未开始
                    if (a.getActivityDate() != null) {
                        LocalDateTime activityStart = a.getStartTime() != null ?
                                LocalDateTime.of(a.getActivityDate(), a.getStartTime()) :
                                a.getActivityDate().atStartOfDay();
                        return now.isBefore(activityStart);
                    }
                    return true;
                })
                .count();
        stats.put("registeringActivities", registeringActivities);

        return Result.success(stats);
    }

    @GetMapping("/categories")
    public Result<List<ActivityCategory>> getCategories(
            @RequestParam(required = false) Integer isActive) {
        LambdaQueryWrapper<ActivityCategory> wrapper = new LambdaQueryWrapper<>();

        // 如果明确指定了 isActive 参数，则按参数过滤；否则展示所有未删除的分类
        if (isActive != null) {
            wrapper.eq(ActivityCategory::getIsActive, isActive);
        }

        // 只过滤已删除的数据（处理 null 和 0 的情况）
        wrapper.and(w -> w.eq(ActivityCategory::getDeleted, 0).or().isNull(ActivityCategory::getDeleted));
        wrapper.orderByAsc(ActivityCategory::getSortOrder);

        List<ActivityCategory> categories = activityCategoryService.list(wrapper);
        return Result.success(categories);
    }

    @GetMapping("/categories/{id}")
    public Result<ActivityCategory> getCategoryById(@PathVariable Integer id) {
        ActivityCategory category = activityCategoryService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        return Result.success(category);
    }

    @PostMapping("/categories")
    public Result<ActivityCategory> createCategory(@RequestBody ActivityCategory category) {
        long count = activityCategoryService.lambdaQuery()
                .eq(ActivityCategory::getName, category.getName())
                .eq(ActivityCategory::getDeleted, 0)
                .count();
        
        if (count > 0) {
            return Result.error("分类名称已存在");
        }
        
        category.setCreateTime(LocalDateTime.now());
        category.setIsActive(1);
        category.setDeleted(0);
        
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        
        boolean saved = activityCategoryService.save(category);
        if (saved) {
            return Result.success("创建成功", category);
        }
        return Result.error("创建失败");
    }

    @PutMapping("/categories/{id}")
    public Result<ActivityCategory> updateCategory(@PathVariable Integer id, @RequestBody ActivityCategory category) {
        ActivityCategory existing = activityCategoryService.getById(id);
        if (existing == null) {
            return Result.error("分类不存在");
        }
        
        long count = activityCategoryService.lambdaQuery()
                .eq(ActivityCategory::getName, category.getName())
                .eq(ActivityCategory::getDeleted, 0)
                .ne(ActivityCategory::getId, id)
                .count();
        
        if (count > 0) {
            return Result.error("分类名称已存在");
        }
        
        category.setId(id);
        boolean updated = activityCategoryService.updateById(category);
        if (updated) {
            return Result.success("更新成功", category);
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Integer id) {
        ActivityCategory category = activityCategoryService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        
        category.setDeleted(1);
        boolean deleted = activityCategoryService.updateById(category);
        if (deleted) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }

    @PutMapping("/categories/{id}/status")
    public Result<ActivityCategory> updateCategoryStatus(@PathVariable Integer id, @RequestBody java.util.Map<String, Integer> params) {
        ActivityCategory category = activityCategoryService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        
        Integer status = params.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态值无效");
        }
        
        category.setIsActive(status);
        boolean updated = activityCategoryService.updateById(category);
        if (updated) {
            return Result.success("状态更新成功", category);
        }
        return Result.error("状态更新失败");
    }

    private void enrichActivity(Activity activity) {
        if (activity == null) return;

        // 根据当前时间自动判断并更新活动状态
        updateActivityStatusByTime(activity);

        // 设置状态文本
        activity.setStatusText(getStatusText(activity.getStatus()));

        // 如果有 categoryId，从分类表获取分类名称
        if (activity.getCategoryId() != null) {
            ActivityCategory category = activityCategoryService.getById(activity.getCategoryId());
            if (category != null) {
                activity.setActivityTypeText(category.getName());
            } else {
                activity.setActivityTypeText(getActivityTypeText(activity.getActivityType()));
            }
        } else {
            // 设置活动类型文本（兼容旧数据）
            activity.setActivityTypeText(getActivityTypeText(activity.getActivityType()));
        }

        // 计算参与率
        if (activity.getMaxParticipants() != null && activity.getMaxParticipants() > 0) {
            double rate = (double) activity.getCurrentParticipants() / activity.getMaxParticipants() * 100;
            activity.setParticipationRate(Math.round(rate * 100.0) / 100.0);
        } else {
            activity.setParticipationRate(0.0);
        }

        // 判断是否可报名（基于报名时间和活动状态）
        activity.setCanSignup(canSignup(activity));

        // 判断是否已满
        activity.setIsFull(activity.getMaxParticipants() != null &&
                activity.getMaxParticipants() > 0 &&
                activity.getCurrentParticipants() >= activity.getMaxParticipants());
    }

    /**
     * 根据当前时间自动判断并更新活动状态
     * 状态规则：
     * 0-草稿, 1-报名中, 2-即将开始, 3-进行中, 4-已结束, 5-已取消
     */
    private void updateActivityStatusByTime(Activity activity) {
        if (activity == null || activity.getActivityDate() == null) {
            return;
        }

        // 已取消的活动状态保持不变
        if (activity.getStatus() != null && activity.getStatus() == 5) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDate activityDate = activity.getActivityDate();
        LocalTime startTime = activity.getStartTime();
        LocalTime endTime = activity.getEndTime();

        // 构建活动开始和结束时间
        LocalDateTime activityStart = startTime != null ?
                LocalDateTime.of(activityDate, startTime) :
                activityDate.atStartOfDay();
        LocalDateTime activityEnd = endTime != null ?
                LocalDateTime.of(activityDate, endTime) :
                activityStart.plusHours(2);

        // 根据当前时间判断状态
        Integer newStatus = null;

        if (now.isAfter(activityEnd)) {
            // 当前时间已过活动结束时间 -> 已结束
            newStatus = 4;
        } else if (now.isAfter(activityStart) && now.isBefore(activityEnd)) {
            // 当前时间在活动开始和结束之间 -> 进行中
            newStatus = 3;
        } else if (now.isAfter(activityStart.minusHours(1)) && now.isBefore(activityStart)) {
            // 活动开始前1小时内 -> 即将开始
            newStatus = 2;
        } else {
            // 活动开始前超过1小时
            // 检查报名截止时间
            LocalDateTime registrationEndTime = activity.getRegistrationEndTime();
            LocalDateTime registrationStartTime = activity.getRegistrationStartTime();

            if (registrationEndTime != null && now.isAfter(registrationEndTime)) {
                // 报名已截止但活动未开始 -> 报名结束（使用"即将开始"状态或保持"报名中"）
                // 这里选择使用"即将开始"表示报名已结束等待活动开始
                if (now.isBefore(activityStart.minusHours(1))) {
                    newStatus = 1; // 报名中（报名已截止但距离活动开始还有一段时间）
                } else {
                    newStatus = 2; // 即将开始
                }
            } else if (registrationStartTime != null && now.isBefore(registrationStartTime)) {
                // 报名尚未开始 -> 草稿状态
                newStatus = 0;
            } else {
                // 报名进行中 -> 报名中
                newStatus = 1;
            }
        }

        // 更新状态（如果发生变化）
        if (newStatus != null && !newStatus.equals(activity.getStatus())) {
            activity.setStatus(newStatus);
            // 异步更新数据库中的状态
            activityService.updateById(activity);
        }
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "草稿";
            case 1: return "报名中";
            case 2: return "即将开始";
            case 3: return "进行中";
            case 4: return "已结束";
            case 5: return "已取消";
            default: return "未知";
        }
    }

    private String getActivityTypeText(Integer type) {
        if (type == null) return "其他";
        switch (type) {
            case 1: return "文化娱乐";
            case 2: return "体育健身";
            case 3: return "知识讲座";
            case 4: return "手工艺术";
            case 5: return "节日庆祝";
            case 6: return "社交活动";
            case 7: return "其他";
            default: return "其他";
        }
    }

    private boolean canSignup(Activity activity) {
        // 只有状态为"报名中"(1)的活动可以报名
        if (activity.getStatus() == null || activity.getStatus() != 1) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        // 检查报名开始时间
        if (activity.getRegistrationStartTime() != null &&
                now.isBefore(activity.getRegistrationStartTime())) {
            return false;
        }

        // 检查报名截止时间
        if (activity.getRegistrationEndTime() != null &&
                now.isAfter(activity.getRegistrationEndTime())) {
            return false;
        }

        // 检查活动是否已经开始或已结束
        if (activity.getActivityDate() != null) {
            LocalDate activityDate = activity.getActivityDate();
            LocalTime startTime = activity.getStartTime();
            LocalDateTime activityStart = startTime != null ?
                    LocalDateTime.of(activityDate, startTime) :
                    activityDate.atStartOfDay();

            // 如果活动已经开始，不能报名
            if (now.isAfter(activityStart)) {
                return false;
            }
        }

        // 检查是否已满
        if (activity.getMaxParticipants() != null &&
                activity.getMaxParticipants() > 0 &&
                activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            return false;
        }

        return true;
    }

    private void updateActivityStatus(Activity activity) {
        if (activity.getActivityDate() == null || activity.getStartTime() == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime activityStart = LocalDateTime.of(activity.getActivityDate(), activity.getStartTime());
        LocalDateTime activityEnd = activity.getEndTime() != null ?
                LocalDateTime.of(activity.getActivityDate(), activity.getEndTime()) : activityStart.plusHours(2);

        // 根据时间自动更新状态
        if (activity.getStatus() != null && activity.getStatus() == 5) {
            // 已取消状态保持不变
            return;
        }

        if (now.isBefore(activityStart.minusHours(1))) {
            // 活动开始前1小时，状态为报名中
            if (activity.getStatus() == null || activity.getStatus() < 2) {
                activity.setStatus(1);
            }
        } else if (now.isBefore(activityStart)) {
            // 活动即将开始
            activity.setStatus(2);
        } else if (now.isAfter(activityStart) && now.isBefore(activityEnd)) {
            // 活动进行中
            activity.setStatus(3);
        } else if (now.isAfter(activityEnd)) {
            // 活动已结束
            activity.setStatus(4);
        }
    }
}
