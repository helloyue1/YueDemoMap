package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.dto.BalanceRecordDTO;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.entity.Role;

import java.math.BigDecimal;
import java.util.List;

public interface UserService extends IService<User> {

    User findByUsername(String username);

    List<Role> findRolesByUserId(Integer userId);

    User login(String username, String password);

    void logout(String token);

    User register(User user);

    void resetPassword(Integer userId, String newPassword);

    List<User> findByRoleId(Integer roleId);

    User getCurrentUser();

    // ==================== 余额管理方法 ====================

    BigDecimal getBalance(Integer userId);

    void recharge(Integer userId, BigDecimal amount, String paymentMethod);

    void withdraw(Integer userId, BigDecimal amount);

    void deductBalance(Integer userId, BigDecimal amount, String title, String description);

    List<BalanceRecordDTO> getBalanceRecords(Integer userId);
}
