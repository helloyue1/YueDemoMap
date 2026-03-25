package com.elderly.apartment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.MealOrder;

import java.time.LocalDate;
import java.util.List;

public interface MealOrderService extends IService<MealOrder> {

    List<MealOrder> getByDateAndType(LocalDate date, Integer mealType);

    List<MealOrder> getByElderlyId(Long elderlyId);

    IPage<MealOrder> getOrderList(Integer page, Integer size, Long elderlyId, String elderlyName, String roomNumber, String orderDate, Integer mealType, Integer status);

    MealOrder getOrderById(Long id);
}
