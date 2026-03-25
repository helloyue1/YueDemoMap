package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.EmergencyContact;
import com.elderly.apartment.mapper.EmergencyContactMapper;
import com.elderly.apartment.service.EmergencyContactService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmergencyContactServiceImpl extends ServiceImpl<EmergencyContactMapper, EmergencyContact> implements EmergencyContactService {

    @Override
    public IPage<EmergencyContact> getEmergencyContactPage(IPage<EmergencyContact> page, String name, String contactType, Integer status) {
        LambdaQueryWrapper<EmergencyContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), EmergencyContact::getName, name)
               .eq(StringUtils.hasText(contactType), EmergencyContact::getContactType, contactType)
               .eq(status != null, EmergencyContact::getStatus, status)
               .eq(EmergencyContact::getDeleted, 0)
               .orderByAsc(EmergencyContact::getSortOrder)
               .orderByDesc(EmergencyContact::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public List<EmergencyContact> getActiveContacts() {
        LambdaQueryWrapper<EmergencyContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyContact::getStatus, 1)
               .eq(EmergencyContact::getDeleted, 0)
               .orderByAsc(EmergencyContact::getSortOrder)
               .orderByDesc(EmergencyContact::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<EmergencyContact> getAllContacts() {
        LambdaQueryWrapper<EmergencyContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyContact::getDeleted, 0)
               .orderByAsc(EmergencyContact::getSortOrder)
               .orderByDesc(EmergencyContact::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<EmergencyContact> getContactsByType(String type) {
        LambdaQueryWrapper<EmergencyContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyContact::getContactType, type)
               .eq(EmergencyContact::getStatus, 1)
               .eq(EmergencyContact::getDeleted, 0)
               .orderByAsc(EmergencyContact::getSortOrder);
        return this.list(wrapper);
    }

    @Override
    public boolean createContact(EmergencyContact contact) {
        if (contact.getSortOrder() == null) {
            contact.setSortOrder(0);
        }
        if (contact.getStatus() == null) {
            contact.setStatus(1);
        }
        contact.setCreateTime(LocalDateTime.now());
        contact.setUpdateTime(LocalDateTime.now());
        contact.setDeleted(0);
        return this.save(contact);
    }

    @Override
    public boolean updateContact(EmergencyContact contact) {
        contact.setUpdateTime(LocalDateTime.now());
        return this.updateById(contact);
    }

    @Override
    public boolean deleteContact(Integer id) {
        EmergencyContact contact = this.getById(id);
        if (contact == null) {
            return false;
        }
        contact.setDeleted(1);
        contact.setUpdateTime(LocalDateTime.now());
        return this.updateById(contact);
    }

    @Override
    public boolean toggleStatus(Integer id) {
        EmergencyContact contact = this.getById(id);
        if (contact == null) {
            return false;
        }
        contact.setStatus(contact.getStatus() == 1 ? 0 : 1);
        contact.setUpdateTime(LocalDateTime.now());
        return this.updateById(contact);
    }
}
