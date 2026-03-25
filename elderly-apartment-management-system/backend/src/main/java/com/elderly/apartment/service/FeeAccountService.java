package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.FeeAccount;

import java.math.BigDecimal;

public interface FeeAccountService extends IService<FeeAccount> {

    FeeAccount getByElderlyId(Integer elderlyId);

    void createAccount(Integer elderlyId);

    boolean addBalance(Integer elderlyId, BigDecimal amount);

    boolean deductBalance(Integer elderlyId, BigDecimal amount);
}
