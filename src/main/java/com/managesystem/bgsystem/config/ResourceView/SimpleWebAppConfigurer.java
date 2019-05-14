package com.managesystem.bgsystem.config.ResourceView;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class SimpleWebAppConfigurer extends WebMvcConfigurerAdapter {


    @Value("${web.upload-path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + uploadPath);
        super.addResourceHandlers(registry);
    }
}
