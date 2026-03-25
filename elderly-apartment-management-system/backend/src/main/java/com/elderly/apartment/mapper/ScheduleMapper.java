package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

    @Select("SELECT * FROM schedule WHERE elderly_id = #{elderlyId} AND schedule_date = #{date} AND deleted = 0 ORDER BY start_time")
    List<Schedule> selectByElderlyIdAndDate(@Param("elderlyId") Long elderlyId, @Param("date") LocalDate date);

    @Select("SELECT * FROM schedule WHERE elderly_id = #{elderlyId} AND schedule_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 ORDER BY schedule_date, start_time")
    List<Schedule> selectByElderlyIdAndDateRange(@Param("elderlyId") Long elderlyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 查询需要提醒的日程
     * 查询条件：
     * 1. 日程日期为今天
     * 2. 开始时间在当前时间和未来5分钟之间
     * 3. 提醒时间不为0（表示需要提醒）
     * 4. 日程状态正常
     * 5. 未删除
     */
    @Select("SELECT * FROM schedule WHERE schedule_date = #{today} " +
            "AND start_time BETWEEN #{startTime} AND #{endTime} " +
            "AND remind_minutes > 0 " +
            "AND status = 1 " +
            "AND deleted = 0 " +
            "ORDER BY start_time")
    List<Schedule> selectRemindableSchedules(@Param("today") LocalDate today,
                                              @Param("startTime") java.time.LocalTime startTime,
                                              @Param("endTime") java.time.LocalTime endTime);
}
