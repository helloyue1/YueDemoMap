package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.RoomServiceEvaluation;
import com.elderly.apartment.entity.RoomServiceRequest;
import com.elderly.apartment.mapper.RoomServiceEvaluationMapper;
import com.elderly.apartment.service.RoomServiceEvaluationService;
import com.elderly.apartment.service.RoomServiceRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RoomServiceEvaluationServiceImpl extends ServiceImpl<RoomServiceEvaluationMapper, RoomServiceEvaluation>
        implements RoomServiceEvaluationService {

    @Autowired
    private RoomServiceRequestService requestService;

    @Override
    public RoomServiceEvaluation getDetailById(Integer id) {
        return baseMapper.selectDetailById(id);
    }

    @Override
    public Page<RoomServiceEvaluation> getPageWithDetail(Integer page, Integer size, Integer roomId,
                                                          Integer elderlyId, Integer requestId,
                                                          Integer minRating) {
        Page<RoomServiceEvaluation> pageParam = new Page<>(page, size);
        IPage<RoomServiceEvaluation> result = baseMapper.selectPageWithDetail(pageParam, roomId, elderlyId, requestId, minRating);
        return (Page<RoomServiceEvaluation>) result;
    }

    @Override
    @Transactional
    public RoomServiceEvaluation createEvaluation(RoomServiceEvaluation evaluation) {
        // 检查服务申请是否存在且已完成
        RoomServiceRequest request = requestService.getById(evaluation.getRequestId());
        if (request == null) {
            throw new RuntimeException("服务申请不存在");
        }
        if (!"COMPLETED".equals(request.getStatus())) {
            throw new RuntimeException("服务未完成，无法评价");
        }

        // 检查是否已评价
        RoomServiceEvaluation existing = lambdaQuery()
                .eq(RoomServiceEvaluation::getRequestId, evaluation.getRequestId())
                .one();
        if (existing != null) {
            throw new RuntimeException("该服务已评价，不能重复评价");
        }

        // 设置关联信息
        evaluation.setRoomId(request.getRoomId());
        evaluation.setElderlyId(request.getElderlyId());

        save(evaluation);
        return evaluation;
    }

    @Override
    public Map<String, Object> getRoomRatingStats(Integer roomId) {
        return baseMapper.selectRoomRatingStats(roomId);
    }

    @Override
    public List<Map<String, Object>> getRoomRatingDistribution(Integer roomId) {
        return baseMapper.selectRoomRatingDistribution(roomId);
    }

    @Override
    public Map<String, Object> getOverallRatingStats() {
        return baseMapper.selectOverallRatingStats();
    }
}
