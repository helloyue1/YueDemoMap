package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.RoomServiceEvaluation;

import java.util.List;
import java.util.Map;

public interface RoomServiceEvaluationService extends IService<RoomServiceEvaluation> {

    RoomServiceEvaluation getDetailById(Integer id);

    Page<RoomServiceEvaluation> getPageWithDetail(Integer page, Integer size, Integer roomId,
                                                     Integer elderlyId, Integer requestId,
                                                     Integer minRating);

    RoomServiceEvaluation createEvaluation(RoomServiceEvaluation evaluation);

    Map<String, Object> getRoomRatingStats(Integer roomId);

    List<Map<String, Object>> getRoomRatingDistribution(Integer roomId);

    Map<String, Object> getOverallRatingStats();
}
