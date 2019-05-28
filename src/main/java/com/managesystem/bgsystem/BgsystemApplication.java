package com.managesystem.bgsystem;

import com.managesystem.bgsystem.config.ipfilter.interceptor.ParamsFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession
@ServletComponentScan
public class BgsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BgsystemApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean testFilterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new ParamsFilter());
        registration.addUrlPatterns("/*");
    /*    registration.addInitParameter("paramName", "paramValue");*/
        registration.setName("paramsFilter");
        registration.setOrder(1);
        return registration;
    }

}
