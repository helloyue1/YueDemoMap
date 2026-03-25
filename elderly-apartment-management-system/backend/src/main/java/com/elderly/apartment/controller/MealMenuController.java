package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.MealMenu;
import com.elderly.apartment.service.MealMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meal/menu")
public class MealMenuController {

    @Autowired
    private MealMenuService mealMenuService;

    @GetMapping("/list")
    public Result<Page<MealMenu>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer type) {
        LambdaQueryWrapper<MealMenu> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(MealMenu::getName, name);
        }
        if (type != null) {
            wrapper.and(w -> w.eq(MealMenu::getType, type).or().eq(MealMenu::getType, 4));
        }
        wrapper.orderByDesc(MealMenu::getCreateTime);
        Page<MealMenu> result = mealMenuService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<List<MealMenu>> all(@RequestParam(required = false) Integer type) {
        LambdaQueryWrapper<MealMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MealMenu::getStatus, 1);
        if (type != null) {
            wrapper.and(w -> w.eq(MealMenu::getType, type).or().eq(MealMenu::getType, 4));
        }
        wrapper.orderByAsc(MealMenu::getType, MealMenu::getName);
        return Result.success(mealMenuService.list(wrapper));
    }

    @GetMapping("/{id}")
    public Result<MealMenu> getById(@PathVariable Long id) {
        return Result.success(mealMenuService.getById(id));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody MealMenu mealMenu) {
        return Result.success(mealMenuService.save(mealMenu));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody MealMenu mealMenu) {
        mealMenu.setId(id);
        return Result.success(mealMenuService.updateById(mealMenu));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(mealMenuService.removeById(id));
    }
}
