package com.managesystem.bgsystem.config.ipfilter.utils;

public class StringUtils {
    public static Boolean isEmpty(String str) {
        return ("".equals(str) || str == null) ? true : false;
    }
}
