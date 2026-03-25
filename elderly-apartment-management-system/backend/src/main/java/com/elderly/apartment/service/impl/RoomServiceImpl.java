package com.elderly.apartment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.apartment.entity.Room;
import com.elderly.apartment.mapper.RoomMapper;
import com.elderly.apartment.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    @Autowired
    private RoomMapper roomMapper;

    @Override
    public List<Room> findByType(Integer type) {
        LambdaQueryWrapper<Room> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Room::getType, type)
                    .eq(Room::getDeleted, 0);
        return list(queryWrapper);
    }

    @Override
    public List<Room> findByStatus(Integer status) {
        LambdaQueryWrapper<Room> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Room::getStatus, status)
                    .eq(Room::getDeleted, 0);
        return list(queryWrapper);
    }

    @Override
    public List<Room> findAvailableRooms() {
        return findByStatus(1);
    }

    @Override
    public List<Room> findOccupiedRooms() {
        return findByStatus(2);
    }

    @Override
    public Room updateStatus(Integer id, Integer status) {
        Room room = getById(id);
        if (room != null) {
            room.setStatus(status);
            updateById(room);
        }
        return room;
    }

    @Override
    public boolean isRoomAvailable(Integer id) {
        Room room = getById(id);
        return room != null && room.getStatus() == 1 && room.getCurrentOccupancy() < room.getCapacity();
    }

    @Override
    public Room increaseOccupancy(Integer id) {
        Room room = getById(id);
        if (room != null && room.getCurrentOccupancy() < room.getCapacity()) {
            room.setCurrentOccupancy(room.getCurrentOccupancy() + 1);
            updateById(room);
        }
        return room;
    }

    @Override
    public Room decreaseOccupancy(Integer id) {
        Room room = getById(id);
        if (room != null && room.getCurrentOccupancy() > 0) {
            room.setCurrentOccupancy(room.getCurrentOccupancy() - 1);
            updateById(room);
        }
        return room;
    }
}
