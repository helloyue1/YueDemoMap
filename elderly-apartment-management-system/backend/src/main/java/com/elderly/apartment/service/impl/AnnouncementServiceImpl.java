package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.Announcement;
import com.elderly.apartment.mapper.AnnouncementMapper;
import com.elderly.apartment.service.AnnouncementService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Override
    public Map<String, Object> getStatistics() {
        return baseMapper.selectStatistics();
    }
}
