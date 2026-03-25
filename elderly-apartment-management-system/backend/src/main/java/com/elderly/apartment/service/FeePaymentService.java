package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.FeePayment;

public interface FeePaymentService extends IService<FeePayment> {

    Page<FeePayment> getPaymentList(Integer page, Integer size, String elderlyName, String startDate, String endDate);

    void createPayment(FeePayment payment);
}
