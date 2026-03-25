package com.elderly.apartment.security;

import com.elderly.apartment.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 数据权限工具类
 * 用于处理老人角色的数据隔离
 */
public class DataPermissionUtil {

    /**
     * 获取当前登录用户
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser();
        }
        return null;
    }

    /**
     * 获取当前登录用户ID
     */
    public static Integer getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * 判断当前用户是否为老人角色
     */
    public static boolean isElderlyRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ELDERLY"));
    }

    /**
     * 判断当前用户是否为管理员角色
     */
    public static boolean isAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * 获取老人角色的老人ID
     * 如果当前用户是老人角色，返回关联的老人ID
     * 否则返回null
     */
    public static Integer getElderlyIdForCurrentUser() {
        if (!isElderlyRole()) {
            return null;
        }
        
        User user = getCurrentUser();
        if (user == null) {
            return null;
        }
        
        // 直接返回用户的ID（老人数据已合并到user表）
        return user.getId();
    }

    /**
     * 检查当前用户是否有权限访问指定的老人数据
     * @param elderlyId 老人ID
     * @return true: 有权限, false: 无权限
     */
    public static boolean hasPermissionForElderly(Integer elderlyId) {
        // 管理员可以访问所有数据
        if (isAdminRole()) {
            return true;
        }
        
        // 老人只能访问自己的数据
        if (isElderlyRole()) {
            Integer currentElderlyId = getElderlyIdForCurrentUser();
            return currentElderlyId != null && currentElderlyId.equals(elderlyId);
        }
        
        // 其他角色（如护理人员）可以根据业务需求扩展
        return true;
    }

    /**
     * 获取当前用户可以访问的老人ID列表
     * 如果是老人角色，返回只包含自己的列表
     * 如果是管理员，返回null表示可以访问所有
     */
    public static Integer getAccessibleElderlyId() {
        if (isAdminRole()) {
            return null; // null 表示可以访问所有
        }
        
        if (isElderlyRole()) {
            return getElderlyIdForCurrentUser();
        }
        
        return null;
    }
}
