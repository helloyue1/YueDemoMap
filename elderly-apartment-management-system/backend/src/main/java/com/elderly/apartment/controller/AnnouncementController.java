package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.Announcement;
import com.elderly.apartment.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = announcementService.getStatistics();
        // 处理 null 值
        stats.put("total", stats.get("total") != null ? ((Number) stats.get("total")).longValue() : 0);
        stats.put("published", stats.get("published") != null ? ((Number) stats.get("published")).longValue() : 0);
        stats.put("draft", stats.get("draft") != null ? ((Number) stats.get("draft")).longValue() : 0);
        stats.put("top", stats.get("top") != null ? ((Number) stats.get("top")).longValue() : 0);
        return Result.success(stats);
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> getAnnouncementList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {

        Page<Announcement> pageParam = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();

        if (title != null && !title.isEmpty()) {
            wrapper.like(Announcement::getTitle, title);
        }

        if (type != null) {
            wrapper.eq(Announcement::getType, type);
        }

        if (status != null) {
            wrapper.eq(Announcement::getStatus, status);
        }

        wrapper.eq(Announcement::getDeleted, 0);
        // 置顶的公告排在前面，然后按发布时间倒序
        wrapper.orderByDesc(Announcement::getTop);
        wrapper.orderByDesc(Announcement::getPublishTime);
        wrapper.orderByDesc(Announcement::getCreateTime);

        Page<Announcement> result = announcementService.page(pageParam, wrapper);

        // 丰富数据
        result.getRecords().forEach(this::enrichAnnouncement);

        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());

        return Result.success(data);
    }

    @GetMapping("/detail/{id}")
    public Result<Announcement> getAnnouncementById(@PathVariable Integer id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return Result.error("公告不存在");
        }
        enrichAnnouncement(announcement);
        return Result.success(announcement);
    }

    @PostMapping("/create")
    public Result<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        // 设置默认值
        if (announcement.getViewCount() == null) {
            announcement.setViewCount(0);
        }
        if (announcement.getTop() == null) {
            announcement.setTop(0);
        }
        if (announcement.getStatus() == null) {
            announcement.setStatus(1);
        }

        // 如果状态为已发布，设置发布时间
        if (announcement.getStatus() == 1 && announcement.getPublishTime() == null) {
            announcement.setPublishTime(LocalDateTime.now());
        }

        // 设置发布人信息（这里简化处理，实际应该从当前登录用户获取）
        if (announcement.getPublisherName() == null) {
            announcement.setPublisherName("管理员");
        }

        boolean saved = announcementService.save(announcement);
        if (saved) {
            return Result.success("公告创建成功", announcement);
        }
        return Result.error("公告创建失败");
    }

    @PutMapping("/update/{id}")
    public Result<Announcement> updateAnnouncement(@PathVariable Integer id, @RequestBody Announcement announcement) {
        Announcement existing = announcementService.getById(id);
        if (existing == null) {
            return Result.error("公告不存在");
        }

        announcement.setId(id);

        // 如果状态从草稿变为已发布，设置发布时间
        if (existing.getStatus() == 0 && announcement.getStatus() != null && announcement.getStatus() == 1) {
            announcement.setPublishTime(LocalDateTime.now());
        }

        boolean updated = announcementService.updateById(announcement);
        if (updated) {
            return Result.success("公告更新成功", announcement);
        }
        return Result.error("公告更新失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Integer id) {
        boolean deleted = announcementService.removeById(id);
        if (deleted) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }

    @PutMapping("/publish/{id}")
    public Result<Announcement> publishAnnouncement(@PathVariable Integer id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return Result.error("公告不存在");
        }

        announcement.setStatus(1);
        announcement.setPublishTime(LocalDateTime.now());
        announcementService.updateById(announcement);

        return Result.success("公告发布成功", announcement);
    }

    @PutMapping("/toggle-top/{id}")
    public Result<Announcement> toggleTop(@PathVariable Integer id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return Result.error("公告不存在");
        }

        // 切换置顶状态
        announcement.setTop(announcement.getTop() == 1 ? 0 : 1);
        announcementService.updateById(announcement);

        return Result.success(announcement.getTop() == 1 ? "置顶成功" : "已取消置顶", announcement);
    }

    @PostMapping("/view/{id}")
    public Result<Void> increaseViewCount(@PathVariable Integer id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement != null) {
            announcement.setViewCount(announcement.getViewCount() + 1);
            announcementService.updateById(announcement);
        }
        return Result.success(null);
    }

    private void enrichAnnouncement(Announcement announcement) {
        if (announcement == null) return;

        // 设置类型文本
        announcement.setTypeText(getTypeText(announcement.getType()));

        // 设置状态文本
        announcement.setStatusText(getStatusText(announcement.getStatus()));
    }

    private String getTypeText(Integer type) {
        if (type == null) return "普通";
        switch (type) {
            case 1: return "普通";
            case 2: return "重要";
            case 3: return "紧急";
            default: return "普通";
        }
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "草稿";
            case 1: return "已发布";
            case 2: return "已下架";
            default: return "未知";
        }
    }
}
