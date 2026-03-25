package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.entity.FeeBill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface FeeBillMapper extends BaseMapper<FeeBill> {

    Page<FeeBill> selectBillList(Page<FeeBill> page,
                                  @Param("elderlyName") String elderlyName,
                                  @Param("billMonth") String billMonth,
                                  @Param("status") Integer status);

    @Select("SELECT fb.*, u.real_name as elderly_name, u.room_number " +
            "FROM fee_bill fb " +
            "LEFT JOIN user u ON fb.elderly_id = u.id " +
            "WHERE fb.id = #{id}")
    @Results({
        @Result(column = "elderly_name", property = "elderlyName"),
        @Result(column = "room_number", property = "roomNumber")
    })
    FeeBill selectBillDetail(@Param("id") Long id);

    @Select("SELECT SUM(payable_amount) FROM fee_bill " +
            "WHERE bill_month >= #{startMonth} AND bill_month <= #{endMonth}")
    BigDecimal sumPayableAmount(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth);

    @Select("SELECT SUM(paid_amount) FROM fee_bill " +
            "WHERE bill_month >= #{startMonth} AND bill_month <= #{endMonth}")
    BigDecimal sumPaidAmount(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth);

    @Select("SELECT SUM(payable_amount - paid_amount) FROM fee_bill " +
            "WHERE bill_month >= #{startMonth} AND bill_month <= #{endMonth} " +
            "AND status != 2")
    BigDecimal sumUnpaidAmount(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth);

    List<Map<String, Object>> selectMonthlyStats(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth);

    @Select("SELECT u.id as elderly_id, u.real_name as elderly_name, u.room_number, u.phone, " +
            "SUM(fb.payable_amount - fb.paid_amount) as total_unpaid, " +
            "COUNT(*) as unpaid_months " +
            "FROM fee_bill fb " +
            "LEFT JOIN user u ON fb.elderly_id = u.id " +
            "WHERE fb.status != 2 " +
            "GROUP BY u.id, u.real_name, u.room_number, u.phone " +
            "HAVING SUM(fb.payable_amount - fb.paid_amount) > 0 " +
            "ORDER BY total_unpaid DESC")
    List<Map<String, Object>> selectDebtorList();
}
