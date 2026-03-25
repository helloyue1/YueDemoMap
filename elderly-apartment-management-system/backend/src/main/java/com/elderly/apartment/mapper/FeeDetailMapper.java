package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.FeeDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeeDetailMapper extends BaseMapper<FeeDetail> {

    @Select("SELECT fd.*, ft.name as fee_type_name " +
            "FROM fee_detail fd " +
            "LEFT JOIN fee_type ft ON fd.fee_type_id = ft.id " +
            "WHERE fd.bill_id = #{billId}")
    List<FeeDetail> selectByBillId(@Param("billId") Long billId);
}
