package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.dto.BalanceRecordDTO;
import com.elderly.apartment.dto.RechargeRequest;
import com.elderly.apartment.dto.WithdrawRequest;
import com.elderly.apartment.entity.Role;
import com.elderly.apartment.entity.Room;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.entity.UserRole;
import com.elderly.apartment.entity.BalanceRecord;
import com.elderly.apartment.service.UserService;
import com.elderly.apartment.service.RoomService;
import com.elderly.apartment.mapper.RoleMapper;
import com.elderly.apartment.mapper.UserRoleMapper;
import com.elderly.apartment.mapper.BalanceRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BalanceRecordMapper balanceRecordMapper;

    // ==================== 通用用户接口 ====================

    @GetMapping
    public Result<Page<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer userType) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            wrapper.like(User::getUsername, username);
        }

        // 按用户类型筛选（1=员工，2=老人）
        if (userType != null) {
            wrapper.eq(User::getUserType, userType);
        }
        
        if (role != null && !role.isEmpty()) {
            LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.eq(Role::getCode, role);
            Role roleEntity = roleMapper.selectOne(roleWrapper);
            
            if (roleEntity != null) {
                LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
                userRoleWrapper.eq(UserRole::getRoleId, roleEntity.getId());
                List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
                List<Integer> userIds = userRoles.stream().map(UserRole::getUserId).collect(Collectors.toList());
                
                if (!userIds.isEmpty()) {
                    wrapper.in(User::getId, userIds);
                } else {
                    wrapper.eq(User::getId, -1);
                }
            }
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userService.page(pageParam, wrapper);
        
        // 为每个用户加载角色信息
        result.getRecords().forEach(user -> {
            user.setPassword(null);
            loadUserRoles(user);
        });
        
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        loadUserRoles(user);
        return Result.success(user);
    }

    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        if (userService.findByUsername(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        // 默认用户类型为员工
        if (user.getUserType() == null) {
            user.setUserType(1);
        }
        userService.save(user);
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        User existing = userService.getById(id);
        if (existing == null) {
            return Result.error("用户不存在");
        }
        
        user.setId(id);
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existing.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.updateById(user);
        user.setPassword(null);
        return Result.success(user);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        userService.removeById(id);
        return Result.success(null);
    }

    // ==================== 老人管理接口 ====================

    @GetMapping("/elderly")
    public Result<Page<User>> getElderlyList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) Integer status) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询老人类型
        wrapper.eq(User::getUserType, 2);
        
        if (name != null && !name.isEmpty()) {
            wrapper.like(User::getRealName, name);
        }

        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }

        if (roomNumber != null && !roomNumber.isEmpty()) {
            wrapper.like(User::getRoomNumber, roomNumber);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userService.page(pageParam, wrapper);
        
        // 处理老人数据
        result.getRecords().forEach(user -> {
            user.setPassword(null);
            loadUserRoles(user);
            // 计算年龄
            if (user.getBirthday() != null && !user.getBirthday().isEmpty()) {
                try {
                    LocalDate birthDate = LocalDate.parse(user.getBirthday());
                    user.setAge(Period.between(birthDate, LocalDate.now()).getYears());
                } catch (Exception e) {
                    user.setAge(0);
                }
            }
        });
        
        return Result.success(result);
    }

    @GetMapping("/elderly/{id}")
    public Result<User> getElderlyById(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null || !user.isElderly()) {
            return Result.error("老人信息不存在");
        }
        user.setPassword(null);
        loadUserRoles(user);
        
        // 计算年龄
        if (user.getBirthday() != null && !user.getBirthday().isEmpty()) {
            try {
                LocalDate birthDate = LocalDate.parse(user.getBirthday());
                user.setAge(Period.between(birthDate, LocalDate.now()).getYears());
            } catch (Exception e) {
                user.setAge(0);
            }
        }
        
        return Result.success(user);
    }

    @PostMapping("/elderly")
    public Result<User> createElderly(@RequestBody User user) {
        // 检查用户名是否已存在
        if (userService.findByUsername(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        
        // 设置老人类型
        user.setUserType(2);
        // 设置默认状态为在住
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        
        // 加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 默认密码123456
            user.setPassword(passwordEncoder.encode("123456"));
        }

        // 根据 roomNumber 查找 roomId
        if (user.getRoomNumber() != null && !user.getRoomNumber().isEmpty()) {
            LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Room::getRoomNumber, user.getRoomNumber());
            Room room = roomService.getOne(wrapper);
            if (room != null) {
                user.setRoomId(room.getId());
            }
        }

        userService.save(user);

        // 更新房间入住人数和状态
        if (user.getRoomId() != null) {
            Room room = roomService.getById(user.getRoomId());
            if (room != null) {
                room.setCurrentOccupancy(room.getCurrentOccupancy() + 1);
                if (room.getCurrentOccupancy() >= room.getCapacity()) {
                    room.setStatus(1); // 已满
                } else if (room.getCurrentOccupancy() > 0) {
                    room.setStatus(3); // 部分入住
                }
                roomService.updateById(room);
            }
        }

        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/elderly/{id}")
    public Result<User> updateElderly(@PathVariable Integer id, @RequestBody User user) {
        User existing = userService.getById(id);
        if (existing == null || !existing.isElderly()) {
            return Result.error("老人信息不存在");
        }

        Integer oldRoomId = existing.getRoomId();
        Integer newRoomId = user.getRoomId();

        // 根据 roomNumber 查找 roomId
        if (user.getRoomNumber() != null && !user.getRoomNumber().isEmpty()) {
            LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Room::getRoomNumber, user.getRoomNumber());
            Room room = roomService.getOne(wrapper);
            if (room != null) {
                newRoomId = room.getId();
                user.setRoomId(newRoomId);
            }
        }

        user.setId(id);
        user.setUserType(2); // 保持老人类型
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existing.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.updateById(user);

        // 处理房间变更
        handleRoomChange(oldRoomId, newRoomId);

        user.setPassword(null);
        return Result.success(user);
    }

    @DeleteMapping("/elderly/{id}")
    public Result<Void> deleteElderly(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null || !user.isElderly()) {
            return Result.error("老人信息不存在");
        }

        // 处理房间迁出
        if (user.getRoomId() != null) {
            Room room = roomService.getById(user.getRoomId());
            if (room != null && room.getCurrentOccupancy() > 0) {
                room.setCurrentOccupancy(room.getCurrentOccupancy() - 1);
                if (room.getCurrentOccupancy() == 0) {
                    room.setStatus(0); // 空闲
                } else if (room.getCurrentOccupancy() < room.getCapacity()) {
                    room.setStatus(3); // 部分入住
                }
                roomService.updateById(room);
            }
        }

        userService.removeById(id);
        return Result.success(null);
    }

    @PostMapping("/elderly/{id}/check-out")
    public Result<Void> checkOutElderly(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null || !user.isElderly()) {
            return Result.error("老人信息不存在");
        }

        // 处理房间迁出
        if (user.getRoomId() != null) {
            Room room = roomService.getById(user.getRoomId());
            if (room != null && room.getCurrentOccupancy() > 0) {
                room.setCurrentOccupancy(room.getCurrentOccupancy() - 1);
                if (room.getCurrentOccupancy() == 0) {
                    room.setStatus(0);
                } else if (room.getCurrentOccupancy() < room.getCapacity()) {
                    room.setStatus(3);
                }
                roomService.updateById(room);
            }
        }

        user.setStatus(2); // 已退住
        user.setRoomId(null);
        user.setRoomNumber(null);
        userService.updateById(user);
        return Result.success(null);
    }

    @GetMapping("/elderly/staying")
    public Result<List<User>> getAllStayingElderly() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, 2)
               .eq(User::getStatus, 1);
        List<User> result = userService.list(wrapper);
        result.forEach(user -> {
            user.setPassword(null);
            loadUserRoles(user);
        });
        return Result.success(result);
    }

    @GetMapping("/elderly/checked-out")
    public Result<List<User>> getAllCheckedOutElderly() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, 2)
               .eq(User::getStatus, 2);
        List<User> result = userService.list(wrapper);
        result.forEach(user -> {
            user.setPassword(null);
            loadUserRoles(user);
        });
        return Result.success(result);
    }

    @GetMapping("/elderly/search")
    public Result<List<User>> searchElderly(@RequestParam String name) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, 2)
               .like(User::getRealName, name);
        List<User> result = userService.list(wrapper);
        result.forEach(user -> {
            user.setPassword(null);
            loadUserRoles(user);
        });
        return Result.success(result);
    }

    @PutMapping("/elderly/{id}/health-status")
    public Result<Void> updateHealthStatus(@PathVariable Integer id, @RequestBody String healthStatus) {
        User user = userService.getById(id);
        if (user == null || !user.isElderly()) {
            return Result.error("老人信息不存在");
        }

        user.setHealthStatus(healthStatus);
        userService.updateById(user);
        return Result.success(null);
    }

    // ==================== 个人资料接口 ====================

    @GetMapping("/profile")
    public Result<User> getProfile() {
        User user = userService.getCurrentUser();
        if (user == null) {
            return Result.error("未登录");
        }
        user.setPassword(null);
        loadUserRoles(user);
        return Result.success(user);
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody ProfileUpdateRequest request) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return Result.error("未登录");
        }

        User user = new User();
        user.setId(currentUser.getId());
        user.setRealName(request.getName());
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setBio(request.getBio());
        user.setPassword(currentUser.getPassword());
        
        // 老人特有字段
        if (currentUser.isElderly()) {
            user.setBirthday(request.getBirthday());
            user.setIdCard(request.getIdCard());
            // 性别转换
            if ("男".equals(request.getGender())) {
                user.setGender(1);
            } else if ("女".equals(request.getGender())) {
                user.setGender(2);
            }
        }
        
        userService.updateById(user);

        User updatedUser = userService.getById(currentUser.getId());
        updatedUser.setPassword(null);
        loadUserRoles(updatedUser);
        return Result.success(updatedUser);
    }

    @PutMapping("/{id}/password")
    public Result<Void> updatePassword(@PathVariable Integer id, @RequestBody PasswordRequest request) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return Result.error("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.updateById(user);
        return Result.success(null);
    }

    // ==================== 私有方法 ====================

    private void loadUserRoles(User user) {
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getUserId, user.getId());
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        
        if (!userRoles.isEmpty()) {
            List<Integer> roleIds = userRoles.stream()
                    .map(UserRole::getUserId)
                    .collect(Collectors.toList());
            List<Role> roles = roleMapper.selectBatchIds(roleIds);
            user.setRoles(roles);
        }
    }

    private void handleRoomChange(Integer oldRoomId, Integer newRoomId) {
        if (oldRoomId != null && !oldRoomId.equals(newRoomId)) {
            Room oldRoom = roomService.getById(oldRoomId);
            if (oldRoom != null && oldRoom.getCurrentOccupancy() > 0) {
                oldRoom.setCurrentOccupancy(oldRoom.getCurrentOccupancy() - 1);
                if (oldRoom.getCurrentOccupancy() == 0) {
                    oldRoom.setStatus(0);
                } else if (oldRoom.getCurrentOccupancy() < oldRoom.getCapacity()) {
                    oldRoom.setStatus(3);
                }
                roomService.updateById(oldRoom);
            }
        }

        if (newRoomId != null && !newRoomId.equals(oldRoomId)) {
            Room newRoom = roomService.getById(newRoomId);
            if (newRoom != null) {
                newRoom.setCurrentOccupancy(newRoom.getCurrentOccupancy() + 1);
                if (newRoom.getCurrentOccupancy() >= newRoom.getCapacity()) {
                    newRoom.setStatus(1);
                } else if (newRoom.getCurrentOccupancy() > 0) {
                    newRoom.setStatus(3);
                }
                roomService.updateById(newRoom);
            }
        }
    }

    // ==================== 余额管理接口 ====================

    @GetMapping("/elderly/balance")
    public Result<BigDecimal> getBalance(@RequestParam(required = false) Integer userId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return Result.error("未登录");
        }

        // 如果指定了userId，检查权限
        Integer targetUserId = userId;
        if (targetUserId == null) {
            targetUserId = currentUser.getId();
        } else if (!currentUser.isElderly() && !targetUserId.equals(currentUser.getId())) {
            // 非老人角色可以查看其他老人余额
            User targetUser = userService.getById(targetUserId);
            if (targetUser == null || !targetUser.isElderly()) {
                return Result.error("目标用户不存在或不是老人");
            }
        } else if (currentUser.isElderly() && !targetUserId.equals(currentUser.getId())) {
            // 老人只能查看自己的余额
            return Result.error("无权查看他人余额");
        }

        User user = userService.getById(targetUserId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        BigDecimal balance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        return Result.success(balance);
    }

    @PostMapping("/elderly/balance/recharge")
    public Result<Void> recharge(@RequestBody RechargeRequest request) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return Result.error("未登录");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("充值金额必须大于0");
        }

        // 老人自己充值或管理员为老人充值
        Integer targetUserId = currentUser.isElderly() ? currentUser.getId() : null;
        if (targetUserId == null) {
            return Result.error("请指定要充值的用户");
        }

        User user = userService.getById(targetUserId);
        if (user == null || !user.isElderly()) {
            return Result.error("用户不存在或不是老人");
        }

        BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        user.setBalance(currentBalance.add(request.getAmount()));
        userService.updateById(user);

        return Result.success(null);
    }

    @PostMapping("/elderly/balance/withdraw")
    public Result<Void> withdraw(@RequestBody WithdrawRequest request) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return Result.error("未登录");
        }

        if (!currentUser.isElderly()) {
            return Result.error("只有老人可以提现");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("提现金额必须大于0");
        }

        User user = userService.getById(currentUser.getId());
        BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;

        if (currentBalance.compareTo(request.getAmount()) < 0) {
            return Result.error("余额不足");
        }

        user.setBalance(currentBalance.subtract(request.getAmount()));
        userService.updateById(user);

        return Result.success(null);
    }

    @GetMapping("/elderly/balance/records")
    public Result<List<BalanceRecordDTO>> getBalanceRecords(@RequestParam(required = false) Integer userId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return Result.error("未登录");
        }

        Integer targetUserId = userId;
        if (targetUserId == null) {
            targetUserId = currentUser.getId();
        } else if (currentUser.isElderly() && !targetUserId.equals(currentUser.getId())) {
            return Result.error("无权查看他人记录");
        }

        User user = userService.getById(targetUserId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 从余额记录表查询交易记录
        List<BalanceRecordDTO> records = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 查询数据库中的交易记录
        LambdaQueryWrapper<BalanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BalanceRecord::getElderlyId, targetUserId)
               .orderByDesc(BalanceRecord::getCreateTime);
        List<BalanceRecord> balanceRecords = balanceRecordMapper.selectList(wrapper);

        if (balanceRecords != null && !balanceRecords.isEmpty()) {
            for (BalanceRecord br : balanceRecords) {
                BalanceRecordDTO dto = new BalanceRecordDTO();
                dto.setId(br.getId());
                dto.setTitle(br.getTitle());
                dto.setDescription(br.getDescription());
                dto.setAmount(br.getAmount());
                dto.setType(br.getType());
                dto.setPaymentMethod(br.getPaymentMethod());
                dto.setCreateTime(br.getCreateTime() != null ? br.getCreateTime().format(formatter) : "");
                records.add(dto);
            }
        }

        return Result.success(records);
    }

    // ==================== 请求类 ====================

    public static class PasswordRequest {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class ProfileUpdateRequest {
        private String name;
        private String nickname;
        private String gender;
        private String birthday;
        private String phone;
        private String idCard;
        private String bio;
        private String avatar;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getBirthday() { return birthday; }
        public void setBirthday(String birthday) { this.birthday = birthday; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getIdCard() { return idCard; }
        public void setIdCard(String idCard) { this.idCard = idCard; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
    }
}
