package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.entity.FeePayment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FeePaymentMapper extends BaseMapper<FeePayment> {

    Page<FeePayment> selectPaymentList(Page<FeePayment> page,
                                        @Param("elderlyName") String elderlyName,
                                        @Param("startDate") String startDate,
                                        @Param("endDate") String endDate);
}
