package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.Role;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.entity.UserRole;
import com.elderly.apartment.mapper.RoleMapper;
import com.elderly.apartment.mapper.UserMapper;
import com.elderly.apartment.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/nurse")
public class NurseController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * 获取可用护理员列表（包含护士和护理员角色）
     */
    @GetMapping
    public Result<List<Map<String, Object>>> getNurseList(
            @RequestParam(required = false) String name) {
        
        // 获取 NURSE 和 CAREGIVER 角色的ID
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(Role::getCode, "NURSE", "CAREGIVER");
        List<Role> roles = roleMapper.selectList(roleWrapper);
        
        if (roles.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        
        List<Integer> roleIds = roles.stream()
                .map(Role::getId)
                .collect(Collectors.toList());
        
        // 获取具有这些角色的用户ID
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.in(UserRole::getRoleId, roleIds);
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        
        if (userRoles.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        
        List<Integer> userIds = userRoles.stream()
                .map(UserRole::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        // 查询这些用户的信息
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(User::getId, userIds);
        userWrapper.eq(User::getStatus, 1); // 只查询状态正常的用户
        
        // 如果传入了姓名，进行模糊查询
        if (name != null && !name.isEmpty()) {
            userWrapper.and(w -> w.like(User::getRealName, name)
                    .or()
                    .like(User::getUsername, name));
        }
        
        List<User> users = userMapper.selectList(userWrapper);
        
        // 构建返回结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (User user : users) {
            Map<String, Object> nurse = new HashMap<>();
            nurse.put("id", user.getId());
            nurse.put("name", user.getRealName() != null ? user.getRealName() : user.getUsername());
            nurse.put("username", user.getUsername());
            nurse.put("phone", user.getPhone());
            nurse.put("email", user.getEmail());
            nurse.put("status", user.getStatus() == 1 ? "available" : "busy");
            nurse.put("createTime", user.getCreateTime());
            
            // 获取用户的角色信息
            List<String> userRoleCodes = getUserRoleCodes(user.getId());
            nurse.put("roles", userRoleCodes);
            
            // 模拟今日任务数据（后续可以从实际的任务表中查询）
            nurse.put("todayTasks", (int) (Math.random() * 8));
            nurse.put("maxTasks", 8);
            
            result.add(nurse);
        }
        
        return Result.success(result);
    }

    /**
     * 获取用户的角色代码列表
     */
    private List<String> getUserRoleCodes(Integer userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);
        
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Integer> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(Role::getId, roleIds);
        List<Role> roles = roleMapper.selectList(roleWrapper);
        
        return roles.stream()
                .map(Role::getCode)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getNurseById(@PathVariable Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("护理员不存在");
        }
        
        Map<String, Object> nurse = new HashMap<>();
        nurse.put("id", user.getId());
        nurse.put("name", user.getRealName() != null ? user.getRealName() : user.getUsername());
        nurse.put("username", user.getUsername());
        nurse.put("phone", user.getPhone());
        nurse.put("email", user.getEmail());
        nurse.put("status", user.getStatus() == 1 ? "available" : "busy");
        nurse.put("createTime", user.getCreateTime());
        
        // 获取用户的角色信息
        List<String> userRoleCodes = getUserRoleCodes(user.getId());
        nurse.put("roles", userRoleCodes);
        
        // 模拟今日任务数据
        nurse.put("todayTasks", (int) (Math.random() * 8));
        nurse.put("maxTasks", 8);
        
        return Result.success(nurse);
    }
}
