package com.elderly.apartment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "upload")
public class UploadConfig {
    
    /**
     * 上传文件存储路径
     */
    private String path = "uploads";
    
    /**
     * 访问URL前缀
     */
    private String urlPrefix = "/uploads";
    
    /**
     * 允许的图片类型
     */
    private String[] allowedTypes = {"image/jpeg", "image/png", "image/gif", "image/webp"};
    
    /**
     * 最大文件大小（MB）
     */
    private int maxSize = 5;
    
    /**
     * 房间图片子目录
     */
    private String roomDir = "rooms";
    
    /**
     * 老人头像子目录
     */
    private String elderlyDir = "elderly";
    
    /**
     * 用户头像子目录
     */
    private String avatarDir = "avatars";
    
    /**
     * 活动图片子目录
     */
    private String activityDir = "activities";
}
