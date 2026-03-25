package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("real_name")
    private String realName;

    @TableField("nickname")
    private String nickname;

    @TableField("avatar")
    private String avatar;

    @TableField("bio")
    private String bio;

    @TableField("phone")
    private String phone;

    @TableField("email")
    private String email;

    @TableField("status")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @TableField(exist = false)
    private List<Role> roles;

    @TableField(exist = false)
    private Integer elderlyId;

    // 老人相关字段
    @TableField("user_type")
    private Integer userType;

    @TableField("gender")
    private Integer gender;

    @TableField("age")
    private Integer age;

    @TableField("birthday")
    private String birthday;

    @TableField("nationality")
    private String nationality;

    @TableField("id_card")
    private String idCard;

    @TableField("address")
    private String address;

    @TableField("health_status")
    private String healthStatus;

    @TableField("emergency_contact")
    private String emergencyContact;

    @TableField("emergency_phone")
    private String emergencyPhone;

    @TableField("room_id")
    private Integer roomId;

    @TableField("room_number")
    private String roomNumber;

    @TableField("check_in_date")
    private String checkInDate;

    @TableField("check_in_time")
    private LocalDateTime checkInTime;

    @TableField("check_out_time")
    private LocalDateTime checkOutTime;

    @TableField("balance")
    private java.math.BigDecimal balance;

    @TableField("notes")
    private String notes;

    /**
     * 判断是否为老人
     */
    public boolean isElderly() {
        return userType != null && userType == 2;
    }
}
