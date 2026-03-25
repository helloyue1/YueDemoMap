package com.elderly.apartment.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UploadConfig uploadConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取上传目录的绝对路径
        String path = uploadConfig.getPath();
        Path uploadPath;
        if (path.startsWith("./")) {
            String userDir = System.getProperty("user.dir");
            uploadPath = Paths.get(userDir, path.substring(2));
        } else {
            uploadPath = Paths.get(path);
        }
        
        String absolutePath = uploadPath.toFile().getAbsolutePath();
        
        // 映射 /uploads/** 到文件系统
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath + "/");
        
        System.out.println("静态资源映射: /uploads/** -> file:" + absolutePath + "/");
    }
}
