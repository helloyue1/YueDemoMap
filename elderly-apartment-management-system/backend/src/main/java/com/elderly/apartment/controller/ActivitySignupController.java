package com.elderly.apartment.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.Activity;
import com.elderly.apartment.entity.ActivitySignup;
import com.elderly.apartment.entity.Room;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.service.ActivityService;
import com.elderly.apartment.service.ActivitySignupService;
import com.elderly.apartment.service.RoomService;
import com.elderly.apartment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/activity-signups")
public class ActivitySignupController {

    @Autowired
    private ActivitySignupService activitySignupService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    // 二维码签名密钥
    @Value("${qrcode.secret:elderly-apartment-checkin-secret-key}")
    private String qrCodeSecret;

    // 二维码有效期（分钟）
    @Value("${qrcode.expire-minutes:30}")
    private Integer qrCodeExpireMinutes;

    // 存储生成的二维码令牌（用于验证）
    private static final Map<String, QrCodeToken> qrCodeTokenMap = new ConcurrentHashMap<>();

    // 二维码令牌类
    private static class QrCodeToken {
        Integer signupId;
        Integer activityId;
        Integer elderlyId;
        LocalDateTime createTime;
        LocalDateTime expireTime;
        boolean used;
        boolean isActivityQr; // 是否为活动二维码

        QrCodeToken(Integer signupId, Integer activityId, Integer elderlyId, int expireMinutes) {
            this.signupId = signupId;
            this.activityId = activityId;
            this.elderlyId = elderlyId;
            this.createTime = LocalDateTime.now();
            this.expireTime = this.createTime.plusMinutes(expireMinutes);
            this.used = false;
            this.isActivityQr = false;
        }

        // 活动二维码构造函数
        QrCodeToken(Integer activityId, int expireMinutes, boolean isActivityQr) {
            this.activityId = activityId;
            this.createTime = LocalDateTime.now();
            this.expireTime = this.createTime.plusMinutes(expireMinutes);
            this.used = false;
            this.isActivityQr = isActivityQr;
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(expireTime);
        }
    }

    @GetMapping
    public Result<Page<ActivitySignup>> getActivitySignupList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer activityId,
            @RequestParam(required = false) String elderlyName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        Page<ActivitySignup> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();

        if (activityId != null) {
            wrapper.eq(ActivitySignup::getActivityId, activityId);
        }

        if (elderlyName != null && !elderlyName.isEmpty()) {
            wrapper.like(ActivitySignup::getElderlyName, elderlyName);
        }

