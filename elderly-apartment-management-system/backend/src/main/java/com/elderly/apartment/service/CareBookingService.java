package com.elderly.apartment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.CareBooking;

import java.util.Map;

public interface CareBookingService extends IService<CareBooking> {

    IPage<CareBooking> getBookingPage(Integer page, Integer size, Map<String, Object> params);

    boolean confirmBooking(Long id);

    boolean completeBooking(Long id);

    boolean cancelBooking(Long id);

    boolean assignNurse(Long id, Long nurseId);

    boolean payBooking(Long id, String paymentMethod, Double amount);
}
