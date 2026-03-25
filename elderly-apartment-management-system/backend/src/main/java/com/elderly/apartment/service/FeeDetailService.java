package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.FeeDetail;

import java.util.List;

public interface FeeDetailService extends IService<FeeDetail> {

    List<FeeDetail> getDetailsByBillId(Long billId);
}
