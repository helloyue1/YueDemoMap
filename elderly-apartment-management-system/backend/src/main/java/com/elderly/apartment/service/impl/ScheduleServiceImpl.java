package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.Schedule;
import com.elderly.apartment.mapper.ScheduleMapper;
import com.elderly.apartment.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {

    @Override
    public List<Schedule> getSchedulesByElderlyIdAndDate(Long elderlyId, LocalDate date) {
        return baseMapper.selectByElderlyIdAndDate(elderlyId, date);
    }

    @Override
    public List<Schedule> getSchedulesByElderlyIdAndDateRange(Long elderlyId, LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectByElderlyIdAndDateRange(elderlyId, startDate, endDate);
    }

    @Override
    public boolean createSchedule(Schedule schedule) {
        return save(schedule);
    }

    @Override
    public boolean updateSchedule(Schedule schedule) {
        return updateById(schedule);
    }

    @Override
    public boolean deleteSchedule(Long id) {
        return removeById(id);
    }
}
