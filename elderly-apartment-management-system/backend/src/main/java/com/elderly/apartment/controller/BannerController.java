package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.Banner;
import com.elderly.apartment.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/banners")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping("/{id}")
    public Result<Banner> getBannerById(@PathVariable Long id) {
        Banner banner = bannerService.getById(id);
        if (banner == null) {
            return Result.error("轮播图不存在");
        }
        enrichBanner(banner);
        return Result.success(banner);
    }

    @GetMapping
    public Result<Page<Banner>> getBannerList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String position) {

        Page<Banner> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();

        if (title != null && !title.isEmpty()) {
            wrapper.like(Banner::getTitle, title);
        }

        if (status != null) {
            wrapper.eq(Banner::getStatus, status);
        }

        if (position != null && !position.isEmpty()) {
            wrapper.eq(Banner::getPosition, position);
        }

        wrapper.orderByAsc(Banner::getSortOrder);
        wrapper.orderByDesc(Banner::getCreateTime);

        Page<Banner> result = bannerService.page(pageParam, wrapper);
        result.getRecords().forEach(this::enrichBanner);

        return Result.success(result);
    }

    @GetMapping("/active")
    public Result<List<Banner>> getActiveBanners(
            @RequestParam(defaultValue = "home") String position) {

        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getStatus, 1);
        wrapper.eq(Banner::getPosition, position);
        wrapper.orderByAsc(Banner::getSortOrder);

        List<Banner> banners = bannerService.list(wrapper);
        banners.forEach(this::enrichBanner);

        return Result.success(banners);
    }

    @PostMapping
    public Result<Banner> createBanner(@RequestBody Banner banner) {
        if (banner.getSortOrder() == null) {
            banner.setSortOrder(0);
        }
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        if (banner.getPosition() == null) {
            banner.setPosition("home");
        }

        boolean saved = bannerService.save(banner);
        if (saved) {
            return Result.success("轮播图创建成功", banner);
        }
        return Result.error("轮播图创建失败");
    }

    @PutMapping("/{id}")
    public Result<Banner> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        Banner existing = bannerService.getById(id);
        if (existing == null) {
            return Result.error("轮播图不存在");
        }

        banner.setId(id);
        boolean updated = bannerService.updateById(banner);
        if (updated) {
            return Result.success("轮播图更新成功", banner);
        }
        return Result.error("轮播图更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBanner(@PathVariable Long id) {
        boolean deleted = bannerService.removeById(id);
        if (deleted) {
            return Result.success("轮播图删除成功", null);
        }
        return Result.error("轮播图删除失败");
    }

    @PutMapping("/{id}/status")
    public Result<Banner> updateBannerStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Banner banner = bannerService.getById(id);
        if (banner == null) {
            return Result.error("轮播图不存在");
        }

        Integer status = params.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态值无效，有效值：0-禁用，1-启用");
        }

        banner.setStatus(status);
        boolean updated = bannerService.updateById(banner);
        if (updated) {
            String statusText = status == 1 ? "启用" : "禁用";
            return Result.success("轮播图已" + statusText, banner);
        }
        return Result.error("状态更新失败");
    }

    @PutMapping("/{id}/sort")
    public Result<Banner> updateBannerSort(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Banner banner = bannerService.getById(id);
        if (banner == null) {
            return Result.error("轮播图不存在");
        }

        Integer sortOrder = params.get("sortOrder");
        if (sortOrder == null) {
            return Result.error("排序值不能为空");
        }

        banner.setSortOrder(sortOrder);
        boolean updated = bannerService.updateById(banner);
        if (updated) {
            return Result.success("排序更新成功", banner);
        }
        return Result.error("排序更新失败");
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 总轮播图数
        long total = bannerService.count();
        stats.put("total", total);

        // 启用中的轮播图数
        long active = bannerService.lambdaQuery()
                .eq(Banner::getStatus, 1)
                .count();
        stats.put("active", active);

        // 禁用的轮播图数
        long inactive = bannerService.lambdaQuery()
                .eq(Banner::getStatus, 0)
                .count();
        stats.put("inactive", inactive);

        return Result.success(stats);
    }

    private void enrichBanner(Banner banner) {
        if (banner == null) return;

        // 设置状态文本
        banner.setStatusText(getStatusText(banner.getStatus()));
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "禁用";
            case 1: return "启用";
            default: return "未知";
        }
    }
}
