package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.entity.RoomServiceEvaluation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface RoomServiceEvaluationMapper extends BaseMapper<RoomServiceEvaluation> {

    @Select("SELECT e.*, r.request_no, r.service_type, rm.room_number, u2.real_name as elderly_name, u.username as user_name " +
            "FROM room_service_evaluation e " +
            "LEFT JOIN room_service_request r ON e.request_id = r.id " +
            "LEFT JOIN room rm ON e.room_id = rm.id " +
            "LEFT JOIN user u2 ON e.elderly_id = u2.id " +
            "LEFT JOIN user u ON e.user_id = u.id " +
            "WHERE e.deleted = 0 AND e.id = #{id}")
    RoomServiceEvaluation selectDetailById(@Param("id") Integer id);

    @Select("<script>" +
            "SELECT e.*, r.request_no, r.service_type, rm.room_number, u2.real_name as elderly_name, u.username as user_name " +
            "FROM room_service_evaluation e " +
            "LEFT JOIN room_service_request r ON e.request_id = r.id " +
            "LEFT JOIN room rm ON e.room_id = rm.id " +
            "LEFT JOIN user u2 ON e.elderly_id = u2.id " +
            "LEFT JOIN user u ON e.user_id = u.id " +
            "WHERE e.deleted = 0 " +
            "<if test='roomId != null'> AND e.room_id = #{roomId} </if>" +
            "<if test='elderlyId != null'> AND e.elderly_id = #{elderlyId} </if>" +
            "<if test='requestId != null'> AND e.request_id = #{requestId} </if>" +
            "<if test='minRating != null'> AND e.rating &gt;= #{minRating} </if>" +
            "ORDER BY e.create_time DESC" +
            "</script>")
    IPage<RoomServiceEvaluation> selectPageWithDetail(Page<RoomServiceEvaluation> page,
                                                       @Param("roomId") Integer roomId,
                                                       @Param("elderlyId") Integer elderlyId,
                                                       @Param("requestId") Integer requestId,
                                                       @Param("minRating") Integer minRating);

    @Select("SELECT AVG(rating) as avg_rating, COUNT(*) as total_count " +
            "FROM room_service_evaluation " +
            "WHERE deleted = 0 AND room_id = #{roomId}")
    Map<String, Object> selectRoomRatingStats(@Param("roomId") Integer roomId);

    @Select("SELECT rating, COUNT(*) as count " +
            "FROM room_service_evaluation " +
            "WHERE deleted = 0 AND room_id = #{roomId} " +
            "GROUP BY rating ORDER BY rating DESC")
    List<Map<String, Object>> selectRoomRatingDistribution(@Param("roomId") Integer roomId);

    @Select("SELECT AVG(rating) as avg_rating, COUNT(*) as total_count, " +
            "SUM(CASE WHEN rating >= 4 THEN 1 ELSE 0 END) as good_count, " +
            "SUM(CASE WHEN rating <= 2 THEN 1 ELSE 0 END) as bad_count " +
            "FROM room_service_evaluation " +
            "WHERE deleted = 0")
    Map<String, Object> selectOverallRatingStats();
}
