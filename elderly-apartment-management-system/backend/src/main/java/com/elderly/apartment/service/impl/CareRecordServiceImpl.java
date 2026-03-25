package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.CareRecord;
import com.elderly.apartment.mapper.CareRecordMapper;
import com.elderly.apartment.service.CareRecordService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CareRecordServiceImpl extends ServiceImpl<CareRecordMapper, CareRecord> implements CareRecordService {

    @Override
    public IPage<CareRecord> getCareRecordPage(IPage<CareRecord> page, String elderlyName, String caregiverName,
                                               Integer careType, Integer status, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<CareRecord> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(elderlyName)) {
            wrapper.like(CareRecord::getElderlyName, elderlyName);
        }

        if (StringUtils.hasText(caregiverName)) {
            wrapper.like(CareRecord::getCaregiverName, caregiverName);
        }

        if (careType != null) {
            wrapper.eq(CareRecord::getCareType, careType);
        }

        if (status != null) {
            wrapper.eq(CareRecord::getStatus, status);
        }

        if (startTime != null) {
            wrapper.ge(CareRecord::getCareTime, startTime);
        }

        if (endTime != null) {
            wrapper.le(CareRecord::getCareTime, endTime);
        }

        wrapper.eq(CareRecord::getDeleted, 0);
        wrapper.orderByDesc(CareRecord::getCareTime);

        return this.page(page, wrapper);
    }

    @Override
    public List<CareRecord> getCareRecordsByElderlyId(Integer elderlyId) {
        return baseMapper.selectByElderlyId(elderlyId);
    }

    @Override
    public List<CareRecord> getCareRecordsByCaregiverId(Integer caregiverId) {
        return baseMapper.selectByCaregiverId(caregiverId);
    }

    @Override
    public List<CareRecord> getCareRecordsByCarePlanId(Integer carePlanId) {
        return baseMapper.selectByCarePlanId(carePlanId);
    }

    @Override
    public List<CareRecord> getTodayRecords() {
        return baseMapper.selectTodayRecords();
    }

    @Override
    public List<CareRecord> getRecordsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.selectByTimeRange(startTime, endTime);
    }

    @Override
    public boolean updateStatus(Integer id, Integer status) {
        return baseMapper.updateStatus(id, status) > 0;
    }

    @Override
    public boolean createCareRecord(CareRecord careRecord) {
        return this.save(careRecord);
    }

    @Override
    public boolean updateCareRecord(CareRecord careRecord) {
        return this.updateById(careRecord);
    }

    @Override
    public Map<String, Object> getCareStatistics(Integer caregiverId) {
        Map<String, Object> statistics = new HashMap<>();

        // 统计完成数量
        Long completedCount = baseMapper.countCompletedByCaregiver(caregiverId);
        statistics.put("completedCount", completedCount);

        // 统计今日记录数
        List<CareRecord> todayRecords = baseMapper.selectTodayRecords();
        long todayCount = todayRecords.stream()
                .filter(r -> r.getCaregiverId() != null && r.getCaregiverId().equals(caregiverId))
                .count();
        statistics.put("todayCount", todayCount);

        return statistics;
    }
}
