package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meal")
public class MealController {

    @Autowired
    private UserService userService;

    @GetMapping("/plan")
    public Result<List<User>> getMealPlan() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, 2);
        wrapper.eq(User::getStatus, 1);
        wrapper.orderByAsc(User::getRealName);
        List<User> result = userService.list(wrapper);
        return Result.success(result);
    }

    @GetMapping("/arrange")
    public Result<List<User>> getMealArrange() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, 2);
        wrapper.eq(User::getStatus, 1);
        wrapper.orderByAsc(User::getRealName);
        List<User> result = userService.list(wrapper);
        return Result.success(result);
    }

    @GetMapping("/menu")
    public Result<List<User>> getMealMenu() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, 2);
        wrapper.eq(User::getStatus, 1);
        wrapper.orderByAsc(User::getRealName);
        List<User> result = userService.list(wrapper);
        return Result.success(result);
    }

    @GetMapping("/order")
    public Result<List<User>> getMealOrder() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, 2);
        wrapper.eq(User::getStatus, 1);
        wrapper.orderByAsc(User::getRealName);
        List<User> result = userService.list(wrapper);
        return Result.success(result);
    }

    @GetMapping("/distribution")
    public Result<List<User>> getMealDistribution() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, 2);
        wrapper.eq(User::getStatus, 1);
        wrapper.orderByAsc(User::getRealName);
        List<User> result = userService.list(wrapper);
        return Result.success(result);
    }
}
