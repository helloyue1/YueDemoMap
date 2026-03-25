package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.FeeBill;

import java.util.List;
import java.util.Map;

public interface FeeBillService extends IService<FeeBill> {

    Page<FeeBill> getBillList(Integer page, Integer size, String elderlyName, String billMonth, Integer status);

    FeeBill getBillDetail(Long id);

    void saveBill(FeeBill bill);

    void generateMonthlyBills(String billMonth);

    Map<String, Object> getStatistics(String startMonth, String endMonth);

    List<Map<String, Object>> getDebtorList();
}
