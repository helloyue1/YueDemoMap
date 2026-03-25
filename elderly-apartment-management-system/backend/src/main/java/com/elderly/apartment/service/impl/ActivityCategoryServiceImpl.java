package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.ActivityCategory;
import com.elderly.apartment.mapper.ActivityCategoryMapper;
import com.elderly.apartment.service.ActivityCategoryService;
import org.springframework.stereotype.Service;

@Service
public class ActivityCategoryServiceImpl extends ServiceImpl<ActivityCategoryMapper, ActivityCategory> implements ActivityCategoryService {
}
