package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.FeeStandard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeeStandardMapper extends BaseMapper<FeeStandard> {

    @Select("SELECT fs.*, ft.name as fee_type_name FROM fee_standard fs " +
            "LEFT JOIN fee_type ft ON fs.fee_type_id = ft.id " +
            "WHERE fs.status = 1 AND fs.effective_date <= CURDATE() " +
            "AND (fs.expiry_date IS NULL OR fs.expiry_date >= CURDATE())")
    List<FeeStandard> selectActiveStandards();

    @Select("SELECT * FROM fee_standard WHERE fee_type_id = #{feeTypeId} " +
            "AND status = 1 AND effective_date <= CURDATE() " +
            "AND (expiry_date IS NULL OR expiry_date >= CURDATE()) " +
            "ORDER BY amount ASC LIMIT 1")
    FeeStandard selectByFeeTypeAndRoomType(@Param("feeTypeId") Long feeTypeId, @Param("roomType") Integer roomType);
}
