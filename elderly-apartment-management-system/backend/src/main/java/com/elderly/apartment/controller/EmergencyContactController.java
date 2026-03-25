package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.EmergencyContact;
import com.elderly.apartment.service.EmergencyContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emergency-contact")
public class EmergencyContactController {

    @Autowired
    private EmergencyContactService emergencyContactService;

    @GetMapping("/list")
    public Result<List<EmergencyContact>> getAllContacts() {
        List<EmergencyContact> list = emergencyContactService.getAllContacts();
        return Result.success(list);
    }

    @GetMapping("/active")
    public Result<List<EmergencyContact>> getActiveContacts() {
        List<EmergencyContact> list = emergencyContactService.getActiveContacts();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<IPage<EmergencyContact>> getContactPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String contactType,
            @RequestParam(required = false) Integer status) {
        Page<EmergencyContact> pageObj = new Page<>(page, size);
        IPage<EmergencyContact> result = emergencyContactService.getEmergencyContactPage(pageObj, name, contactType, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<EmergencyContact> getContactById(@PathVariable Integer id) {
        EmergencyContact contact = emergencyContactService.getById(id);
        return Result.success(contact);
    }

    @GetMapping("/type/{type}")
    public Result<List<EmergencyContact>> getContactsByType(@PathVariable String type) {
        List<EmergencyContact> list = emergencyContactService.getContactsByType(type);
        return Result.success(list);
    }

    @PostMapping
    public Result<EmergencyContact> createContact(@RequestBody EmergencyContact contact) {
        boolean success = emergencyContactService.createContact(contact);
        if (success) {
            return Result.success("紧急联系人创建成功", contact);
        }
        return Result.error("紧急联系人创建失败");
    }

    @PutMapping("/{id}")
    public Result<EmergencyContact> updateContact(@PathVariable Integer id, @RequestBody EmergencyContact contact) {
        contact.setId(id);
        boolean success = emergencyContactService.updateContact(contact);
        if (success) {
            return Result.success("紧急联系人更新成功", contact);
        }
        return Result.error("紧急联系人更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteContact(@PathVariable Integer id) {
        boolean success = emergencyContactService.deleteContact(id);
        if (success) {
            return Result.success("紧急联系人删除成功", null);
        }
        return Result.error("紧急联系人删除失败");
    }

    @PutMapping("/{id}/toggle")
    public Result<Void> toggleStatus(@PathVariable Integer id) {
        boolean success = emergencyContactService.toggleStatus(id);
        if (success) {
            return Result.success("状态切换成功", null);
        }
        return Result.error("状态切换失败");
    }
}
