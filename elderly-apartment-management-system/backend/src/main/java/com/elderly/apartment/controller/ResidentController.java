package com.elderly.apartment.controller;

import com.elderly.apartment.common.Result;
import com.elderly.apartment.service.ResidentService;
import com.elderly.apartment.vo.ResidentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 住客控制器 - 提供elderly和user表关联查询接口
 */
@Slf4j
@RestController
@RequestMapping("/resident")
public class ResidentController {

    @Autowired
    private ResidentService residentService;

    /**
     * 获取所有住客列表（已关联user表）
     *
     * @return 住客列表
     */
    @GetMapping("/list")
    public Result<List<ResidentVO>> getResidentList() {
        try {
            List<ResidentVO> list = residentService.getResidentList();
            return Result.success(list);
        } catch (Exception e) {
            log.error("获取住客列表失败", e);
            return Result.error("获取住客列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取住客详情
     *
     * @param id 老人ID
     * @return 住客详情
     */
    @GetMapping("/{id}")
    public Result<ResidentVO> getResidentById(@PathVariable Integer id) {
        try {
            ResidentVO resident = residentService.getResidentById(id);
            if (resident == null) {
                return Result.error("住客不存在");
            }
            return Result.success(resident);
        } catch (Exception e) {
            log.error("获取住客详情失败, id: {}", id, e);
            return Result.error("获取住客详情失败: " + e.getMessage());
        }
    }

    /**
     * 根据房间号获取住客列表
     *
     * @param roomNumber 房间号
     * @return 住客列表
     */
    @GetMapping("/room/{roomNumber}")
    public Result<List<ResidentVO>> getResidentsByRoomNumber(@PathVariable String roomNumber) {
        try {
            List<ResidentVO> list = residentService.getResidentsByRoomNumber(roomNumber);
            return Result.success(list);
        } catch (Exception e) {
            log.error("根据房间号获取住客列表失败, roomNumber: {}", roomNumber, e);
            return Result.error("获取住客列表失败: " + e.getMessage());
        }
    }

    /**
     * 搜索住客（按姓名或手机号）
     *
     * @param keyword 搜索关键词
     * @return 住客列表
     */
    @GetMapping("/search")
    public Result<List<ResidentVO>> searchResidents(@RequestParam String keyword) {
        try {
            List<ResidentVO> list = residentService.searchResidents(keyword);
            return Result.success(list);
        } catch (Exception e) {
            log.error("搜索住客失败, keyword: {}", keyword, e);
            return Result.error("搜索住客失败: " + e.getMessage());
        }
    }
}
