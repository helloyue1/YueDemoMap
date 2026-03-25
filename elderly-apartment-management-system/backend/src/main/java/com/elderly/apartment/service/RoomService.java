package com.elderly.apartment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elderly.apartment.entity.Room;

import java.util.List;

public interface RoomService extends IService<Room> {

    List<Room> findByType(Integer type);

    List<Room> findByStatus(Integer status);

    List<Room> findAvailableRooms();

    List<Room> findOccupiedRooms();

    Room updateStatus(Integer id, Integer status);

    boolean isRoomAvailable(Integer id);

    Room increaseOccupancy(Integer id);

    Room decreaseOccupancy(Integer id);
}
