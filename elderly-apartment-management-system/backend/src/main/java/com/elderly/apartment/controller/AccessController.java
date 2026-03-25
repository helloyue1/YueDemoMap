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
@RequestMapping("/access")
public class AccessController {

    @Autowired
    private UserService userService;

    @GetMapping("/record")
    public Result<Page<User>> getAccessRecordList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String visitorName,
            @RequestParam(required = false) String elderlyName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 只查询老人用户
        wrapper.eq(User::getUserType, 2);

        if (elderlyName != null && !elderlyName.isEmpty()) {
            wrapper.like(User::getRealName, elderlyName);
        }

        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(User::getCreateTime, startDate);
        }

        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(User::getCreateTime, endDate);
        }

        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userService.page(pageParam, wrapper);
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<List<User>> getAccessList() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, 2);
        wrapper.eq(User::getStatus, 1);
        wrapper.orderByAsc(User::getRealName);
        List<User> result = userService.list(wrapper);
        return Result.success(result);
    }

    @GetMapping("/visit")
    public Result<List<User>> getAccessVisit() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, 2);
        wrapper.eq(User::getStatus, 1);
        wrapper.orderByAsc(User::getRealName);
        List<User> result = userService.list(wrapper);
        return Result.success(result);
    }
}
