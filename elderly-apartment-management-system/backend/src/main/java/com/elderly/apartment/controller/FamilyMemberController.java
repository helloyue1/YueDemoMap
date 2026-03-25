package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.FamilyMember;
import com.elderly.apartment.service.FamilyMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/family")
public class FamilyMemberController {

    @Autowired
    private FamilyMemberService familyMemberService;

    @GetMapping
    public Result<Page<FamilyMember>> getFamilyMemberList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Long elderlyId,
            @RequestParam(required = false) String relationship) {

        Page<FamilyMember> pageParam = new Page<>(page, size);
        Page<FamilyMember> result = familyMemberService.findPage(pageParam, name, phone, elderlyId, relationship);
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<Page<FamilyMember>> getFamilyMemberList2(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Long elderlyId,
            @RequestParam(required = false) String relationship) {

        Page<FamilyMember> pageParam = new Page<>(page, size);
        Page<FamilyMember> result = familyMemberService.findPage(pageParam, name, phone, elderlyId, relationship);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<FamilyMember> getFamilyMemberById(@PathVariable Long id) {
        FamilyMember member = familyMemberService.findByIdWithElderly(id);
        if (member == null) {
            return Result.error("家属信息不存在");
        }
        return Result.success(member);
    }

    @GetMapping("/elderly/{elderlyId}")
    public Result<List<FamilyMember>> getFamilyMembersByElderlyId(@PathVariable Long elderlyId) {
        List<FamilyMember> members = familyMemberService.findByElderlyId(elderlyId);
        return Result.success(members);
    }

    @PostMapping
    public Result<FamilyMember> createFamilyMember(@RequestBody FamilyMember familyMember) {
        if (familyMember.getStatus() == null) {
            familyMember.setStatus(1);
        }
        if (familyMember.getIsEmergencyContact() == null) {
            familyMember.setIsEmergencyContact(0);
        }
        if (familyMember.getIsPrimaryContact() == null) {
            familyMember.setIsPrimaryContact(0);
        }

        boolean success = familyMemberService.save(familyMember);
        if (success) {
            return Result.success(familyMember);
        }
        return Result.error("添加家属失败");
    }

    @PutMapping("/{id}")
    public Result<FamilyMember> updateFamilyMember(@PathVariable Long id, @RequestBody FamilyMember familyMember) {
        FamilyMember existing = familyMemberService.getById(id);
        if (existing == null) {
            return Result.error("家属信息不存在");
        }

        familyMember.setId(id);
        boolean success = familyMemberService.updateById(familyMember);
        if (success) {
            return Result.success(familyMemberService.getById(id));
        }
        return Result.error("更新家属信息失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteFamilyMember(@PathVariable Long id) {
        FamilyMember existing = familyMemberService.getById(id);
        if (existing == null) {
            return Result.error("家属信息不存在");
        }

        boolean success = familyMemberService.removeById(id);
        if (success) {
            return Result.success(null);
        }
        return Result.error("删除家属失败");
    }

    @GetMapping("/elderly/{elderlyId}/emergency")
    public Result<List<FamilyMember>> getEmergencyContacts(@PathVariable Long elderlyId) {
        List<FamilyMember> members = familyMemberService.findEmergencyContacts(elderlyId);
        return Result.success(members);
    }

    @GetMapping("/elderly/{elderlyId}/primary")
    public Result<List<FamilyMember>> getPrimaryContacts(@PathVariable Long elderlyId) {
        List<FamilyMember> members = familyMemberService.findPrimaryContacts(elderlyId);
        return Result.success(members);
    }

    @PutMapping("/{id}/emergency")
    public Result<Void> setEmergencyContact(
            @PathVariable Long id,
            @RequestParam Boolean isEmergency) {
        boolean success = familyMemberService.setEmergencyContact(id, isEmergency);
        if (success) {
            return Result.success(null);
        }
        return Result.error("设置紧急联系人失败");
    }

    @PutMapping("/{id}/primary")
    public Result<Void> setPrimaryContact(
            @PathVariable Long id,
            @RequestParam Boolean isPrimary) {
        boolean success = familyMemberService.setPrimaryContact(id, isPrimary);
        if (success) {
            return Result.success(null);
        }
        return Result.error("设置主要联系人失败");
    }

    @GetMapping("/relationships")
    public Result<List<RelationshipOption>> getRelationshipOptions() {
        List<RelationshipOption> options = List.of(
                new RelationshipOption("spouse", "配偶"),
                new RelationshipOption("child", "子女"),
                new RelationshipOption("sibling", "兄弟姐妹"),
                new RelationshipOption("parent", "父母"),
                new RelationshipOption("grandchild", "孙辈"),
                new RelationshipOption("other", "其他")
        );
        return Result.success(options);
    }

    public static class RelationshipOption {
        private String value;
        private String label;

        public RelationshipOption(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
