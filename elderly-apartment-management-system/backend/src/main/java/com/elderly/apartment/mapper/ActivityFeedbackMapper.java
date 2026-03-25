package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.ActivityFeedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface ActivityFeedbackMapper extends BaseMapper<ActivityFeedback> {

    @Select("SELECT AVG(satisfaction_score) as avgScore, COUNT(*) as count FROM activity_feedback WHERE activity_id = #{activityId} AND deleted = 0")
    Map<String, Object> selectAvgScoreByActivityId(@Param("activityId") Integer activityId);

    @Select("SELECT satisfaction_score as score, COUNT(*) as count FROM activity_feedback WHERE activity_id = #{activityId} AND deleted = 0 GROUP BY satisfaction_score")
    List<Map<String, Object>> selectScoreDistribution(@Param("activityId") Integer activityId);
}
