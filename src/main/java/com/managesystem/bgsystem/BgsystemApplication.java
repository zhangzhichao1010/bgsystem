package com.managesystem.bgsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession
@ServletComponentScan
public class BgsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BgsystemApplication.class, args);
    }

}
