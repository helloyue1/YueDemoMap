package com.elderly.apartment.config;

import com.elderly.apartment.entity.Role;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.mapper.RoleMapper;
import com.elderly.apartment.mapper.UserMapper;
import com.elderly.apartment.mapper.UserRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 数据初始化器
 * 用于初始化角色、老人用户账号等基础数据
 * 注意：elderly和elderly_user表已合并到user表
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("开始初始化基础数据...");
        initRoles();
        initElderlyUsers();
        log.info("基础数据初始化完成");
    }

    /**
     * 初始化角色数据
     */
    private void initRoles() {
        // 检查老人角色是否存在
        Role elderlyRole = roleMapper.selectById(7);
        if (elderlyRole == null) {
            elderlyRole = new Role();
            elderlyRole.setId(7);
            elderlyRole.setName("老人");
            elderlyRole.setCode("ELDERLY");
            elderlyRole.setDescription("养老院入住老人角色，只能查看自己的数据");
            elderlyRole.setCreateTime(LocalDateTime.now());
            elderlyRole.setUpdateTime(LocalDateTime.now());
            roleMapper.insert(elderlyRole);
            log.info("创建老人角色成功");
        } else {
            // 检查角色code是否正确，如果不正确则更新
            if (!"ELDERLY".equals(elderlyRole.getCode())) {
                elderlyRole.setCode("ELDERLY");
                elderlyRole.setUpdateTime(LocalDateTime.now());
                roleMapper.updateById(elderlyRole);
                log.info("更新老人角色code为ELDERLY");
            } else {
                log.info("老人角色已存在且code正确");
            }
        }
    }

    /**
     * 初始化老人用户账号
     * 注意：数据直接存入user表，user_type=2表示老人
     */
    private void initElderlyUsers() {
        // 定义老人用户数据
        ElderlyUserData[] elderlyUsers = {
            new ElderlyUserData(101, "zhangdaye_201", "张大爷", "13800138001", "zhangdaye@example.com", "201", "身份证后6位：51234"),
            new ElderlyUserData(102, "linainai_202", "李奶奶", "13800138002", "linainai@example.com", "202", "身份证后6位：21234"),
            new ElderlyUserData(103, "wangyeye_203", "王爷爷", "13800138003", "wangyeye@example.com", "203", "身份证后6位：81234"),
            new ElderlyUserData(104, "zhaonainai_204", "赵奶奶", "13800138004", "zhaonainai@example.com", "204", "身份证后6位：51234"),
            new ElderlyUserData(105, "liudaye_205", "刘大爷", "13800138005", "liudaye@example.com", "205", "身份证后6位：91234")
        };

        String defaultPassword = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO"; // BCrypt加密后的密码

        for (ElderlyUserData data : elderlyUsers) {
            // 检查用户是否存在
            User user = userMapper.selectById(data.userId);
            if (user == null) {
                user = new User();
                user.setId(data.userId);
                user.setUsername(data.username);
                user.setPassword(defaultPassword);
                user.setRealName(data.realName);
                user.setPhone(data.phone);
                user.setEmail(data.email);
                user.setStatus(1);
                user.setUserType(2); // 设置为老人类型
                user.setRoomNumber(data.roomNumber);
                user.setCreateTime(LocalDateTime.now());
                user.setUpdateTime(LocalDateTime.now());
                userMapper.insert(user);
                log.info("创建老人用户成功: {}", data.username);
            } else {
                // 更新为老人类型
                if (user.getUserType() == null || user.getUserType() != 2) {
                    user.setUserType(2);
                    user.setRoomNumber(data.roomNumber);
                    userMapper.updateById(user);
                    log.info("更新用户为老人类型: {}", data.username);
                } else {
                    log.info("老人用户已存在: {}", data.username);
                }
            }

            // 检查用户角色关联是否存在
            if (!hasUserRole(data.userId, 7)) {
                userRoleMapper.insertUserRole(data.userId, 7);
                log.info("为用户 {} 分配老人角色", data.username);
            }
        }
    }

    /**
     * 检查用户是否已有指定角色
     */
    private boolean hasUserRole(Integer userId, Integer roleId) {
        return userRoleMapper.countUserRole(userId, roleId) > 0;
    }

    /**
     * 老人用户数据内部类
     */
    private static class ElderlyUserData {
        final Integer userId;
        final String username;
        final String realName;
        final String phone;
        final String email;
        final String roomNumber;
        final String passwordHint;

        ElderlyUserData(Integer userId, String username, String realName, String phone,
                       String email, String roomNumber, String passwordHint) {
            this.userId = userId;
            this.username = username;
            this.realName = realName;
            this.phone = phone;
            this.email = email;
            this.roomNumber = roomNumber;
            this.passwordHint = passwordHint;
        }
    }
}
