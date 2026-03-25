package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.ActivityFeedback;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ActivityFeedbackService extends IService<ActivityFeedback> {

    Map<String, Object> getActivityFeedbackStats(Integer activityId);

    List<Map<String, Object>> getScoreDistribution(Integer activityId);

    BigDecimal calculateOverallScore(Integer activityId);
}
