package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.ServiceItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ServiceItemMapper extends BaseMapper<ServiceItem> {

    @Select("SELECT si.*, sc.name as category_name FROM service_item si " +
            "LEFT JOIN service_category sc ON si.category_id = sc.id " +
            "WHERE si.category_id = #{categoryId} AND si.status = 1 AND si.deleted = 0 " +
            "ORDER BY si.sort_order")
    List<ServiceItem> selectByCategoryId(@Param("categoryId") Long categoryId);

    @Select("SELECT si.*, sc.name as category_name FROM service_item si " +
            "LEFT JOIN service_category sc ON si.category_id = sc.id " +
            "WHERE si.status = 1 AND si.deleted = 0 ORDER BY si.category_id, si.sort_order")
    List<ServiceItem> selectAllActiveItems();

    @Select("SELECT si.*, sc.name as category_name FROM service_item si " +
            "LEFT JOIN service_category sc ON si.category_id = sc.id " +
            "WHERE si.id = #{id} AND si.deleted = 0")
    ServiceItem selectItemWithCategory(@Param("id") Long id);
}
