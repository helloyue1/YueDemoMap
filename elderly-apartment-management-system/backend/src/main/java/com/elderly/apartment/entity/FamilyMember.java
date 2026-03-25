package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("family_member")
public class FamilyMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("elderly_id")
    private Long elderlyId;

    @TableField("name")
    private String name;

    @TableField("relationship")
    private String relationship;

    @TableField("phone")
    private String phone;

    @TableField("id_card")
    private String idCard;

    @TableField("gender")
    private Integer gender;

    @TableField("address")
    private String address;

    @TableField("work_unit")
    private String workUnit;

    @TableField("is_emergency_contact")
    private Integer isEmergencyContact;

    @TableField("is_primary_contact")
    private Integer isPrimaryContact;

    @TableField("remark")
    private String remark;

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
    private String elderlyName;

    @TableField(exist = false)
    private String roomNumber;
}
