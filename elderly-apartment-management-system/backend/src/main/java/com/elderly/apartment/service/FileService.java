package com.elderly.apartment.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    
    /**
     * 上传单张图片
     *
     * @param file      图片文件
     * @param directory 子目录（如：rooms, elderly, avatars）
     * @return 图片访问URL
     */
    String uploadImage(MultipartFile file, String directory);
    
    /**
     * 上传多张图片
     *
     * @param files     图片文件列表
     * @param directory 子目录
     * @return 图片访问URL列表
     */
    List<String> uploadImages(List<MultipartFile> files, String directory);
    
    /**
     * 删除图片
     *
     * @param imageUrl 图片URL
     * @return 是否删除成功
     */
    boolean deleteImage(String imageUrl);
    
    /**
     * 获取图片完整访问URL
     *
     * @param relativePath 相对路径
     * @return 完整URL
     */
    String getFullImageUrl(String relativePath);
    
    /**
     * 验证图片文件
     *
     * @param file 图片文件
     * @return 验证结果
     */
    boolean validateImage(MultipartFile file);
}