        if (status != null) {
            wrapper.eq(ActivitySignup::getStatus, status);
        }

        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(ActivitySignup::getSignupTime, LocalDateTime.parse(startDate + "T00:00:00"));
        }

        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(ActivitySignup::getSignupTime, LocalDateTime.parse(endDate + "T23:59:59"));
        }

        wrapper.eq(ActivitySignup::getDeleted, 0);
        
        // 默认只查询有效的报名记录（待审核、已报名、已签到、候补中），排除已取消和已拒绝的
        if (status == null) {
            wrapper.in(ActivitySignup::getStatus, 0, 1, 2, 5);
        }
        
        wrapper.orderByDesc(ActivitySignup::getSignupTime);

        Page<ActivitySignup> result = activitySignupService.page(pageParam, wrapper);

        // 丰富报名信息
        result.getRecords().forEach(this::enrichSignup);

        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<ActivitySignup> getSignupById(@PathVariable Integer id) {
        ActivitySignup signup = activitySignupService.getById(id);
        if (signup == null) {
            return Result.error("报名记录不存在");
        }
        enrichSignup(signup);
        return Result.success(signup);
    }

    @PostMapping
    public Result<ActivitySignup> createSignup(@RequestBody ActivitySignup signup) {
        // 检查活动是否存在
        Activity activity = activityService.getById(signup.getActivityId());
        if (activity == null) {
            return Result.error("活动不存在");
        }

        // 检查住客是否存在
        User user = userService.getById(signup.getElderlyId());
        if (user == null) {
            return Result.error("住客信息不存在");
        }

        // 检查是否已报名（只检查有效的报名状态：待审核0、已报名1、已签到2、候补中5）
        ActivitySignup existingSignup = activitySignupService.lambdaQuery()
                .eq(ActivitySignup::getActivityId, signup.getActivityId())
                .eq(ActivitySignup::getElderlyId, signup.getElderlyId())
                .eq(ActivitySignup::getDeleted, 0)
                .in(ActivitySignup::getStatus, 0, 1, 2, 5) // 只检查有效状态
                .one();

        if (existingSignup != null) {
            return Result.success("该老人已报名此活动", existingSignup);
        }

        // 检查是否存在已取消或已拒绝的记录，如果存在则更新该记录
        ActivitySignup cancelledSignup = activitySignupService.lambdaQuery()
                .eq(ActivitySignup::getActivityId, signup.getActivityId())
                .eq(ActivitySignup::getElderlyId, signup.getElderlyId())
                .eq(ActivitySignup::getDeleted, 0)
                .in(ActivitySignup::getStatus, 3, 4) // 已取消或已拒绝
                .one();

        if (cancelledSignup != null) {
            // 更新已取消/已拒绝的记录为重新报名
            cancelledSignup.setStatus(1); // 已报名
            cancelledSignup.setSignupTime(LocalDateTime.now());
            cancelledSignup.setCancelReason(null);
            cancelledSignup.setRejectReason(null);
            cancelledSignup.setIsWaitlist(0);
            cancelledSignup.setWaitlistOrder(null);

            // 检查是否需要进入候补
            if (activity.getMaxParticipants() != null &&
                    activity.getMaxParticipants() > 0 &&
                    activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
                cancelledSignup.setStatus(5); // 候补中
                cancelledSignup.setIsWaitlist(1);
                // 计算候补序号
                long waitlistCount = activitySignupService.lambdaQuery()
                        .eq(ActivitySignup::getActivityId, signup.getActivityId())
                        .eq(ActivitySignup::getIsWaitlist, 1)
                        .eq(ActivitySignup::getDeleted, 0)
                        .count();
                cancelledSignup.setWaitlistOrder((int) waitlistCount + 1);
            }

            boolean updated = activitySignupService.updateById(cancelledSignup);
            if (updated) {
                // 更新活动报名人数
                updateActivityParticipantCount(signup.getActivityId());
                return Result.success("报名成功", cancelledSignup);
            }
            return Result.error("报名失败");
        }

        // 自动填充信息
        signup.setActivityTitle(activity.getTitle());
        signup.setElderlyName(user.getRealName());
        signup.setElderlyPhone(user.getPhone());
        signup.setRoomNumber(user.getRoomNumber());
        signup.setEmergencyContactName(user.getEmergencyContact());
        signup.setEmergencyContactPhone(user.getEmergencyPhone());
        signup.setHealthStatus(user.getHealthStatus());
        signup.setSignupTime(LocalDateTime.now());

        // 设置默认状态
        if (signup.getStatus() == null) {
            signup.setStatus(1); // 已报名
        }

        // 检查是否需要进入候补
        if (activity.getMaxParticipants() != null &&
                activity.getMaxParticipants() > 0 &&
                activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            signup.setStatus(5); // 候补中
            signup.setIsWaitlist(1);
            // 计算候补序号
            long waitlistCount = activitySignupService.lambdaQuery()
                    .eq(ActivitySignup::getActivityId, signup.getActivityId())
                    .eq(ActivitySignup::getIsWaitlist, 1)
                    .eq(ActivitySignup::getDeleted, 0)
                    .count();
            signup.setWaitlistOrder((int) waitlistCount + 1);
        }

        boolean saved = activitySignupService.save(signup);
        if (saved) {
            // 更新活动报名人数
            updateActivityParticipantCount(signup.getActivityId());
            return Result.success("报名成功", signup);
        }
        return Result.error("报名失败");
    }

    @PutMapping("/{id}")
    public Result<ActivitySignup> updateSignup(@PathVariable Integer id, @RequestBody ActivitySignup signup) {
        ActivitySignup existing = activitySignupService.getById(id);
        if (existing == null) {
            return Result.error("报名记录不存在");
        }

        signup.setId(id);
        boolean updated = activitySignupService.updateById(signup);
        if (updated) {
            // 更新活动报名人数
            updateActivityParticipantCount(existing.getActivityId());
            return Result.success("更新成功", signup);
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteSignup(@PathVariable Integer id) {
        ActivitySignup signup = activitySignupService.getById(id);
        if (signup == null) {
            return Result.error("报名记录不存在");
        }

        boolean deleted = activitySignupService.removeById(id);
        if (deleted) {
            // 更新活动报名人数
            updateActivityParticipantCount(signup.getActivityId());
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }

    @PostMapping("/{id}/check-in")
    public Result<ActivitySignup> checkInSignup(@PathVariable Integer id, @RequestBody(required = false) Map<String, Object> params) {
        ActivitySignup signup = activitySignupService.getById(id);
        if (signup == null) {
            return Result.error("报名记录不存在");
        }

        // 检查是否已经签到
        if (signup.getStatus() != null && signup.getStatus() == 2) {
            return Result.error("已经签到过了");
        }

        signup.setStatus(2); // 已签到
        signup.setCheckinTime(LocalDateTime.now());

        // 签到方式
        if (params != null && params.get("checkinMethod") != null) {
            signup.setCheckinMethod((Integer) params.get("checkinMethod"));
        } else {
            signup.setCheckinMethod(2); // 默认手动签到
        }

        // 签到操作人
        if (params != null && params.get("checkedInBy") != null) {
            signup.setCheckedInBy((Integer) params.get("checkedInBy"));
        }

        activitySignupService.updateById(signup);

        // 更新活动签到人数
        updateActivityCheckinCount(signup.getActivityId());

        return Result.success("签到成功", signup);
    }

    @PostMapping("/{id}/cancel")
    public Result<ActivitySignup> cancelSignup(@PathVariable Integer id, @RequestBody(required = false) Map<String, String> params) {
        ActivitySignup signup = activitySignupService.getById(id);
        if (signup == null) {
            return Result.error("报名记录不存在");
        }

        signup.setStatus(3); // 已取消
        if (params != null && params.get("cancelReason") != null) {
            signup.setCancelReason(params.get("cancelReason"));
        }

        activitySignupService.updateById(signup);

        // 更新活动报名人数
        updateActivityParticipantCount(signup.getActivityId());

        // 如果有候补人员，将其转为正式报名
        promoteWaitlist(signup.getActivityId());

        return Result.success("取消成功", signup);
    }

    @PostMapping("/{id}/reject")
    public Result<ActivitySignup> rejectSignup(@PathVariable Integer id, @RequestBody(required = false) Map<String, String> params) {
        ActivitySignup signup = activitySignupService.getById(id);
        if (signup == null) {
            return Result.error("报名记录不存在");
        }

        signup.setStatus(4); // 已拒绝
        if (params != null && params.get("rejectReason") != null) {
            signup.setRejectReason(params.get("rejectReason"));
        }

        activitySignupService.updateById(signup);

        // 更新活动报名人数
        updateActivityParticipantCount(signup.getActivityId());

        return Result.success("已拒绝", signup);
    }

    @PostMapping("/{id}/approve")
    public Result<ActivitySignup> approveSignup(@PathVariable Integer id) {
        ActivitySignup signup = activitySignupService.getById(id);
        if (signup == null) {
            return Result.error("报名记录不存在");
        }

        if (signup.getStatus() != 0) { // 不是待审核状态
            return Result.error("该报名不需要审核");
        }

        signup.setStatus(1); // 已报名
        activitySignupService.updateById(signup);

        return Result.success("审核通过", signup);
    }

    @GetMapping("/statistics/{activityId}")
    public Result<Map<String, Object>> getSignupStatistics(@PathVariable Integer activityId) {
        Map<String, Object> stats = new HashMap<>();

        // 总报名人数
        long totalSignups = activitySignupService.lambdaQuery()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getDeleted, 0)
                .count();
        stats.put("totalSignups", totalSignups);

        // 已报名人数
        long confirmedSignups = activitySignupService.lambdaQuery()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getStatus, 1)
                .eq(ActivitySignup::getDeleted, 0)
                .count();
        stats.put("confirmedSignups", confirmedSignups);

        // 已签到人数
        long checkedInCount = activitySignupService.lambdaQuery()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getStatus, 2)
                .eq(ActivitySignup::getDeleted, 0)
                .count();
        stats.put("checkedInCount", checkedInCount);

        // 已取消人数
        long cancelledCount = activitySignupService.lambdaQuery()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getStatus, 3)
                .eq(ActivitySignup::getDeleted, 0)
                .count();
        stats.put("cancelledCount", cancelledCount);

        // 候补人数
        long waitlistCount = activitySignupService.lambdaQuery()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getStatus, 5)
                .eq(ActivitySignup::getDeleted, 0)
                .count();
        stats.put("waitlistCount", waitlistCount);

        // 签到率
        if (confirmedSignups > 0) {
            double checkinRate = (double) checkedInCount / confirmedSignups * 100;
            stats.put("checkinRate", Math.round(checkinRate * 100.0) / 100.0);
        } else {
            stats.put("checkinRate", 0.0);
        }

        return Result.success(stats);
    }

    @GetMapping("/my-activities/{elderlyId}")
    public Result<List<ActivitySignup>> getMyActivities(@PathVariable Integer elderlyId) {
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getElderlyId, elderlyId);
        wrapper.eq(ActivitySignup::getDeleted, 0);
        // 只查询有效的报名记录（待审核、已报名、已签到、候补中），排除已取消和已拒绝的
        wrapper.in(ActivitySignup::getStatus, 0, 1, 2, 5);
        wrapper.orderByDesc(ActivitySignup::getSignupTime);

        List<ActivitySignup> signups = activitySignupService.list(wrapper);
        signups.forEach(this::enrichSignup);

        return Result.success(signups);
    }

    @PostMapping("/batch-check-in")
    public Result<Map<String, Object>> batchCheckIn(@RequestBody Map<String, Object> params) {
        List<Integer> ids = (List<Integer>) params.get("ids");
        Integer checkinMethod = params.get("checkinMethod") != null ? (Integer) params.get("checkinMethod") : 2;
        Integer checkedInBy = (Integer) params.get("checkedInBy");

        int successCount = 0;
        int failCount = 0;

        for (Integer id : ids) {
            ActivitySignup signup = activitySignupService.getById(id);
            if (signup != null && signup.getStatus() != null && signup.getStatus() != 2) {
                signup.setStatus(2);
                signup.setCheckinTime(LocalDateTime.now());
                signup.setCheckinMethod(checkinMethod);
                signup.setCheckedInBy(checkedInBy);
                activitySignupService.updateById(signup);
                successCount++;

                // 更新活动签到人数
                updateActivityCheckinCount(signup.getActivityId());
            } else {
                failCount++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);

        return Result.success("批量签到完成", result);
    }

    private void enrichSignup(ActivitySignup signup) {
        if (signup == null) return;

        // 设置状态文本
        signup.setStatusText(getStatusText(signup.getStatus()));

        // 设置报名来源文本
        signup.setSignupSourceText(getSignupSourceText(signup.getSignupSource()));

        // 设置签到方式文本
        signup.setCheckinMethodText(getCheckinMethodText(signup.getCheckinMethod()));

        // 加载活动信息
        Activity activity = activityService.getById(signup.getActivityId());
        signup.setActivity(activity);

        // 加载老人信息
        User user = userService.getById(signup.getElderlyId());
        signup.setUser(user);

        // 从老人信息中补充报名记录中的空字段
        if (user != null) {
            // 补充联系电话
            if (signup.getElderlyPhone() == null || signup.getElderlyPhone().isEmpty()) {
                signup.setElderlyPhone(user.getPhone());
            }
            // 补充紧急联系人
            if (signup.getEmergencyContactName() == null || signup.getEmergencyContactName().isEmpty()) {
                signup.setEmergencyContactName(user.getEmergencyContact());
            }
            // 补充紧急联系人电话
            if (signup.getEmergencyContactPhone() == null || signup.getEmergencyContactPhone().isEmpty()) {
                signup.setEmergencyContactPhone(user.getEmergencyPhone());
            }
            // 补充健康状况
            if (signup.getHealthStatus() == null || signup.getHealthStatus().isEmpty()) {
                signup.setHealthStatus(user.getHealthStatus());
            }
            // 补充老人姓名
            if (signup.getElderlyName() == null || signup.getElderlyName().isEmpty()) {
                signup.setElderlyName(user.getRealName());
            }

            // 从房间服务中获取房间号（优先使用roomId查询）
            if (user.getRoomId() != null) {
                Room room = roomService.getById(user.getRoomId());
                if (room != null) {
                    signup.setRoomNumber(room.getRoomNumber());
                    // 同时更新老人信息中的房间号
                    user.setRoomNumber(room.getRoomNumber());
                }
            }
            // 如果signup中房间号为空，使用老人信息中的房间号
            if ((signup.getRoomNumber() == null || signup.getRoomNumber().isEmpty()) && user.getRoomNumber() != null) {
                signup.setRoomNumber(user.getRoomNumber());
            }
        }
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待审核";
            case 1: return "已报名";
            case 2: return "已签到";
            case 3: return "已取消";
            case 4: return "已拒绝";
            case 5: return "候补中";
            default: return "未知";
        }
    }

    private String getSignupSourceText(Integer source) {
        if (source == null) return "未知";
        switch (source) {
            case 1: return "本人报名";
            case 2: return "家属代报";
            case 3: return "工作人员代报";
            default: return "未知";
        }
    }

    private String getCheckinMethodText(Integer method) {
        if (method == null) return "未知";
        switch (method) {
            case 1: return "扫码签到";
            case 2: return "手动签到";
            case 3: return "系统自动";
            default: return "未知";
        }
    }

    private void updateActivityParticipantCount(Integer activityId) {
        long count = activitySignupService.lambdaQuery()
                .eq(ActivitySignup::getActivityId, activityId)
                .in(ActivitySignup::getStatus, 1, 2, 5) // 已报名、已签到、候补中
                .eq(ActivitySignup::getDeleted, 0)
                .count();

        Activity activity = new Activity();
        activity.setId(activityId);
        activity.setCurrentParticipants((int) count);
        activityService.updateById(activity);
    }

    private void updateActivityCheckinCount(Integer activityId) {
        long count = activitySignupService.lambdaQuery()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getStatus, 2) // 已签到
                .eq(ActivitySignup::getDeleted, 0)
                .count();

        Activity activity = new Activity();
        activity.setId(activityId);
        activity.setCheckedInCount((int) count);
        activityService.updateById(activity);
    }

    private void promoteWaitlist(Integer activityId) {
        // 查找第一个候补人员
        ActivitySignup waitlistSignup = activitySignupService.lambdaQuery()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getStatus, 5) // 候补中
                .eq(ActivitySignup::getDeleted, 0)
                .orderByAsc(ActivitySignup::getWaitlistOrder)
                .last("LIMIT 1")
                .one();

        if (waitlistSignup != null) {
            waitlistSignup.setStatus(1); // 转为已报名
            waitlistSignup.setIsWaitlist(0);
            waitlistSignup.setWaitlistOrder(0);
            activitySignupService.updateById(waitlistSignup);

            // 更新活动报名人数
            updateActivityParticipantCount(activityId);
        }
    }

    /**
     * 生成活动签到二维码（整个活动的通用二维码）
     */
    @GetMapping("/activity/{activityId}/qrcode")
    public Result<Map<String, Object>> generateActivityQrCode(@PathVariable Integer activityId) {
        // 检查活动是否存在
        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            return Result.error("活动不存在");
        }

        // 生成唯一令牌
        String token = IdUtil.simpleUUID();

        // 创建活动二维码令牌
        QrCodeToken qrToken = new QrCodeToken(activityId, qrCodeExpireMinutes, true);
        qrCodeTokenMap.put(token, qrToken);

        // 生成签名
        String sign = generateActivitySign(activityId, token);

        // 构建二维码内容
        String qrContent = String.format(
            "elderly://activity-checkin?activityId=%d&token=%s&sign=%s",
            activityId, token, sign
        );

        // 生成二维码图片（Base64）
        try {
            QrConfig config = new QrConfig(300, 300);
            config.setMargin(2);
            config.setForeColor(Color.BLACK);
            config.setBackColor(Color.WHITE);

            String base64QrCode = QrCodeUtil.generateAsBase64(qrContent, config, "png");

            // 避免重复添加 data:image/png;base64, 前缀
            String qrCodeUrl;
            if (base64QrCode.startsWith("data:image")) {
                qrCodeUrl = base64QrCode;
            } else {
                qrCodeUrl = "data:image/png;base64," + base64QrCode;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("qrCode", qrCodeUrl);
            result.put("content", qrContent);
            result.put("token", token);
            result.put("expireMinutes", qrCodeExpireMinutes);
            result.put("activityId", activityId);
            result.put("activityTitle", activity.getTitle());
            result.put("isActivityQr", true);

            return Result.success("二维码生成成功", result);
        } catch (Exception e) {
            return Result.error("二维码生成失败: " + e.getMessage());
        }
    }

    /**
     * 生成签到二维码（管理端为报名记录生成二维码）
     */
    @GetMapping("/{id}/qrcode")
    public Result<Map<String, Object>> generateCheckInQrCode(@PathVariable Integer id) {
        ActivitySignup signup = activitySignupService.getById(id);
        if (signup == null) {
            return Result.error("报名记录不存在");
        }

        // 检查是否已签到
        if (signup.getStatus() != null && signup.getStatus() == 2) {
            return Result.error("该用户已签到，无需再次生成二维码");
        }

        // 检查是否是有效的报名状态（待审核0、已报名1、候补中5）
        if (signup.getStatus() == null ||
            (signup.getStatus() != 0 && signup.getStatus() != 1 && signup.getStatus() != 5)) {
            return Result.error("当前报名状态不支持签到");
        }

        // 生成唯一令牌
        String token = IdUtil.simpleUUID();

        // 创建二维码令牌
        QrCodeToken qrToken = new QrCodeToken(
            signup.getId(),
            signup.getActivityId(),
            signup.getElderlyId(),
            qrCodeExpireMinutes
        );
        qrCodeTokenMap.put(token, qrToken);

        // 生成签名
        String sign = generateSign(signup.getId(), signup.getActivityId(), signup.getElderlyId(), token);

        // 构建二维码内容
        String qrContent = String.format(
            "elderly://checkin?signupId=%d&activityId=%d&elderlyId=%d&token=%s&sign=%s",
            signup.getId(), signup.getActivityId(), signup.getElderlyId(), token, sign
        );

        // 生成二维码图片（Base64）
        try {
            QrConfig config = new QrConfig(300, 300);
            config.setMargin(2);
            config.setForeColor(Color.BLACK);
            config.setBackColor(Color.WHITE);

            String base64QrCode = QrCodeUtil.generateAsBase64(qrContent, config, "png");

            // 避免重复添加 data:image/png;base64, 前缀
            String qrCodeUrl;
            if (base64QrCode.startsWith("data:image")) {
                qrCodeUrl = base64QrCode;
            } else {
                qrCodeUrl = "data:image/png;base64," + base64QrCode;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("qrCode", qrCodeUrl);
            result.put("content", qrContent);
            result.put("token", token);
            result.put("expireMinutes", qrCodeExpireMinutes);
            result.put("signupId", signup.getId());
            result.put("activityId", signup.getActivityId());
            result.put("elderlyId", signup.getElderlyId());
            result.put("elderlyName", signup.getElderlyName());

            return Result.success("二维码生成成功", result);
        } catch (Exception e) {
            return Result.error("二维码生成失败: " + e.getMessage());
        }
    }

    /**
     * 活动扫码签到接口（APP端调用，扫描活动二维码）
     */
    @PostMapping("/scan-activity-checkin")
    public Result<ActivitySignup> scanActivityCheckIn(@RequestBody Map<String, Object> params) {
        String token = (String) params.get("token");
        String sign = (String) params.get("sign");
        Integer activityId = params.get("activityId") != null ? Integer.valueOf(params.get("activityId").toString()) : null;
        Integer elderlyId = params.get("elderlyId") != null ? Integer.valueOf(params.get("elderlyId").toString()) : null;
        Integer scannedBy = params.get("scannedBy") != null ? Integer.valueOf(params.get("scannedBy").toString()) : null;

        // 参数校验
        if (token == null || sign == null || activityId == null || elderlyId == null) {
            return Result.error("二维码参数不完整");
        }

        // 验证签名
        String expectedSign = generateActivitySign(activityId, token);
        if (!expectedSign.equals(sign)) {
            return Result.error("二维码签名无效，请重新生成");
        }

        // 验证令牌
        QrCodeToken qrToken = qrCodeTokenMap.get(token);
        if (qrToken == null) {
            return Result.error("二维码已失效，请重新生成");
        }

        if (qrToken.isExpired()) {
            qrCodeTokenMap.remove(token);
            return Result.error("二维码已过期，请重新生成");
        }

        if (!qrToken.isActivityQr || !qrToken.activityId.equals(activityId)) {
            return Result.error("二维码数据不匹配");
        }

        // 检查活动是否存在
        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            return Result.error("活动不存在");
        }

        // 检查住客是否存在
        User user = userService.getById(elderlyId);
        if (user == null) {
            return Result.error("住客信息不存在");
        }

        // 查找该住客在此活动的报名记录
        ActivitySignup signup = activitySignupService.lambdaQuery()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getElderlyId, elderlyId)
                .eq(ActivitySignup::getDeleted, 0)
                .in(ActivitySignup::getStatus, 0, 1, 5) // 待审核、已报名、候补中
                .one();

        if (signup == null) {
            return Result.error("该住客未报名此活动");
        }

        // 检查是否已经签到
        if (signup.getStatus() != null && signup.getStatus() == 2) {
            return Result.error("已经签到过了");
        }

        // 执行签到
        signup.setStatus(2); // 已签到
        signup.setCheckinTime(LocalDateTime.now());
        signup.setCheckinMethod(1); // 扫码签到
        if (scannedBy != null) {
            signup.setCheckedInBy(scannedBy);
        }

        boolean updated = activitySignupService.updateById(signup);
        if (updated) {
            // 更新活动签到人数
            updateActivityCheckinCount(activityId);

            // 丰富返回数据
            enrichSignup(signup);
            return Result.success("签到成功", signup);
        }

        return Result.error("签到失败，请重试");
    }

    /**
     * 扫码签到接口（APP端调用）
     */
    @PostMapping("/scan-checkin")
    public Result<ActivitySignup> scanCheckIn(@RequestBody Map<String, Object> params) {
        String token = (String) params.get("token");
        String sign = (String) params.get("sign");
        Integer signupId = params.get("signupId") != null ? Integer.valueOf(params.get("signupId").toString()) : null;
        Integer activityId = params.get("activityId") != null ? Integer.valueOf(params.get("activityId").toString()) : null;
        Integer elderlyId = params.get("elderlyId") != null ? Integer.valueOf(params.get("elderlyId").toString()) : null;
        Integer scannedBy = params.get("scannedBy") != null ? Integer.valueOf(params.get("scannedBy").toString()) : null;

        // 参数校验
        if (token == null || sign == null || signupId == null || activityId == null || elderlyId == null) {
            return Result.error("二维码参数不完整");
        }

        // 验证签名
        String expectedSign = generateSign(signupId, activityId, elderlyId, token);
        if (!expectedSign.equals(sign)) {
            return Result.error("二维码签名无效，请重新生成");
        }

        // 验证令牌
        QrCodeToken qrToken = qrCodeTokenMap.get(token);
        if (qrToken == null) {
            return Result.error("二维码已失效，请重新生成");
        }

        if (qrToken.isExpired()) {
            qrCodeTokenMap.remove(token);
            return Result.error("二维码已过期，请重新生成");
        }

        if (qrToken.used) {
            return Result.error("二维码已被使用，请重新生成");
        }

        // 验证令牌数据是否匹配
        if (!qrToken.signupId.equals(signupId) ||
            !qrToken.activityId.equals(activityId) ||
            !qrToken.elderlyId.equals(elderlyId)) {
            return Result.error("二维码数据不匹配");
        }

        // 获取报名记录
        ActivitySignup signup = activitySignupService.getById(signupId);
        if (signup == null) {
            return Result.error("报名记录不存在");
        }

        // 检查是否已经签到
        if (signup.getStatus() != null && signup.getStatus() == 2) {
            return Result.error("已经签到过了");
        }

        // 执行签到
        signup.setStatus(2); // 已签到
        signup.setCheckinTime(LocalDateTime.now());
        signup.setCheckinMethod(1); // 扫码签到
        if (scannedBy != null) {
            signup.setCheckedInBy(scannedBy);
        }

        boolean updated = activitySignupService.updateById(signup);
        if (updated) {
            // 标记令牌已使用
            qrToken.used = true;
            qrCodeTokenMap.remove(token);

            // 更新活动签到人数
            updateActivityCheckinCount(activityId);

            // 丰富返回数据
            enrichSignup(signup);
            return Result.success("签到成功", signup);
        }

        return Result.error("签到失败，请重试");
    }

    /**
     * 验证活动二维码有效性（APP端扫码前预览用）
     */
    @PostMapping("/verify-activity-qrcode")
    public Result<Map<String, Object>> verifyActivityQrCode(@RequestBody Map<String, Object> params) {
        String token = (String) params.get("token");
        String sign = (String) params.get("sign");
        Integer activityId = params.get("activityId") != null ? Integer.valueOf(params.get("activityId").toString()) : null;

        if (token == null || sign == null || activityId == null) {
            return Result.error("二维码参数不完整");
        }

        // 验证签名
        String expectedSign = generateActivitySign(activityId, token);
        if (!expectedSign.equals(sign)) {
            return Result.error("二维码签名无效");
        }

        // 验证令牌
        QrCodeToken qrToken = qrCodeTokenMap.get(token);
        if (qrToken == null) {
            return Result.error("二维码已失效");
        }

        if (qrToken.isExpired()) {
            return Result.error("二维码已过期");
        }

        if (!qrToken.isActivityQr || !qrToken.activityId.equals(activityId)) {
            return Result.error("二维码数据不匹配");
        }

        // 获取活动信息
        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            return Result.error("活动不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("valid", true);
        result.put("isActivityQr", true);
        result.put("activityId", activityId);
        result.put("activityTitle", activity.getTitle());
        result.put("activityLocation", activity.getLocation());
        result.put("activityTime", formatActivityTime(activity));
        result.put("expireTime", qrToken.expireTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return Result.success("二维码有效", result);
    }

    /**
     * 验证二维码有效性（APP端扫码前预览用）
     */
    @PostMapping("/verify-qrcode")
    public Result<Map<String, Object>> verifyQrCode(@RequestBody Map<String, Object> params) {
        String token = (String) params.get("token");
        String sign = (String) params.get("sign");
        Integer signupId = params.get("signupId") != null ? Integer.valueOf(params.get("signupId").toString()) : null;
        Integer activityId = params.get("activityId") != null ? Integer.valueOf(params.get("activityId").toString()) : null;
        Integer elderlyId = params.get("elderlyId") != null ? Integer.valueOf(params.get("elderlyId").toString()) : null;

        if (token == null || sign == null || signupId == null || activityId == null || elderlyId == null) {
            return Result.error("二维码参数不完整");
        }

        // 验证签名
        String expectedSign = generateSign(signupId, activityId, elderlyId, token);
        if (!expectedSign.equals(sign)) {
            return Result.error("二维码签名无效");
        }

        // 验证令牌
        QrCodeToken qrToken = qrCodeTokenMap.get(token);
        if (qrToken == null) {
            return Result.error("二维码已失效");
        }

        if (qrToken.isExpired()) {
            return Result.error("二维码已过期");
        }

        if (qrToken.used) {
            return Result.error("二维码已被使用");
        }

        // 获取报名和活动信息
        ActivitySignup signup = activitySignupService.getById(signupId);
        Activity activity = activityService.getById(activityId);

        if (signup == null || activity == null) {
            return Result.error("活动或报名记录不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("valid", true);
        result.put("activityTitle", activity.getTitle());
        result.put("activityLocation", activity.getLocation());
        result.put("activityTime", formatActivityTime(activity));
        result.put("elderlyName", signup.getElderlyName());
        result.put("roomNumber", signup.getRoomNumber());
        result.put("signupStatus", signup.getStatus());
        result.put("expireTime", qrToken.expireTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return Result.success("二维码有效", result);
    }

    /**
     * 生成签名
     */
    private String generateSign(Integer signupId, Integer activityId, Integer elderlyId, String token) {
        String data = String.format("signupId=%d&activityId=%d&elderlyId=%d&token=%s&secret=%s",
            signupId, activityId, elderlyId, token, qrCodeSecret);
        return DigestUtil.md5Hex(data).substring(0, 16);
    }

    /**
     * 生成活动二维码签名
     */
    private String generateActivitySign(Integer activityId, String token) {
        String data = String.format("activityId=%d&token=%s&secret=%s",
            activityId, token, qrCodeSecret);
        return DigestUtil.md5Hex(data).substring(0, 16);
    }

    /**
     * 格式化活动时间
     */
    private String formatActivityTime(Activity activity) {
        if (activity.getActivityDate() == null) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(activity.getActivityDate());
        if (activity.getStartTime() != null) {
            sb.append(" ").append(activity.getStartTime());
            if (activity.getEndTime() != null) {
                sb.append(" - ").append(activity.getEndTime());
            }
        }
        return sb.toString();
    }

    /**
     * 清理过期的二维码令牌（可以配合定时任务使用）
     */
    @PostConstruct
    public void cleanExpiredTokens() {
        // 启动时清理过期的令牌
        qrCodeTokenMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}
