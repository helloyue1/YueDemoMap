package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.ServiceItem;
import com.elderly.apartment.mapper.ServiceItemMapper;
import com.elderly.apartment.service.ServiceItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceItemServiceImpl extends ServiceImpl<ServiceItemMapper, ServiceItem> implements ServiceItemService {

    @Override
    public List<ServiceItem> getItemsByCategoryId(Long categoryId) {
        return baseMapper.selectByCategoryId(categoryId);
    }

    @Override
    public List<ServiceItem> getAllActiveItems() {
        return baseMapper.selectAllActiveItems();
    }

    @Override
    public ServiceItem getItemWithCategory(Long id) {
        return baseMapper.selectItemWithCategory(id);
    }
}
