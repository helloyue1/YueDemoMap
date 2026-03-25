package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.dto.BalanceRecordDTO;
import com.elderly.apartment.entity.BalanceRecord;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.entity.Role;
import com.elderly.apartment.mapper.BalanceRecordMapper;
import com.elderly.apartment.mapper.UserMapper;
import com.elderly.apartment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BalanceRecordMapper balanceRecordMapper;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public List<Role> findRolesByUserId(Integer userId) {
        return userMapper.findRolesByUserId(userId);
    }

    @Override
    public User login(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("User is disabled");
        }
        return user;
    }

    @Override
    public void logout(String token) {
    }

    @Override
    public User register(User user) {
        if (findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        save(user);
        return user;
    }

    @Override
    public void resetPassword(Integer userId, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }

    @Override
    public List<User> findByRoleId(Integer roleId) {
        return null;
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        return findByUsername(username);
    }

    // ==================== 余额管理方法实现 ====================

    @Override
    public BigDecimal getBalance(Integer userId) {
        User user = getById(userId);
        if (user == null) {
            return BigDecimal.ZERO;
        }
        return user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
    }

    @Override
    public void recharge(Integer userId, BigDecimal amount, String paymentMethod) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("充值金额必须大于0");
        }

        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新余额
        BigDecimal newBalance = user.getBalance() != null 
            ? user.getBalance().add(amount) 
            : amount;
        user.setBalance(newBalance);
        updateById(user);

        // 添加交易记录
        BalanceRecord record = new BalanceRecord();
        record.setElderlyId(userId);
        record.setAmount(amount);
        record.setType("recharge");
        record.setTitle("余额充值");
        record.setPaymentMethod(paymentMethod);
        record.setCreateTime(LocalDateTime.now());
        balanceRecordMapper.insert(record);
    }

    @Override
    public void withdraw(Integer userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("提现金额必须大于0");
        }

        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        BigDecimal currentBalance = user.getBalance() != null 
            ? user.getBalance() 
            : BigDecimal.ZERO;

        if (currentBalance.compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }

        // 更新余额
        BigDecimal newBalance = currentBalance.subtract(amount);
        user.setBalance(newBalance);
        updateById(user);

        // 添加交易记录
        BalanceRecord record = new BalanceRecord();
        record.setElderlyId(userId);
        record.setAmount(amount);
        record.setType("withdraw");
        record.setTitle("余额提现");
        record.setCreateTime(LocalDateTime.now());
        balanceRecordMapper.insert(record);
    }

    @Override
    public void deductBalance(Integer userId, BigDecimal amount, String title, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("消费金额必须大于0");
        }

        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        BigDecimal currentBalance = user.getBalance() != null
                ? user.getBalance()
                : BigDecimal.ZERO;

        if (currentBalance.compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }

        // 更新余额
        BigDecimal newBalance = currentBalance.subtract(amount);
        user.setBalance(newBalance);
        updateById(user);

        // 添加消费记录
        BalanceRecord record = new BalanceRecord();
        record.setElderlyId(userId);
        record.setAmount(amount);
        record.setType("consume");
        record.setTitle(title != null ? title : "消费支出");
        record.setDescription(description != null ? description : "");
        record.setCreateTime(LocalDateTime.now());
        balanceRecordMapper.insert(record);
    }

    @Override
    public List<BalanceRecordDTO> getBalanceRecords(Integer userId) {
        LambdaQueryWrapper<BalanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BalanceRecord::getElderlyId, userId)
               .orderByDesc(BalanceRecord::getCreateTime);
        List<BalanceRecord> records = balanceRecordMapper.selectList(wrapper);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return records.stream().map(record -> {
            BalanceRecordDTO dto = new BalanceRecordDTO();
            dto.setId(record.getId());
            dto.setTitle(record.getTitle());
            dto.setDescription(record.getDescription());
            dto.setAmount(record.getAmount());
            dto.setType(record.getType());
            if (record.getCreateTime() != null) {
                dto.setCreateTime(record.getCreateTime().format(formatter));
            }
            return dto;
        }).collect(Collectors.toList());
    }
}
