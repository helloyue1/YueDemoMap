package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.FamilyMember;
import com.elderly.apartment.mapper.FamilyMemberMapper;
import com.elderly.apartment.service.FamilyMemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class FamilyMemberServiceImpl extends ServiceImpl<FamilyMemberMapper, FamilyMember> implements FamilyMemberService {

    @Override
    public List<FamilyMember> findByElderlyId(Long elderlyId) {
        return baseMapper.selectByElderlyId(elderlyId);
    }

    @Override
    public FamilyMember findByIdWithElderly(Long id) {
        return baseMapper.selectByIdWithElderly(id);
    }

    @Override
    public Page<FamilyMember> findPage(Page<FamilyMember> page, String name, String phone, Long elderlyId, String relationship) {
        LambdaQueryWrapper<FamilyMember> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(name)) {
            wrapper.like(FamilyMember::getName, name);
        }

        if (StringUtils.hasText(phone)) {
            wrapper.like(FamilyMember::getPhone, phone);
        }

        if (elderlyId != null) {
            wrapper.eq(FamilyMember::getElderlyId, elderlyId);
        }

        if (StringUtils.hasText(relationship)) {
            wrapper.eq(FamilyMember::getRelationship, relationship);
        }

        wrapper.eq(FamilyMember::getDeleted, 0);
        wrapper.orderByDesc(FamilyMember::getCreateTime);

        Page<FamilyMember> resultPage = page(page, wrapper);

        for (FamilyMember member : resultPage.getRecords()) {
            FamilyMember fullInfo = baseMapper.selectByIdWithElderly(member.getId());
            if (fullInfo != null) {
                member.setElderlyName(fullInfo.getElderlyName());
                member.setRoomNumber(fullInfo.getRoomNumber());
            }
        }

        return resultPage;
    }

    @Override
    public List<FamilyMember> findEmergencyContacts(Long elderlyId) {
        LambdaQueryWrapper<FamilyMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FamilyMember::getElderlyId, elderlyId)
                .eq(FamilyMember::getIsEmergencyContact, 1)
                .eq(FamilyMember::getStatus, 1)
                .eq(FamilyMember::getDeleted, 0);
        return list(wrapper);
    }

    @Override
    public List<FamilyMember> findPrimaryContacts(Long elderlyId) {
        LambdaQueryWrapper<FamilyMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FamilyMember::getElderlyId, elderlyId)
                .eq(FamilyMember::getIsPrimaryContact, 1)
                .eq(FamilyMember::getStatus, 1)
                .eq(FamilyMember::getDeleted, 0);
        return list(wrapper);
    }

    @Override
    public boolean setEmergencyContact(Long id, Boolean isEmergency) {
        FamilyMember member = getById(id);
        if (member == null) {
            return false;
        }
        member.setIsEmergencyContact(isEmergency != null && isEmergency ? 1 : 0);
        return updateById(member);
    }

    @Override
    public boolean setPrimaryContact(Long id, Boolean isPrimary) {
        FamilyMember member = getById(id);
        if (member == null) {
            return false;
        }

        if (isPrimary != null && isPrimary) {
            LambdaQueryWrapper<FamilyMember> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FamilyMember::getElderlyId, member.getElderlyId())
                    .eq(FamilyMember::getIsPrimaryContact, 1)
                    .eq(FamilyMember::getDeleted, 0);
            List<FamilyMember> existingPrimary = list(wrapper);
            for (FamilyMember existing : existingPrimary) {
                existing.setIsPrimaryContact(0);
                updateById(existing);
            }
        }

        member.setIsPrimaryContact(isPrimary != null && isPrimary ? 1 : 0);
        return updateById(member);
    }
}
