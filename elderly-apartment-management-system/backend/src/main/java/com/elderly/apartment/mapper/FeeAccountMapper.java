package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.FeeAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface FeeAccountMapper extends BaseMapper<FeeAccount> {

    @Select("SELECT fa.*, u.real_name as elderly_name, u.room_number " +
            "FROM fee_account fa " +
            "LEFT JOIN user u ON fa.elderly_id = u.id " +
            "WHERE fa.elderly_id = #{elderlyId}")
    FeeAccount selectByElderlyId(@Param("elderlyId") Integer elderlyId);

    @Update("UPDATE fee_account SET balance = balance + #{amount}, " +
            "total_paid = total_paid + #{amount} " +
            "WHERE elderly_id = #{elderlyId}")
    int addBalance(@Param("elderlyId") Integer elderlyId, @Param("amount") BigDecimal amount);

    @Update("UPDATE fee_account SET balance = balance - #{amount}, " +
            "total_consumed = total_consumed + #{amount} " +
            "WHERE elderly_id = #{elderlyId}")
    int deductBalance(@Param("elderlyId") Integer elderlyId, @Param("amount") BigDecimal amount);
}
