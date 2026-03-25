package com.elderly.apartment.controller;

import com.elderly.apartment.common.Result;
import com.elderly.apartment.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {
    
    @Autowired
    private FileService fileService;
    
    /**
     * 上传单张图片
     *
     * @param file      图片文件
     * @param directory 目录类型：rooms, elderly, avatars, activities
     * @return 图片URL
     */
    @PostMapping("/upload")
    public Result<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "directory", defaultValue = "temp") String directory) {
        
        log.info("上传图片到目录: {}", directory);
        
        try {
            String relativePath = fileService.uploadImage(file, directory);
            String fullUrl = fileService.getFullImageUrl(relativePath);
            
            Map<String, String> result = new HashMap<>();
            result.put("relativePath", relativePath);
            result.put("url", fullUrl);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return Result.error("图片上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传多张图片
     *
     * @param files     图片文件列表
     * @param directory 目录类型
     * @return 图片URL列表
     */
    @PostMapping("/upload/batch")
    public Result<List<Map<String, String>>> uploadImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "directory", defaultValue = "temp") String directory) {
        
        log.info("批量上传图片到目录: {}, 数量: {}", directory, files.size());
        
        try {
            List<String> relativePaths = fileService.uploadImages(files, directory);
            
            List<Map<String, String>> resultList = relativePaths.stream()
                    .map(path -> {
                        Map<String, String> map = new HashMap<>();
                        map.put("relativePath", path);
                        map.put("url", fileService.getFullImageUrl(path));
                        return map;
                    }).toList();
            
            return Result.success(resultList);
        } catch (Exception e) {
            log.error("批量图片上传失败", e);
            return Result.error("批量图片上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除图片
     *
     * @param imageUrl 图片URL或相对路径
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Result<Boolean> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        log.info("删除图片: {}", imageUrl);
        
        boolean success = fileService.deleteImage(imageUrl);
        if (success) {
            return Result.success(true);
        } else {
            return Result.error("图片删除失败或图片不存在");
        }
    }
    
    /**
     * 获取图片完整URL
     *
     * @param relativePath 相对路径
     * @return 完整URL
     */
    @GetMapping("/url")
    public Result<String> getFullUrl(@RequestParam("path") String relativePath) {
        String fullUrl = fileService.getFullImageUrl(relativePath);
        return Result.success(fullUrl);
    }
}
