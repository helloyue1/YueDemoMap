package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    @Select("SELECT " +
            "COUNT(*) as total, " +
            "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as published, " +
            "SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) as draft, " +
            "SUM(CASE WHEN top = 1 THEN 1 ELSE 0 END) as top " +
            "FROM announcement WHERE deleted = 0")
    Map<String, Object> selectStatistics();
}
