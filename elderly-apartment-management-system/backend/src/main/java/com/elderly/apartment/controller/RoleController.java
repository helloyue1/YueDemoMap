package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.Role;
import com.elderly.apartment.entity.UserRole;
import com.elderly.apartment.mapper.RoleMapper;
import com.elderly.apartment.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @GetMapping
    public Result<Page<Role>> getRoleList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code) {
        Page<Role> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            wrapper.like(Role::getName, name);
        }
        
        if (code != null && !code.isEmpty()) {
            wrapper.like(Role::getCode, code);
        }
        
        wrapper.orderByDesc(Role::getCreateTime);
        Page<Role> result = roleMapper.selectPage(pageParam, wrapper);
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<List<Role>> getAllRoles() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Role::getCreateTime);
        List<Role> roles = roleMapper.selectList(wrapper);
        return Result.success(roles);
    }

    @GetMapping("/{id}")
    public Result<Role> getRoleById(@PathVariable Integer id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    @PostMapping
    public Result<Role> createRole(@RequestBody Role role) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getCode, role.getCode());
        if (roleMapper.selectCount(wrapper) > 0) {
            return Result.error("角色编码已存在");
        }
        
        roleMapper.insert(role);
        return Result.success(role);
    }

    @PutMapping("/{id}")
    public Result<Role> updateRole(@PathVariable Integer id, @RequestBody Role role) {
        Role existing = roleMapper.selectById(id);
        if (existing == null) {
            return Result.error("角色不存在");
        }
        
        if (!existing.getCode().equals(role.getCode())) {
            LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Role::getCode, role.getCode());
            if (roleMapper.selectCount(wrapper) > 0) {
                return Result.error("角色编码已存在");
            }
        }
        
        role.setId(id);
        roleMapper.updateById(role);
        return Result.success(role);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Integer id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        
        roleMapper.deleteById(id);
        return Result.success(null);
    }

    @GetMapping("/user/{userId}")
    public Result<List<Role>> getRolesByUserId(@PathVariable Integer userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);
        
        if (userRoles.isEmpty()) {
            return Result.success(List.of());
        }
        
        List<Integer> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        return Result.success(roles);
    }

    @PostMapping("/user/{userId}/assign")
    public Result<Void> assignRolesToUser(@PathVariable Integer userId, @RequestBody List<Integer> roleIds) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);
        
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoles = roleIds.stream()
                    .map(roleId -> {
                        UserRole ur = new UserRole();
                        ur.setUserId(userId);
                        ur.setRoleId(roleId);
                        return ur;
                    })
                    .collect(Collectors.toList());
            
            for (UserRole ur : userRoles) {
                userRoleMapper.insert(ur);
            }
        }
        
        return Result.success(null);
    }
}
