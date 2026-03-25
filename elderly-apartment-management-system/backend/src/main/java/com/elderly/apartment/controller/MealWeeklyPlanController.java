package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.MealWeeklyPlan;
import com.elderly.apartment.service.MealWeeklyPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/meal/plan")
public class MealWeeklyPlanController {

    @Autowired
    private MealWeeklyPlanService mealWeeklyPlanService;

    @GetMapping("/list")
    public Result<List<MealWeeklyPlan>> list(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LambdaQueryWrapper<MealWeeklyPlan> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(MealWeeklyPlan::getPlanDate, LocalDate.parse(startDate));
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(MealWeeklyPlan::getPlanDate, LocalDate.parse(endDate));
        }
        wrapper.orderByAsc(MealWeeklyPlan::getPlanDate, MealWeeklyPlan::getMealType);
        return Result.success(mealWeeklyPlanService.list(wrapper));
    }

    @GetMapping("/date")
    public Result<List<MealWeeklyPlan>> getByDate(@RequestParam String date) {
        LambdaQueryWrapper<MealWeeklyPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MealWeeklyPlan::getPlanDate, LocalDate.parse(date));
        wrapper.orderByAsc(MealWeeklyPlan::getMealType);
        return Result.success(mealWeeklyPlanService.list(wrapper));
    }

    @GetMapping("/{id}")
    public Result<MealWeeklyPlan> getById(@PathVariable Long id) {
        return Result.success(mealWeeklyPlanService.getById(id));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody MealWeeklyPlan plan) {
        return Result.success(mealWeeklyPlanService.save(plan));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody MealWeeklyPlan plan) {
        plan.setId(id);
        return Result.success(mealWeeklyPlanService.updateById(plan));
    }

    @PutMapping("/{id}/publish")
    public Result<Boolean> publish(@PathVariable Long id) {
        MealWeeklyPlan plan = new MealWeeklyPlan();
        plan.setId(id);
        plan.setStatus(1);
        return Result.success(mealWeeklyPlanService.updateById(plan));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(mealWeeklyPlanService.removeById(id));
    }
}
