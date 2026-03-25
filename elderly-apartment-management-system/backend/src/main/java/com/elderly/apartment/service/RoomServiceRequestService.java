package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.RoomServiceRequest;

import java.util.List;
import java.util.Map;

public interface RoomServiceRequestService extends IService<RoomServiceRequest> {

    RoomServiceRequest getDetailById(Integer id);

    Page<RoomServiceRequest> getPageWithDetail(Integer page, Integer size, Integer roomId,
                                                  Integer elderlyId, Integer userId,
                                                  String serviceType, String status,
                                                  String urgency, String startDate, String endDate);

    String generateRequestNo();

    boolean assignHandler(Integer id, Integer handlerId, String handlerName);

    boolean startHandle(Integer id, String notes);

    boolean completeRequest(Integer id, String notes);

    boolean cancelRequest(Integer id, String reason);

    List<Map<String, Object>> getTypeStats();

    List<Map<String, Object>> getStatusStats();

    Integer getPendingCount();

    List<RoomServiceRequest> getRecentByRoomId(Integer roomId, Integer limit);
}
