package com.elderly.apartment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.vo.ResidentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT r.id, r.name, r.code FROM role r " +
            "JOIN user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<com.elderly.apartment.entity.Role> findRolesByUserId(Integer userId);



    /**
     * 查询住客列表（从user表查询user_type=2的老人）
     */
    @Select("SELECT " +
            "  u.id, " +
            "  u.id as userId, " +
            "  u.real_name as realName, " +
            "  u.username, " +
            "  u.phone, " +
            "  u.room_number as roomNumber, " +
            "  u.gender, " +
            "  u.birthday, " +
            "  u.age, " +
            "  u.avatar, " +
            "  u.id_card as idCard, " +
            "  u.emergency_contact as emergencyContact, " +
            "  u.emergency_phone as emergencyPhone, " +
            "  u.health_status as healthStatus, " +
            "  u.status " +
            "FROM user u " +
            "WHERE u.user_type = 2 AND u.deleted = 0 " +
            "ORDER BY u.id DESC")
    List<ResidentVO> selectResidentList();

    /**
     * 根据ID查询住客详情
     */
    @Select("SELECT " +
            "  u.id, " +
            "  u.id as userId, " +
            "  u.real_name as realName, " +
            "  u.username, " +
            "  u.phone, " +
            "  u.room_number as roomNumber, " +
            "  u.gender, " +
            "  u.birthday, " +
            "  u.age, " +
            "  u.avatar, " +
            "  u.id_card as idCard, " +
            "  u.emergency_contact as emergencyContact, " +
            "  u.emergency_phone as emergencyPhone, " +
            "  u.health_status as healthStatus, " +
            "  u.status " +
            "FROM user u " +
            "WHERE u.id = #{id} AND u.user_type = 2 AND u.deleted = 0")
    ResidentVO selectResidentById(@Param("id") Integer id);

    /**
     * 根据房间号查询住客列表
     */
    @Select("SELECT " +
            "  u.id, " +
            "  u.id as userId, " +
            "  u.real_name as realName, " +
            "  u.username, " +
            "  u.phone, " +
            "  u.room_number as roomNumber, " +
            "  u.gender, " +
            "  u.birthday, " +
            "  u.age, " +
            "  u.avatar, " +
            "  u.id_card as idCard, " +
            "  u.emergency_contact as emergencyContact, " +
            "  u.emergency_phone as emergencyPhone, " +
            "  u.health_status as healthStatus, " +
            "  u.status " +
            "FROM user u " +
            "WHERE u.room_number = #{roomNumber} AND u.user_type = 2 AND u.deleted = 0 " +
            "ORDER BY u.id DESC")
    List<ResidentVO> selectResidentByRoomNumber(@Param("roomNumber") String roomNumber);

    /**
     * 搜索住客（按姓名或手机号）
     */
    @Select("SELECT " +
            "  u.id, " +
            "  u.id as userId, " +
            "  u.real_name as realName, " +
            "  u.username, " +
            "  u.phone, " +
            "  u.room_number as roomNumber, " +
            "  u.gender, " +
            "  u.birthday, " +
            "  u.age, " +
            "  u.avatar, " +
            "  u.id_card as idCard, " +
            "  u.emergency_contact as emergencyContact, " +
            "  u.emergency_phone as emergencyPhone, " +
            "  u.health_status as healthStatus, " +
            "  u.status " +
            "FROM user u " +
            "WHERE u.user_type = 2 AND u.deleted = 0 " +
            "  AND (u.real_name LIKE CONCAT('%', #{keyword}, '%') " +
            "    OR u.phone LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY u.id DESC")
    List<ResidentVO> searchResidents(@Param("keyword") String keyword);
}
