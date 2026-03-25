package com.elderly.apartment.service.impl;

import com.elderly.apartment.config.UploadConfig;
import com.elderly.apartment.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    
    @Autowired
    private UploadConfig uploadConfig;
    
    private Path uploadPath;
    
    @PostConstruct
    public void init() {
        // 初始化上传目录
        String path = uploadConfig.getPath();
        if (path.startsWith("./")) {
            // 相对路径，基于项目根目录
            String userDir = System.getProperty("user.dir");
            uploadPath = Paths.get(userDir, path.substring(2));
        } else {
            uploadPath = Paths.get(path);
        }
        
        try {
            Files.createDirectories(uploadPath);
            // 创建子目录
            Files.createDirectories(uploadPath.resolve(uploadConfig.getRoomDir()));
            Files.createDirectories(uploadPath.resolve(uploadConfig.getElderlyDir()));
            Files.createDirectories(uploadPath.resolve(uploadConfig.getAvatarDir()));
            Files.createDirectories(uploadPath.resolve(uploadConfig.getActivityDir()));
            log.info("文件上传目录初始化完成: {}", uploadPath.toAbsolutePath());
        } catch (IOException e) {
            log.error("创建上传目录失败", e);
            throw new RuntimeException("创建上传目录失败", e);
        }
    }
    
    @Override
    public String uploadImage(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        // 验证文件
        if (!validateImage(file)) {
            throw new IllegalArgumentException("不支持的图片格式或文件过大");
        }
        
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;
            
            // 创建子目录（按日期组织）
            String dateDir = java.time.LocalDate.now().toString().replace("-", "");
            Path targetDir = uploadPath.resolve(directory).resolve(dateDir);
            Files.createDirectories(targetDir);
            
            // 保存文件
            Path targetPath = targetDir.resolve(newFilename);
            file.transferTo(targetPath.toFile());
            
            // 返回相对路径（用于数据库存储）
            String relativePath = directory + "/" + dateDir + "/" + newFilename;
            log.info("图片上传成功: {}", relativePath);
            
            return relativePath;
        } catch (IOException e) {
            log.error("图片上传失败", e);
            throw new RuntimeException("图片上传失败", e);
        }
    }
    
    @Override
    public List<String> uploadImages(List<MultipartFile> files, String directory) {
        List<String> urls = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return urls;
        }
        
        for (MultipartFile file : files) {
            String url = uploadImage(file, directory);
            if (url != null) {
                urls.add(url);
            }
        }
        return urls;
    }
    
    @Override
    public boolean deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }
        
        try {
            // 从URL中提取相对路径
            String relativePath = imageUrl;
            if (imageUrl.startsWith(uploadConfig.getUrlPrefix())) {
                relativePath = imageUrl.substring(uploadConfig.getUrlPrefix().length() + 1);
            }
            if (imageUrl.startsWith("http")) {
                // 如果是完整URL，提取路径部分
                relativePath = imageUrl.substring(imageUrl.indexOf(uploadConfig.getUrlPrefix()) + uploadConfig.getUrlPrefix().length() + 1);
            }
            
            Path filePath = uploadPath.resolve(relativePath);
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("图片删除成功: {}", filePath);
            }
            return deleted;
        } catch (IOException e) {
            log.error("图片删除失败: {}", imageUrl, e);
            return false;
        }
    }
    
    @Override
    public String getFullImageUrl(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }
        
        // 如果已经是完整URL，直接返回
        if (relativePath.startsWith("http")) {
            return relativePath;
        }
        
        // 组合完整URL
        return uploadConfig.getUrlPrefix() + "/" + relativePath;
    }
    
    @Override
    public boolean validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        
        List<String> allowedTypes = Arrays.asList(uploadConfig.getAllowedTypes());
        if (!allowedTypes.contains(contentType.toLowerCase())) {
            log.warn("不支持的图片类型: {}", contentType);
            return false;
        }
        
        // 检查文件大小
        long maxSize = uploadConfig.getMaxSize() * 1024 * 1024; // 转换为字节
        if (file.getSize() > maxSize) {
            log.warn("文件过大: {} bytes, 最大允许: {} bytes", file.getSize(), maxSize);
            return false;
        }
        
        return true;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
