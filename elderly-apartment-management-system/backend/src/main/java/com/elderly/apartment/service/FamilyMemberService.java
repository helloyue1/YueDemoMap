package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.FamilyMember;

import java.util.List;

public interface FamilyMemberService extends IService<FamilyMember> {

    List<FamilyMember> findByElderlyId(Long elderlyId);

    FamilyMember findByIdWithElderly(Long id);

    Page<FamilyMember> findPage(Page<FamilyMember> page, String name, String phone, Long elderlyId, String relationship);

    List<FamilyMember> findEmergencyContacts(Long elderlyId);

    List<FamilyMember> findPrimaryContacts(Long elderlyId);

    boolean setEmergencyContact(Long id, Boolean isEmergency);

    boolean setPrimaryContact(Long id, Boolean isPrimary);
}
