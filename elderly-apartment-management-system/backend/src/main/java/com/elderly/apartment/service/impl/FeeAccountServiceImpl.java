package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.FeeAccount;
import com.elderly.apartment.mapper.FeeAccountMapper;
import com.elderly.apartment.service.FeeAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class FeeAccountServiceImpl extends ServiceImpl<FeeAccountMapper, FeeAccount> implements FeeAccountService {

    @Autowired
    private FeeAccountMapper feeAccountMapper;

    @Override
    public FeeAccount getByElderlyId(Integer elderlyId) {
        return feeAccountMapper.selectByElderlyId(elderlyId);
    }

    @Override
    @Transactional
    public void createAccount(Integer elderlyId) {
        LambdaQueryWrapper<FeeAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeAccount::getElderlyId, elderlyId);
        if (count(wrapper) == 0) {
            FeeAccount account = new FeeAccount();
            account.setElderlyId(elderlyId);
            account.setBalance(BigDecimal.ZERO);
            account.setTotalPaid(BigDecimal.ZERO);
            account.setTotalConsumed(BigDecimal.ZERO);
            account.setStatus(1);
            account.setCreateTime(LocalDateTime.now());
            account.setUpdateTime(LocalDateTime.now());
            save(account);
        }
    }

    @Override
    @Transactional
    public boolean addBalance(Integer elderlyId, BigDecimal amount) {
        return feeAccountMapper.addBalance(elderlyId, amount) > 0;
    }

    @Override
    @Transactional
    public boolean deductBalance(Integer elderlyId, BigDecimal amount) {
        return feeAccountMapper.deductBalance(elderlyId, amount) > 0;
    }
}
