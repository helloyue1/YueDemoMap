package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.ServiceCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ServiceCategoryMapper extends BaseMapper<ServiceCategory> {

    @Select("SELECT * FROM service_category WHERE status = 1 AND deleted = 0 ORDER BY sort_order")
    List<ServiceCategory> selectActiveCategories();
}
