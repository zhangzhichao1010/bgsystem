package com.managesystem.bgsystem.config.Interceptor.HandlerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Register implements WebMvcConfigurer {

    @Value("${web.upload-path}")
    private String uploadPath;
    @Value("${spring.ip.filter.errorpageURL}")
    private String errorpageURL;
    @Value("${spring.ip.filter.filterURL}")
    private String filterURL;

    @Bean
    public IPAdressInterceptor setInterceptorHandleOne() {
        return new IPAdressInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(setInterceptorHandleOne()).addPathPatterns(filterURL).excludePathPatterns(errorpageURL);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + uploadPath);
    }
}
