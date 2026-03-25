package com.elderly.apartment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.CallCaregiver;

import java.util.List;

public interface CallCaregiverService extends IService<CallCaregiver> {

    IPage<CallCaregiver> getCallCaregiverPage(IPage<CallCaregiver> page, String elderlyName, String callType, Integer status, Integer urgencyLevel);

    List<CallCaregiver> getCallCaregiversByElderlyId(Integer elderlyId);

    List<CallCaregiver> getCallCaregiversByStatus(Integer status);

    List<CallCaregiver> getCallCaregiversByNurseId(Integer nurseId);

    List<CallCaregiver> getAllCallCaregivers();

    boolean createCallCaregiver(CallCaregiver callCaregiver);

    boolean updateCallCaregiver(CallCaregiver callCaregiver);

    boolean deleteCallCaregiver(Integer id);

    boolean assignNurse(Integer id, Integer nurseId, String nurseName);

    boolean respondToCall(Integer id);

    boolean completeCall(Integer id, String remark);

    boolean cancelCall(Integer id);

    int getPendingCount();

    int getTodayCount();
}
