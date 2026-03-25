package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.HealthData;

import java.util.List;

/**
 * 健康数据服务接口
 */
public interface HealthDataService extends IService<HealthData> {

    /**
     * 根据老人ID查询健康数据列表
     */
    List<HealthData> getByElderlyId(Integer elderlyId);

    /**
     * 查询最新的健康数据
     */
    List<HealthData> getLatest(Integer limit);
}
