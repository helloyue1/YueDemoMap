package com.elderly.apartment.service;

import com.elderly.apartment.mapper.UserMapper;
import com.elderly.apartment.vo.ResidentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 住客服务类 - 从user表查询user_type=2的老人数据
 */
@Slf4j
@Service
public class ResidentService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取所有住客列表（从user表查询）
     *
     * @return 住客视图对象列表
     */
    public List<ResidentVO> getResidentList() {
        log.info("获取住客列表");
        return userMapper.selectResidentList();
    }

    /**
     * 根据ID获取住客详情
     *
     * @param id 老人ID（即user表id）
     * @return 住客视图对象
     */
    public ResidentVO getResidentById(Integer id) {
        log.info("获取住客详情, id: {}", id);
        return userMapper.selectResidentById(id);
    }

    /**
     * 根据房间号获取住客列表
     *
     * @param roomNumber 房间号
     * @return 住客视图对象列表
     */
    public List<ResidentVO> getResidentsByRoomNumber(String roomNumber) {
        log.info("根据房间号获取住客列表, roomNumber: {}", roomNumber);
        return userMapper.selectResidentByRoomNumber(roomNumber);
    }

    /**
     * 搜索住客（按姓名或手机号）
     *
     * @param keyword 搜索关键词
     * @return 住客视图对象列表
     */
    public List<ResidentVO> searchResidents(String keyword) {
        log.info("搜索住客, keyword: {}", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            return getResidentList();
        }
        return userMapper.searchResidents(keyword.trim());
    }
}
