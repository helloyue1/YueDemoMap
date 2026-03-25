package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.FeeStandard;
import com.elderly.apartment.mapper.FeeStandardMapper;
import com.elderly.apartment.service.FeeStandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeeStandardServiceImpl extends ServiceImpl<FeeStandardMapper, FeeStandard> implements FeeStandardService {

    @Autowired
    private FeeStandardMapper feeStandardMapper;

    @Override
    public List<FeeStandard> getActiveStandards() {
        return feeStandardMapper.selectActiveStandards();
    }

    @Override
    public FeeStandard getStandardByTypeAndRoom(Long feeTypeId, Integer roomType) {
        return feeStandardMapper.selectByFeeTypeAndRoomType(feeTypeId, roomType);
    }
}
