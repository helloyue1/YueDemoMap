package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.ServiceItem;

import java.util.List;

public interface ServiceItemService extends IService<ServiceItem> {

    List<ServiceItem> getItemsByCategoryId(Long categoryId);

    List<ServiceItem> getAllActiveItems();

    ServiceItem getItemWithCategory(Long id);
}
