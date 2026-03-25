package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.ServiceCategory;
import com.elderly.apartment.mapper.ServiceCategoryMapper;
import com.elderly.apartment.service.ServiceCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCategoryServiceImpl extends ServiceImpl<ServiceCategoryMapper, ServiceCategory> implements ServiceCategoryService {

    @Override
    public List<ServiceCategory> getActiveCategories() {
        return baseMapper.selectActiveCategories();
    }
}
