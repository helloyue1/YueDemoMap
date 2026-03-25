package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.FeeInvoice;
import com.elderly.apartment.mapper.FeeInvoiceMapper;
import com.elderly.apartment.service.FeeInvoiceService;
import org.springframework.stereotype.Service;

@Service
public class FeeInvoiceServiceImpl extends ServiceImpl<FeeInvoiceMapper, FeeInvoice> implements FeeInvoiceService {
}
