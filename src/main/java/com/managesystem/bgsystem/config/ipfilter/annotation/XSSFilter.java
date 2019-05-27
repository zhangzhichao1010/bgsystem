package com.managesystem.bgsystem.config.ipfilter.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XSSFilter {
    String[] attackField() default "script";
}
