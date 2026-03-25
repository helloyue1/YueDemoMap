package com.elderly.apartment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.EmergencyContact;

import java.util.List;

public interface EmergencyContactService extends IService<EmergencyContact> {

    IPage<EmergencyContact> getEmergencyContactPage(IPage<EmergencyContact> page, String name, String contactType, Integer status);

    List<EmergencyContact> getActiveContacts();

    List<EmergencyContact> getAllContacts();

    List<EmergencyContact> getContactsByType(String type);

    boolean createContact(EmergencyContact contact);

    boolean updateContact(EmergencyContact contact);

    boolean deleteContact(Integer id);

    boolean toggleStatus(Integer id);
}
