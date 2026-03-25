package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.MealOrder;
import com.elderly.apartment.mapper.MealOrderMapper;
import com.elderly.apartment.service.MealOrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MealOrderServiceImpl extends ServiceImpl<MealOrderMapper, MealOrder> implements MealOrderService {

    @Override
    public List<MealOrder> getByDateAndType(LocalDate date, Integer mealType) {
        return baseMapper.selectByDateAndType(date, mealType);
    }

    @Override
    public List<MealOrder> getByElderlyId(Long elderlyId) {
        return baseMapper.selectByElderlyId(elderlyId);
    }

    @Override
    public IPage<MealOrder> getOrderList(Integer page, Integer size, Long elderlyId, String elderlyName, String roomNumber, String orderDate, Integer mealType, Integer status) {
        Page<MealOrder> pageParam = new Page<>(page, size);
        return baseMapper.selectOrderList(pageParam, elderlyId, elderlyName, roomNumber, orderDate, mealType, status);
    }

    @Override
    public MealOrder getOrderById(Long id) {
        return baseMapper.selectByIdWithElderly(id);
    }
}
