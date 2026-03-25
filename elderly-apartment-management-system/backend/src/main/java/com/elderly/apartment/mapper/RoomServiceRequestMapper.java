package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.entity.RoomServiceRequest;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface RoomServiceRequestMapper extends BaseMapper<RoomServiceRequest> {

    @Select("SELECT r.*, rm.room_number, u2.real_name as elderly_name, u.username as user_name, " +
            "CASE WHEN re.id IS NOT NULL THEN 1 ELSE 0 END as evaluated " +
            "FROM room_service_request r " +
            "LEFT JOIN room rm ON r.room_id = rm.id " +
            "LEFT JOIN user u2 ON r.elderly_id = u2.id " +
            "LEFT JOIN user u ON r.user_id = u.id " +
            "LEFT JOIN room_service_evaluation re ON r.id = re.request_id AND re.deleted = 0 " +
            "WHERE r.deleted = 0 AND r.id = #{id}")
    RoomServiceRequest selectDetailById(@Param("id") Integer id);

    @Select("<script>" +
            "SELECT r.*, rm.room_number, u2.real_name as elderly_name, u.username as user_name, " +
            "CASE WHEN re.id IS NOT NULL THEN 1 ELSE 0 END as evaluated, " +
            "re.rating as evaluation_rating, re.content as evaluation_content " +
            "FROM room_service_request r " +
            "LEFT JOIN room rm ON r.room_id = rm.id " +
            "LEFT JOIN user u2 ON r.elderly_id = u2.id " +
            "LEFT JOIN user u ON r.user_id = u.id " +
            "LEFT JOIN room_service_evaluation re ON r.id = re.request_id AND re.deleted = 0 " +
            "WHERE r.deleted = 0 " +
            "<if test='roomId != null'> AND r.room_id = #{roomId} </if>" +
            "<if test='elderlyId != null'> AND r.elderly_id = #{elderlyId} </if>" +
            "<if test='userId != null'> AND r.user_id = #{userId} </if>" +
            "<if test='serviceType != null and serviceType != \"\"'> AND r.service_type = #{serviceType} </if>" +
            "<if test='status != null and status != \"\"'> AND r.status = #{status} </if>" +
            "<if test='urgency != null and urgency != \"\"'> AND r.urgency = #{urgency} </if>" +
            "<if test='startDate != null'> AND DATE(r.create_time) &gt;= #{startDate} </if>" +
            "<if test='endDate != null'> AND DATE(r.create_time) &lt;= #{endDate} </if>" +
            "ORDER BY r.create_time DESC" +
            "</script>")
    IPage<RoomServiceRequest> selectPageWithDetail(Page<RoomServiceRequest> page,
                                                     @Param("roomId") Integer roomId,
                                                     @Param("elderlyId") Integer elderlyId,
                                                     @Param("userId") Integer userId,
                                                     @Param("serviceType") String serviceType,
                                                     @Param("status") String status,
                                                     @Param("urgency") String urgency,
                                                     @Param("startDate") String startDate,
                                                     @Param("endDate") String endDate);

    @Select("SELECT service_type as type, COUNT(*) as count " +
            "FROM room_service_request " +
            "WHERE deleted = 0 AND create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
            "GROUP BY service_type")
    List<Map<String, Object>> selectTypeStats();

    @Select("SELECT status, COUNT(*) as count " +
            "FROM room_service_request " +
            "WHERE deleted = 0 " +
            "GROUP BY status")
    List<Map<String, Object>> selectStatusStats();

    @Select("SELECT COUNT(*) FROM room_service_request WHERE deleted = 0 AND status = 'PENDING'")
    Integer selectPendingCount();
}
