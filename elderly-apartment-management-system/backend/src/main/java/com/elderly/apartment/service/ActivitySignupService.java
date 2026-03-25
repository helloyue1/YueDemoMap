package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.ActivitySignup;

public interface ActivitySignupService extends IService<ActivitySignup> {

    ActivitySignup findById(Integer id);

    ActivitySignup findByActivityAndElderly(Integer activityId, Integer elderlyId);

    boolean approveSignup(Integer id);

    boolean rejectSignup(Integer id, String reason);

    boolean cancelSignup(Integer id, String reason);

    boolean checkinSignup(Integer id);
}
