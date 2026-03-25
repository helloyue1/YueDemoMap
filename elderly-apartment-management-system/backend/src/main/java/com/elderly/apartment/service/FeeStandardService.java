package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.FeeStandard;

import java.util.List;

public interface FeeStandardService extends IService<FeeStandard> {

    List<FeeStandard> getActiveStandards();

    FeeStandard getStandardByTypeAndRoom(Long feeTypeId, Integer roomType);
}
