package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.MealWeeklyPlan;
import com.elderly.apartment.mapper.MealWeeklyPlanMapper;
import com.elderly.apartment.service.MealWeeklyPlanService;
import org.springframework.stereotype.Service;

@Service
public class MealWeeklyPlanServiceImpl extends ServiceImpl<MealWeeklyPlanMapper, MealWeeklyPlan> implements MealWeeklyPlanService {
}
