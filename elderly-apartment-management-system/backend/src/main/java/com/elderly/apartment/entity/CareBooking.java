package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("care_booking")
public class CareBooking {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long elderlyId;

    private String elderlyName;

    private String roomNumber;

    private String phone;

    private Long serviceId;

    private String serviceName;

    private String serviceType;

    private LocalDate appointmentDate;

    private String appointmentTime;

    private BigDecimal price;

    private String paymentStatus;

    private String status;

    private Long nurseId;

    private String nurseName;

    private String remark;

    private Boolean isPackage;

    private String familyName;

    private String familyPhone;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
