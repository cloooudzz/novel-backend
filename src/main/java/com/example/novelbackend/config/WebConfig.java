package com.example.novelbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${avatar.upload.path}")
    private String uploadPath;

    @Value("${avatar.access.prefix}")
    private String accessPrefix;

    @Value("${cover.upload.path:./uploads/covers/}")
    private String coverUploadPath;

    @Value("${cover.access.prefix:/covers/}")
    private String coverAccessPrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(accessPrefix + "**")
                .addResourceLocations("file:" + uploadPath);

        registry.addResourceHandler(coverAccessPrefix + "**")
                .addResourceLocations("file:" + coverUploadPath);
    }

    // 跨域配置（解决后端访问不到）
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600);
    }
}