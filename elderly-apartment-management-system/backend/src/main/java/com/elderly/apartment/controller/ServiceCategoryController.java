package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.ServiceCategory;
import com.elderly.apartment.service.ServiceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service-categories")
public class ServiceCategoryController {

    @Autowired
    private ServiceCategoryService serviceCategoryService;

    @GetMapping
    public Result<List<ServiceCategory>> getAllCategories() {
        LambdaQueryWrapper<ServiceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceCategory::getDeleted, 0);
        wrapper.orderByAsc(ServiceCategory::getSortOrder);
        List<ServiceCategory> list = serviceCategoryService.list(wrapper);
        return Result.success(list);
    }

    @GetMapping("/active")
    public Result<List<ServiceCategory>> getActiveCategories() {
        List<ServiceCategory> list = serviceCategoryService.getActiveCategories();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<ServiceCategory> getCategoryById(@PathVariable Long id) {
        ServiceCategory category = serviceCategoryService.getById(id);
        if (category == null || category.getDeleted() == 1) {
            return Result.error("分类不存在");
        }
        return Result.success(category);
    }

    @PostMapping
    public Result<ServiceCategory> createCategory(@RequestBody ServiceCategory category) {
        boolean success = serviceCategoryService.save(category);
        if (success) {
            return Result.success(category);
        }
        return Result.error("创建失败");
    }

    @PutMapping("/{id}")
    public Result<ServiceCategory> updateCategory(@PathVariable Long id, @RequestBody ServiceCategory category) {
        category.setId(id);
        boolean success = serviceCategoryService.updateById(category);
        if (success) {
            return Result.success(category);
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteCategory(@PathVariable Long id) {
        boolean success = serviceCategoryService.removeById(id);
        if (success) {
            return Result.success(true);
        }
        return Result.error("删除失败");
    }
}
