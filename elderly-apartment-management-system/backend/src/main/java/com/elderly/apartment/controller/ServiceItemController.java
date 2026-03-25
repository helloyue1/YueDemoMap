package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.ServiceItem;
import com.elderly.apartment.service.ServiceItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service-items")
public class ServiceItemController {

    @Autowired
    private ServiceItemService serviceItemService;

    @GetMapping
    public Result<Page<ServiceItem>> getItemList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {

        Page<ServiceItem> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();

        if (categoryId != null) {
            wrapper.eq(ServiceItem::getCategoryId, categoryId);
        }

        if (name != null && !name.isEmpty()) {
            wrapper.like(ServiceItem::getName, name);
        }

        if (status != null) {
            wrapper.eq(ServiceItem::getStatus, status);
        }

        wrapper.eq(ServiceItem::getDeleted, 0);
        wrapper.orderByAsc(ServiceItem::getCategoryId);
        wrapper.orderByAsc(ServiceItem::getSortOrder);

        Page<ServiceItem> result = serviceItemService.page(pageParam, wrapper);
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<List<ServiceItem>> getAllItems() {
        List<ServiceItem> list = serviceItemService.getAllActiveItems();
        return Result.success(list);
    }

    @GetMapping("/category/{categoryId}")
    public Result<List<ServiceItem>> getItemsByCategory(@PathVariable Long categoryId) {
        List<ServiceItem> list = serviceItemService.getItemsByCategoryId(categoryId);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<ServiceItem> getItemById(@PathVariable Long id) {
        ServiceItem item = serviceItemService.getItemWithCategory(id);
        if (item == null || item.getDeleted() == 1) {
            return Result.error("服务项不存在");
        }
        return Result.success(item);
    }

    @PostMapping
    public Result<ServiceItem> createItem(@RequestBody ServiceItem item) {
        boolean success = serviceItemService.save(item);
        if (success) {
            return Result.success(item);
        }
        return Result.error("创建失败");
    }

    @PutMapping("/{id}")
    public Result<ServiceItem> updateItem(@PathVariable Long id, @RequestBody ServiceItem item) {
        item.setId(id);
        boolean success = serviceItemService.updateById(item);
        if (success) {
            return Result.success(item);
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteItem(@PathVariable Long id) {
        boolean success = serviceItemService.removeById(id);
        if (success) {
            return Result.success(true);
        }
        return Result.error("删除失败");
    }
}
