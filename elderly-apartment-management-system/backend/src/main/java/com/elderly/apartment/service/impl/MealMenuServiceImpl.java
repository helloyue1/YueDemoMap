package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.MealMenu;
import com.elderly.apartment.mapper.MealMenuMapper;
import com.elderly.apartment.service.MealMenuService;
import org.springframework.stereotype.Service;

@Service
public class MealMenuServiceImpl extends ServiceImpl<MealMenuMapper, MealMenu> implements MealMenuService {
}
