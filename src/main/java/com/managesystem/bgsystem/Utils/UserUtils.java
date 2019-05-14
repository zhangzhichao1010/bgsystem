package com.managesystem.bgsystem.Utils;

import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class UserUtils {
    public static Long getCookieUserId(HttpServletRequest request) {
        String uid = getCookieByName(request, "UID");
        if (!StringUtils.isEmpty(uid)) {
            return Long.parseLong(uid.trim());
        } else {
            return null;
        }
    }

    public static String getCookieByName(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(cookieName)) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    public static Integer getCookieFid(HttpServletRequest request) {
        String fid = getCookieByName(request, "xhrd_fid");
        if (!StringUtils.isEmpty(fid)) {
            return Integer.parseInt(fid);
        } else {
            return null;
        }
    }
}
