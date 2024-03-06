package com.example.sbb;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //file:///C:/Users/LG/myapp/uploadDir/
        String uploadDirPath = "file:///C:/Users/LG/myapp/uploads/";
        registry.addResourceHandler("/files/**")
                .addResourceLocations(uploadDirPath);

    }
}