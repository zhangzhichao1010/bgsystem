package com.managesystem.bgsystem.config.Interceptor.utils;

public class StringUtils {
    public static Boolean isEmpty(String str) {
        return ("".equals(str) || str == null) ? true : false;
    }
}
