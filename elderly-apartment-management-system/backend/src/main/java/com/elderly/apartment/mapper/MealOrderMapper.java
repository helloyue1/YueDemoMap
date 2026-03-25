package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.entity.MealOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MealOrderMapper extends BaseMapper<MealOrder> {

    @Select("SELECT mo.*, u.real_name as elderly_name, u.room_number " +
            "FROM meal_order mo " +
            "LEFT JOIN user u ON mo.elderly_id = u.id " +
            "WHERE mo.order_date = #{date} AND mo.meal_type = #{mealType} " +
            "ORDER BY mo.create_time DESC")
    List<MealOrder> selectByDateAndType(@Param("date") LocalDate date, @Param("mealType") Integer mealType);

    @Select("SELECT mo.*, u.real_name as elderly_name, u.room_number " +
            "FROM meal_order mo " +
            "LEFT JOIN user u ON mo.elderly_id = u.id " +
            "WHERE mo.elderly_id = #{elderlyId} " +
            "ORDER BY mo.order_date DESC, mo.meal_type ASC")
    List<MealOrder> selectByElderlyId(@Param("elderlyId") Long elderlyId);

    IPage<MealOrder> selectOrderList(Page<MealOrder> page,
                                     @Param("elderlyId") Long elderlyId,
                                     @Param("elderlyName") String elderlyName,
                                     @Param("roomNumber") String roomNumber,
                                     @Param("orderDate") String orderDate,
                                     @Param("mealType") Integer mealType,
                                     @Param("status") Integer status);

    @Select("SELECT mo.*, u.real_name as elderly_name, u.room_number " +
            "FROM meal_order mo " +
            "LEFT JOIN user u ON mo.elderly_id = u.id " +
            "WHERE mo.id = #{id}")
    MealOrder selectByIdWithElderly(@Param("id") Long id);
}
