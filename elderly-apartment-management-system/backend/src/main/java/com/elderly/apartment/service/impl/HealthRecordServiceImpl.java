package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.HealthRecord;
import com.elderly.apartment.mapper.HealthRecordMapper;
import com.elderly.apartment.service.HealthRecordService;
import org.springframework.stereotype.Service;

/**
 * 健康档案服务实现类
 */
@Service
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord> implements HealthRecordService {
}
