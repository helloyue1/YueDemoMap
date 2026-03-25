package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.RoomServiceRequest;
import com.elderly.apartment.mapper.RoomServiceRequestMapper;
import com.elderly.apartment.service.RoomServiceRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RoomServiceRequestServiceImpl extends ServiceImpl<RoomServiceRequestMapper, RoomServiceRequest>
        implements RoomServiceRequestService {

    @Override
    public RoomServiceRequest getDetailById(Integer id) {
        return baseMapper.selectDetailById(id);
    }

    @Override
    public Page<RoomServiceRequest> getPageWithDetail(Integer page, Integer size, Integer roomId,
                                                       Integer elderlyId, Integer userId,
                                                       String serviceType, String status,
                                                       String urgency, String startDate, String endDate) {
        Page<RoomServiceRequest> pageParam = new Page<>(page, size);
        IPage<RoomServiceRequest> result = baseMapper.selectPageWithDetail(pageParam, roomId, elderlyId, userId,
                serviceType, status, urgency, startDate, endDate);
        return (Page<RoomServiceRequest>) result;
    }

    @Override
    public String generateRequestNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = (int) (Math.random() * 9000) + 1000;
        return "RS" + dateStr + random;
    }

    @Override
    @Transactional
    public boolean assignHandler(Integer id, Integer handlerId, String handlerName) {
        RoomServiceRequest request = getById(id);
        if (request == null) {
            return false;
        }
        request.setHandlerId(handlerId);
        request.setHandlerName(handlerName);
        request.setStatus("ASSIGNED");
        request.setHandleTime(LocalDateTime.now());
        return updateById(request);
    }

    @Override
    @Transactional
    public boolean startHandle(Integer id, String notes) {
        RoomServiceRequest request = getById(id);
        if (request == null) {
            return false;
        }
        request.setStatus("IN_PROGRESS");
        if (notes != null && !notes.isEmpty()) {
            request.setHandleNotes(notes);
        }
        return updateById(request);
    }

    @Override
    @Transactional
    public boolean completeRequest(Integer id, String notes) {
        RoomServiceRequest request = getById(id);
        if (request == null) {
            return false;
        }
        request.setStatus("COMPLETED");
        request.setCompleteTime(LocalDateTime.now());
        if (notes != null && !notes.isEmpty()) {
            request.setHandleNotes(notes);
        }
        return updateById(request);
    }

    @Override
    @Transactional
    public boolean cancelRequest(Integer id, String reason) {
        RoomServiceRequest request = getById(id);
        if (request == null) {
            return false;
        }
        if ("COMPLETED".equals(request.getStatus())) {
            return false;
        }
        request.setStatus("CANCELLED");
        if (reason != null && !reason.isEmpty()) {
            request.setHandleNotes(reason);
        }
        return updateById(request);
    }

    @Override
    public List<Map<String, Object>> getTypeStats() {
        return baseMapper.selectTypeStats();
    }

    @Override
    public List<Map<String, Object>> getStatusStats() {
        return baseMapper.selectStatusStats();
    }

    @Override
    public Integer getPendingCount() {
        return baseMapper.selectPendingCount();
    }

    @Override
    public List<RoomServiceRequest> getRecentByRoomId(Integer roomId, Integer limit) {
        Page<RoomServiceRequest> page = new Page<>(1, limit);
        IPage<RoomServiceRequest> result = baseMapper.selectPageWithDetail(
                page, roomId, null, null, null, null, null, null, null);
        return result.getRecords();
    }
}
