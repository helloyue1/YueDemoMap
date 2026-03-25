package com.elderly.apartment.controller;

import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.security.JwtUtils;
import com.elderly.apartment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        // 先查询用户是否存在
        User user = userService.findByUsername(loginRequest.getUsername());
        if (user == null) {
            return Result.error("用户不存在: " + loginRequest.getUsername());
        }
        
        // 验证密码
        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        
        // 如果BCrypt验证失败，尝试明文密码对比（用于测试）
        if (!passwordMatches) {
            passwordMatches = loginRequest.getPassword().equals(user.getPassword());
        }
        
        if (!passwordMatches) {
            return Result.error("密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }

        // 检查用户类型，只有员工(user_type=1)才能登录管理端（APP端除外）
        boolean isApp = "app".equalsIgnoreCase(loginRequest.getPlatform());
        if (!isApp && (user.getUserType() == null || user.getUserType() != 1)) {
            return Result.error("只有管理员/员工才能登录管理端");
        }

        // 生成JWT token
        String jwt = jwtUtils.generateToken(user.getUsername());

        // 清除密码后返回
        user.setPassword(null);
        user.setRoles(userService.findRolesByUserId(user.getId()));

        // 如果用户是老人类型(user_type=2)，设置elderlyId为自己的id
        if (user.isElderly()) {
            user.setElderlyId(user.getId());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("user", user);

        return Result.success("登录成功", response);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        User registeredUser = userService.register(user);
        return Result.success("Registration successful", registeredUser);
    }

    @PostMapping("/logout")
    public Result<String> logout() {
        SecurityContextHolder.clearContext();
        return Result.success("Logged out successfully", null);
    }

    @GetMapping("/me")
    public Result<User> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        user.setPassword(null);
        user.setRoles(userService.findRolesByUserId(user.getId()));

        // 如果用户是老人类型(user_type=2)，设置elderlyId为自己的id
        if (user.isElderly()) {
            user.setElderlyId(user.getId());
        }

        return Result.success(user);
    }

    public static class LoginRequest {
        private String username;
        private String password;
        private String platform; // app 或 web

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }
    }
}
