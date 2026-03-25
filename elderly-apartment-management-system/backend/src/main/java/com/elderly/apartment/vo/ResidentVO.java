package com.elderly.apartment.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 住客视图对象 - 关联elderly和user表
 */
@Data
public class ResidentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 老人ID（elderly.id）
     */
    private Integer id;

    /**
     * 关联的用户ID（user.id）
     */
    private Integer userId;

    /**
     * 真实姓名（优先从user.real_name获取，其次从elderly.name获取）
     */
    private String realName;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号（优先从user.phone获取，其次从elderly.phone获取）
     */
    private String phone;

    /**
     * 房间号
     */
    private String roomNumber;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 紧急联系人电话
     */
    private String emergencyPhone;

    /**
     * 健康状况
     */
    private String healthStatus;

    /**
     * 状态
     */
    private Integer status;
}
