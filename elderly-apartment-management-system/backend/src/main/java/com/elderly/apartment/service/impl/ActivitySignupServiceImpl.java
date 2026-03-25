package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.ActivitySignup;
import com.elderly.apartment.mapper.ActivitySignupMapper;
import com.elderly.apartment.service.ActivitySignupService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ActivitySignupServiceImpl extends ServiceImpl<ActivitySignupMapper, ActivitySignup> implements ActivitySignupService {

    @Override
    public ActivitySignup findById(Integer id) {
        return getById(id);
    }

    @Override
    public ActivitySignup findByActivityAndElderly(Integer activityId, Integer elderlyId) {
        return lambdaQuery()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getElderlyId, elderlyId)
                .one();
    }

    @Override
    public boolean approveSignup(Integer id) {
        ActivitySignup signup = getById(id);
        if (signup != null && signup.getStatus() == 1) {
            signup.setStatus(2);
            signup.setCheckinTime(LocalDateTime.now());
            return updateById(signup);
        }
        return false;
    }

    @Override
    public boolean rejectSignup(Integer id, String reason) {
        ActivitySignup signup = getById(id);
        if (signup != null && signup.getStatus() == 1) {
            signup.setStatus(3);
            signup.setCancelReason(reason);
            return updateById(signup);
        }
        return false;
    }

    @Override
    public boolean cancelSignup(Integer id, String reason) {
        ActivitySignup signup = getById(id);
        if (signup != null && signup.getStatus() == 1) {
            signup.setStatus(4);
            signup.setCancelReason(reason);
            return updateById(signup);
        }
        return false;
    }

    @Override
    public boolean checkinSignup(Integer id) {
        ActivitySignup signup = getById(id);
        if (signup != null && signup.getStatus() == 2) {
            signup.setStatus(5);
            signup.setCheckinTime(LocalDateTime.now());
            return updateById(signup);
        }
        return false;
    }
}
