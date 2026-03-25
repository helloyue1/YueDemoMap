package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService extends IService<Schedule> {

    List<Schedule> getSchedulesByElderlyIdAndDate(Long elderlyId, LocalDate date);

    List<Schedule> getSchedulesByElderlyIdAndDateRange(Long elderlyId, LocalDate startDate, LocalDate endDate);

    boolean createSchedule(Schedule schedule);

    boolean updateSchedule(Schedule schedule);

    boolean deleteSchedule(Long id);
}
