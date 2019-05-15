package com.managesystem.bgsystem.config.Interceptor.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IPCheck {
    int timesPerSecond();
}
