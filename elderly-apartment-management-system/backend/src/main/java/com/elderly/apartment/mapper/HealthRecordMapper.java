package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 健康档案数据访问层
 */
@Mapper
public interface HealthRecordMapper extends BaseMapper<HealthRecord> {
}
