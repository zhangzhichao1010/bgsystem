package com.managesystem.bgsystem.config.Interceptor.annotation;


import com.managesystem.bgsystem.config.Interceptor.Entity.FetchType;

import java.lang.annotation.*;

import static com.managesystem.bgsystem.config.Interceptor.Entity.FetchType.EAGER;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IPCheck {
    /*
     * timesPerSecond 每秒允许的访问次数
     * */
    int timesPerSecond();

    /*
     * fetch add black IP strategy
     * */
    FetchType fetch() default EAGER;

    /*
     * interruptedTime fetch lazy TimeUnit second
     * */
    long interruptedTime() default 300;

    /*
     * allowTryTimes if -1:try any time，else try allowTryTimes if fail add  ip to black IP
     * */
    int allowTryTimes() default -1;
}
