package com.elderly.apartment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.CareRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CareRecordService extends IService<CareRecord> {

    IPage<CareRecord> getCareRecordPage(IPage<CareRecord> page, String elderlyName, String caregiverName, 
                                        Integer careType, Integer status, LocalDateTime startTime, LocalDateTime endTime);

    List<CareRecord> getCareRecordsByElderlyId(Integer elderlyId);

    List<CareRecord> getCareRecordsByCaregiverId(Integer caregiverId);

    List<CareRecord> getCareRecordsByCarePlanId(Integer carePlanId);

    List<CareRecord> getTodayRecords();

    List<CareRecord> getRecordsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    boolean updateStatus(Integer id, Integer status);

    boolean createCareRecord(CareRecord careRecord);

    boolean updateCareRecord(CareRecord careRecord);

    Map<String, Object> getCareStatistics(Integer caregiverId);
}
