package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.ActivityFeedback;
import com.elderly.apartment.mapper.ActivityFeedbackMapper;
import com.elderly.apartment.service.ActivityFeedbackService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class ActivityFeedbackServiceImpl extends ServiceImpl<ActivityFeedbackMapper, ActivityFeedback> implements ActivityFeedbackService {

    @Override
    public Map<String, Object> getActivityFeedbackStats(Integer activityId) {
        return baseMapper.selectAvgScoreByActivityId(activityId);
    }

    @Override
    public List<Map<String, Object>> getScoreDistribution(Integer activityId) {
        return baseMapper.selectScoreDistribution(activityId);
    }

    @Override
    public BigDecimal calculateOverallScore(Integer activityId) {
        List<ActivityFeedback> feedbacks = lambdaQuery()
                .eq(ActivityFeedback::getActivityId, activityId)
                .eq(ActivityFeedback::getDeleted, 0)
                .list();

        if (feedbacks.isEmpty()) {
            return BigDecimal.ZERO;
        }

        int totalScore = 0;
        int count = 0;

        for (ActivityFeedback feedback : feedbacks) {
            int score = 0;
            int scoreCount = 0;

            if (feedback.getSatisfactionScore() != null) {
                score += feedback.getSatisfactionScore();
                scoreCount++;
            }
            if (feedback.getOrganizationScore() != null) {
                score += feedback.getOrganizationScore();
                scoreCount++;
            }
            if (feedback.getContentScore() != null) {
                score += feedback.getContentScore();
                scoreCount++;
            }
            if (feedback.getStaffScore() != null) {
                score += feedback.getStaffScore();
                scoreCount++;
            }

            if (scoreCount > 0) {
                totalScore += score / scoreCount;
                count++;
            }
        }

        if (count == 0) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(totalScore).divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
    }
}
