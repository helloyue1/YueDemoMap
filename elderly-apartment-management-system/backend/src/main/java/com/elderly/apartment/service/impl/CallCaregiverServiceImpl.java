package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.CallCaregiver;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.mapper.CallCaregiverMapper;
import com.elderly.apartment.service.CallCaregiverService;
import com.elderly.apartment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CallCaregiverServiceImpl extends ServiceImpl<CallCaregiverMapper, CallCaregiver> implements CallCaregiverService {

    @Autowired
    private UserService userService;

    @Override
    public IPage<CallCaregiver> getCallCaregiverPage(IPage<CallCaregiver> page, String elderlyName, String callType, Integer status, Integer urgencyLevel) {
        LambdaQueryWrapper<CallCaregiver> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(elderlyName), CallCaregiver::getElderlyName, elderlyName)
               .eq(StringUtils.hasText(callType), CallCaregiver::getCallType, callType)
               .eq(status != null, CallCaregiver::getStatus, status)
               .eq(urgencyLevel != null, CallCaregiver::getUrgencyLevel, urgencyLevel)
               .eq(CallCaregiver::getDeleted, 0)
               .orderByAsc(CallCaregiver::getStatus)
               .orderByDesc(CallCaregiver::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public List<CallCaregiver> getCallCaregiversByElderlyId(Integer elderlyId) {
        LambdaQueryWrapper<CallCaregiver> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CallCaregiver::getElderlyId, elderlyId)
               .eq(CallCaregiver::getDeleted, 0)
               .orderByDesc(CallCaregiver::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<CallCaregiver> getCallCaregiversByStatus(Integer status) {
        LambdaQueryWrapper<CallCaregiver> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CallCaregiver::getStatus, status)
               .eq(CallCaregiver::getDeleted, 0)
               .orderByDesc(CallCaregiver::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<CallCaregiver> getCallCaregiversByNurseId(Integer nurseId) {
        LambdaQueryWrapper<CallCaregiver> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CallCaregiver::getAssignedNurseId, nurseId)
               .eq(CallCaregiver::getDeleted, 0)
               .orderByDesc(CallCaregiver::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<CallCaregiver> getAllCallCaregivers() {
        LambdaQueryWrapper<CallCaregiver> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CallCaregiver::getDeleted, 0)
               .orderByAsc(CallCaregiver::getStatus)
               .orderByDesc(CallCaregiver::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public boolean createCallCaregiver(CallCaregiver callCaregiver) {
        // 如果提供了老人ID且房间号为空，自动获取老人房间号
        if (callCaregiver.getElderlyId() != null && !StringUtils.hasText(callCaregiver.getRoomNumber())) {
            User user = userService.getById(callCaregiver.getElderlyId());
            if (user != null && StringUtils.hasText(user.getRoomNumber())) {
                callCaregiver.setRoomNumber(user.getRoomNumber());
            }
        }
        callCaregiver.setCreateTime(LocalDateTime.now());
        callCaregiver.setUpdateTime(LocalDateTime.now());
        callCaregiver.setDeleted(0);
        callCaregiver.setStatus(1);
        return this.save(callCaregiver);
    }

    @Override
    public boolean updateCallCaregiver(CallCaregiver callCaregiver) {
        callCaregiver.setUpdateTime(LocalDateTime.now());
        return this.updateById(callCaregiver);
    }

    @Override
    public boolean deleteCallCaregiver(Integer id) {
        CallCaregiver callCaregiver = this.getById(id);
        if (callCaregiver == null) {
            return false;
        }
        callCaregiver.setDeleted(1);
        callCaregiver.setUpdateTime(LocalDateTime.now());
        return this.updateById(callCaregiver);
    }

    @Override
    public boolean assignNurse(Integer id, Integer nurseId, String nurseName) {
        CallCaregiver callCaregiver = this.getById(id);
        if (callCaregiver == null) {
            return false;
        }
        callCaregiver.setAssignedNurseId(nurseId);
        callCaregiver.setAssignedNurseName(nurseName);
        callCaregiver.setStatus(2);
        callCaregiver.setResponseTime(LocalDateTime.now());
        callCaregiver.setUpdateTime(LocalDateTime.now());
        return this.updateById(callCaregiver);
    }

    @Override
    public boolean respondToCall(Integer id) {
        CallCaregiver callCaregiver = this.getById(id);
        if (callCaregiver == null) {
            return false;
        }
        callCaregiver.setStatus(3);
        callCaregiver.setResponseTime(LocalDateTime.now());
        callCaregiver.setUpdateTime(LocalDateTime.now());
        return this.updateById(callCaregiver);
    }

    @Override
    public boolean completeCall(Integer id, String remark) {
        CallCaregiver callCaregiver = this.getById(id);
        if (callCaregiver == null) {
            return false;
        }
        callCaregiver.setStatus(4);
        callCaregiver.setCompleteTime(LocalDateTime.now());
        if (remark != null) {
            callCaregiver.setRemark(remark);
        }
        callCaregiver.setUpdateTime(LocalDateTime.now());
        return this.updateById(callCaregiver);
    }

    @Override
    public boolean cancelCall(Integer id) {
        CallCaregiver callCaregiver = this.getById(id);
        if (callCaregiver == null) {
            return false;
        }
        callCaregiver.setStatus(5);
        callCaregiver.setUpdateTime(LocalDateTime.now());
        return this.updateById(callCaregiver);
    }

    @Override
    public int getPendingCount() {
        LambdaQueryWrapper<CallCaregiver> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CallCaregiver::getStatus, 1)
               .eq(CallCaregiver::getDeleted, 0);
        return (int) this.count(wrapper);
    }

    @Override
    public int getTodayCount() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        
        LambdaQueryWrapper<CallCaregiver> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(CallCaregiver::getCreateTime, startOfDay)
               .lt(CallCaregiver::getCreateTime, endOfDay)
               .eq(CallCaregiver::getDeleted, 0);
        return (int) this.count(wrapper);
    }
}
