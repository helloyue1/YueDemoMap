package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.config.UploadConfig;
import com.elderly.apartment.dto.RoomStatsDTO;
import com.elderly.apartment.entity.Room;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.service.FileService;
import com.elderly.apartment.service.RoomService;
import com.elderly.apartment.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j

@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UploadConfig uploadConfig;

    @GetMapping
    public Result<Page<Room>> getRoomList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        Page<Room> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();

        if (roomNumber != null && !roomNumber.isEmpty()) {
            wrapper.like(Room::getRoomNumber, roomNumber);
        }

        if (floor != null) {
            wrapper.eq(Room::getFloor, floor);
        }

        if (type != null) {
            wrapper.eq(Room::getType, type);
        }

        if (status != null) {
            wrapper.eq(Room::getStatus, status);
        }

        wrapper.orderByAsc(Room::getRoomNumber);
        Page<Room> result = roomService.page(pageParam, wrapper);
        
        // 实时统计每个房间的入住人数
        result.getRecords().forEach(room -> {
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.eq(User::getRoomId, room.getId())
                       .eq(User::getUserType, 2)
                       .ne(User::getStatus, 2);  // 非退房状态
            long count = userService.count(userWrapper);
            room.setCurrentOccupancy((int) count);
            
            // 根据实际入住人数更新状态
            if (count == 0) {
                room.setStatus(0);  // 空闲
            } else if (count >= room.getCapacity()) {
                room.setStatus(1);  // 已满
            } else {
                room.setStatus(3);  // 部分入住
            }
        });
        
        // 处理图片URL
        result.getRecords().forEach(this::processRoomImages);
        
        return Result.success(result);
    }

    @GetMapping("/info")
    public Result<Room> getCurrentUserRoom() {
        // 获取当前登录用户的房间信息
        Integer elderlyId = com.elderly.apartment.security.DataPermissionUtil.getElderlyIdForCurrentUser();
        if (elderlyId == null) {
            return Result.error("未找到入住信息");
        }
        
        // 查询老人信息获取房间ID
        User user = userService.getById(elderlyId);
        if (user == null || user.getRoomId() == null) {
            return Result.error("未找到入住信息");
        }
        
        Room room = roomService.getById(user.getRoomId());
        if (room == null) {
            return Result.error("房间不存在");
        }
        
        // 处理图片URL
        processRoomImages(room);
        
        return Result.success(room);
    }

    @GetMapping("/stats")
    public Result<RoomStatsDTO> getRoomStats() {
        RoomStatsDTO stats = new RoomStatsDTO();

        // 总房间数
        long total = roomService.count();
        stats.setTotal((int) total);

        // 维修中房间数
        LambdaQueryWrapper<Room> maintenanceWrapper = new LambdaQueryWrapper<>();
        maintenanceWrapper.eq(Room::getStatus, 2);
        long maintenanceCount = roomService.count(maintenanceWrapper);
        stats.setMaintenance((int) maintenanceCount);

        // 获取所有非维修中的房间
        LambdaQueryWrapper<Room> roomWrapper = new LambdaQueryWrapper<>();
        roomWrapper.ne(Room::getStatus, 2);
        List<Room> rooms = roomService.list(roomWrapper);
        
        // 实时统计每个房间的入住人数
        int emptyCount = 0;
        int fullCount = 0;
        int partialCount = 0;
        
        for (Room room : rooms) {
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.eq(User::getRoomId, room.getId())
                       .eq(User::getUserType, 2)
                       .ne(User::getStatus, 2);
            long occupantCount = userService.count(userWrapper);
            
            if (occupantCount == 0) {
                emptyCount++;
            } else if (occupantCount >= room.getCapacity()) {
                fullCount++;
            } else {
                partialCount++;
            }
        }
        
        stats.setEmpty(emptyCount);
        stats.setFull(fullCount);
        stats.setPartial(partialCount);

        return Result.success(stats);
    }

    @GetMapping("/{id}")
    public Result<Room> getRoomById(@PathVariable Integer id) {
        Room room = roomService.getById(id);
        if (room == null) {
            return Result.error("房间不存在");
        }
        
        // 处理图片URL
        processRoomImages(room);
        
        return Result.success(room);
    }

    @GetMapping("/{id}/elders")
    public Result<List<User>> getRoomElders(@PathVariable Integer id) {
        Room room = roomService.getById(id);
        if (room == null) {
            return Result.error("房间不存在");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRoomId, id)
               .eq(User::getUserType, 2)
               .ne(User::getStatus, 2);  // 查询非退房的老人（在住、请假等）
        List<User> elders = userService.list(wrapper);
        return Result.success(elders);
    }

    @PostMapping
    public Result<Room> createRoom(@RequestBody Room room) {
        System.out.println("创建房间，接收到的数据: " + room);
        System.out.println("房间图片: " + room.getImages());
        
        // 设置初始入住人数为0
        if (room.getCurrentOccupancy() == null) {
            room.setCurrentOccupancy(0);
        }
        // 根据入住人数设置状态
        if (room.getCurrentOccupancy() == 0) {
            room.setStatus(0);  // 空闲
        } else if (room.getCurrentOccupancy() >= room.getCapacity()) {
            room.setStatus(1);  // 已满
        } else {
            room.setStatus(3);  // 部分入住
        }

        roomService.save(room);
        
        // 处理图片URL
        processRoomImages(room);
        
        return Result.success(room);
    }

    @PutMapping("/{id}")
    public Result<Room> updateRoom(@PathVariable Integer id, @RequestBody Room room) {
        log.info("更新房间 {}, images: {}, coverImage: {}", id, room.getImages(), room.getCoverImage());
        Room existing = roomService.getById(id);
        if (existing == null) {
            return Result.error("房间不存在");
        }

        room.setId(id);
        boolean updated = roomService.updateById(room);
        Room updatedRoom = roomService.getById(id);
        log.info("更新结果: {}, 更新后查询images: {}, coverImage: {}", updated, updatedRoom.getImages(), updatedRoom.getCoverImage());
        
        // 处理图片URL
        processRoomImages(room);
        
        return Result.success(room);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRoom(@PathVariable Integer id) {
        Room room = roomService.getById(id);
        if (room == null) {
            return Result.error("房间不存在");
        }

        if (room.getCurrentOccupancy() > 0) {
            return Result.error("房间仍有老人入住，无法删除");
        }

        roomService.removeById(id);
        return Result.success(null);
    }

    @GetMapping("/type/{type}")
    public Result<List<Room>> getRoomsByType(@PathVariable Integer type) {
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getType, type);
        List<Room> result = roomService.list(wrapper);
        
        // 处理图片URL
        result.forEach(this::processRoomImages);
        
        return Result.success(result);
    }

    @GetMapping("/status/{status}")
    public Result<List<Room>> getRoomsByStatus(@PathVariable Integer status) {
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getStatus, status);
        List<Room> result = roomService.list(wrapper);
        
        // 处理图片URL
        result.forEach(this::processRoomImages);
        
        return Result.success(result);
    }

    @GetMapping("/available")
    public Result<List<Room>> getAvailableRooms() {
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        // 返回空闲(status=0)或部分入住(status=3)的房间
        wrapper.and(w -> w.eq(Room::getStatus, 0).or().eq(Room::getStatus, 3));
        List<Room> result = roomService.list(wrapper);
        
        System.out.println("可用房间查询结果数量: " + result.size());
        result.forEach(r -> System.out.println("房间: " + r.getRoomNumber() + ", status: " + r.getStatus() + ", occupancy: " + r.getCurrentOccupancy() + "/" + r.getCapacity()));
        
        // 过滤出有可用床位的房间（当前入住人数小于容量）
        result = result.stream()
                .filter(room -> room.getCurrentOccupancy() < room.getCapacity())
                .collect(Collectors.toList());
        
        System.out.println("过滤后可用房间数量: " + result.size());
        
        // 处理图片URL
        result.forEach(this::processRoomImages);
        
        return Result.success(result);
    }

    @GetMapping("/occupied")
    public Result<List<Room>> getOccupiedRooms() {
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(Room::getCurrentOccupancy, 0);
        List<Room> result = roomService.list(wrapper);
        
        // 处理图片URL
        result.forEach(this::processRoomImages);
        
        return Result.success(result);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateRoomStatus(@PathVariable Integer id, @RequestParam Integer status) {
        Room room = roomService.getById(id);
        if (room == null) {
            return Result.error("房间不存在");
        }

        room.setStatus(status);
        roomService.updateById(room);
        return Result.success(null);
    }

    @GetMapping("/{id}/available")
    public Result<Boolean> isRoomAvailable(@PathVariable Integer id) {
        Room room = roomService.getById(id);
        if (room == null) {
            return Result.error("房间不存在");
        }

        boolean available = room.getCurrentOccupancy() < room.getCapacity();
        return Result.success(available);
    }

    @PutMapping("/{id}/increase-occupancy")
    public Result<Void> increaseOccupancy(@PathVariable Integer id) {
        Room room = roomService.getById(id);
        if (room == null) {
            return Result.error("房间不存在");
        }

        if (room.getCurrentOccupancy() >= room.getCapacity()) {
            return Result.error("房间已满");
        }

        room.setCurrentOccupancy(room.getCurrentOccupancy() + 1);
        
        // 更新状态
        if (room.getCurrentOccupancy() >= room.getCapacity()) {
            room.setStatus(1);  // 已满
        } else {
            room.setStatus(3);  // 部分入住
        }
        
        roomService.updateById(room);
        return Result.success(null);
    }

    @PutMapping("/{id}/decrease-occupancy")
    public Result<Void> decreaseOccupancy(@PathVariable Integer id) {
        Room room = roomService.getById(id);
        if (room == null) {
            return Result.error("房间不存在");
        }

        if (room.getCurrentOccupancy() <= 0) {
            return Result.error("房间无人入住");
        }

        room.setCurrentOccupancy(room.getCurrentOccupancy() - 1);
        
        // 更新状态
        if (room.getCurrentOccupancy() == 0) {
            room.setStatus(0);  // 空闲
        } else {
            room.setStatus(3);  // 部分入住
        }
        
        roomService.updateById(room);
        return Result.success(null);
    }
    
    /**
     * 处理房间图片URL，将相对路径转换为完整URL
     */
    private void processRoomImages(Room room) {
        if (room.getImages() != null && !room.getImages().isEmpty()) {
            String[] imageArray = room.getImages().split(",");
            StringBuilder fullUrls = new StringBuilder();
            for (int i = 0; i < imageArray.length; i++) {
                String image = imageArray[i].trim();
                if (!image.isEmpty()) {
                    String fullUrl = fileService.getFullImageUrl(image);
                    if (fullUrls.length() > 0) {
                        fullUrls.append(",");
                    }
                    fullUrls.append(fullUrl);
                }
            }
            room.setImages(fullUrls.toString());
        }
    }
}
