package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.ServiceCategory;

import java.util.List;

public interface ServiceCategoryService extends IService<ServiceCategory> {

    List<ServiceCategory> getActiveCategories();
}
