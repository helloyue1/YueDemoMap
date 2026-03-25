package com.elderly.apartment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ElderlyDTO {
    private Integer id;
    private Integer userId;
    private String name;
    private Integer gender;
    private String birthday;
    private Integer age;
    private String nationality;
    private String idCard;
    private String phone;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private Integer roomId;
    private String roomNumber;
    private String checkInDate;
    private Integer healthStatus;
    private Integer status;
    private String notes;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
