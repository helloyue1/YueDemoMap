package com.elderly.apartment.controller;

import com.elderly.apartment.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/menu")
public class MenuController {

    @PostMapping("/click")
    public Result<Map<String, Object>> recordMenuClick(@RequestBody Map<String, String> request) {
        String path = request.get("path");
        log.info("菜单被点击: {}, 时间: {}", path, LocalDateTime.now());

        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("message", "菜单点击记录成功");

        return Result.success(result);
    }
}
