package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.FeeDetail;
import com.elderly.apartment.mapper.FeeDetailMapper;
import com.elderly.apartment.service.FeeDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeeDetailServiceImpl extends ServiceImpl<FeeDetailMapper, FeeDetail> implements FeeDetailService {

    @Autowired
    private FeeDetailMapper feeDetailMapper;

    @Override
    public List<FeeDetail> getDetailsByBillId(Long billId) {
        return feeDetailMapper.selectByBillId(billId);
    }
}
