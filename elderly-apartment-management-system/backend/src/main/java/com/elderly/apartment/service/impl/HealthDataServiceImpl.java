package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.HealthData;
import com.elderly.apartment.mapper.HealthDataMapper;
import com.elderly.apartment.service.HealthDataService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 健康数据服务实现类
 */
@Service
public class HealthDataServiceImpl extends ServiceImpl<HealthDataMapper, HealthData> implements HealthDataService {

    @Override
    public List<HealthData> getByElderlyId(Integer elderlyId) {
        return baseMapper.selectByElderlyId(elderlyId);
    }

    @Override
    public List<HealthData> getLatest(Integer limit) {
        return baseMapper.selectLatest(limit);
    }
}
