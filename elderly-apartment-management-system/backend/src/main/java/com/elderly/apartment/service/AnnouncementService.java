package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.Announcement;

import java.util.Map;

public interface AnnouncementService extends IService<Announcement> {

    Map<String, Object> getStatistics();
}
