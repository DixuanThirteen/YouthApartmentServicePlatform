package com.yasp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保路径以 / 结尾
        String path = new File(uploadDir).getAbsolutePath() + File.separator;

        // 映射规则：
        // 当访问 http://localhost:8080/images/xxx.png 时
        // 自动去硬盘的 uploadDir 目录下找 xxx.png
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + path);
    }
}