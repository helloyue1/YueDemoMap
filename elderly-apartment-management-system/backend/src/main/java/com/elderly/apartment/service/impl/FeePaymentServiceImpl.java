package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.FeeBill;
import com.elderly.apartment.entity.FeePayment;
import com.elderly.apartment.mapper.FeePaymentMapper;
import com.elderly.apartment.service.FeeBillService;
import com.elderly.apartment.service.FeePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class FeePaymentServiceImpl extends ServiceImpl<FeePaymentMapper, FeePayment> implements FeePaymentService {

    @Autowired
    private FeePaymentMapper feePaymentMapper;

    @Autowired
    private FeeBillService feeBillService;

    @Override
    public Page<FeePayment> getPaymentList(Integer page, Integer size, String elderlyName, String startDate, String endDate) {
        Page<FeePayment> pageParam = new Page<>(page, size);
        return feePaymentMapper.selectPaymentList(pageParam, elderlyName, startDate, endDate);
    }

    @Override
    @Transactional
    public void createPayment(FeePayment payment) {
        payment.setPaymentNo(generatePaymentNo());
        payment.setStatus(1);
        payment.setCreateTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());

        save(payment);

        FeeBill bill = feeBillService.getById(payment.getBillId());
        if (bill != null) {
            BigDecimal newPaidAmount = bill.getPaidAmount().add(payment.getPaymentAmount());
            bill.setPaidAmount(newPaidAmount);

            if (newPaidAmount.compareTo(bill.getPayableAmount()) >= 0) {
                bill.setStatus(2);
                bill.setPaidTime(LocalDateTime.now());
            } else {
                bill.setStatus(1);
            }

            feeBillService.updateById(bill);
        }
    }

    private String generatePaymentNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomStr = String.format("%04d", new Random().nextInt(10000));
        return "PAY" + dateStr + randomStr;
    }
}
