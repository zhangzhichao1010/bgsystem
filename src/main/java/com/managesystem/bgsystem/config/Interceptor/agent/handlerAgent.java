package com.managesystem.bgsystem.config.Interceptor.agent;

import com.managesystem.bgsystem.config.Interceptor.Entity.IPDataLocation;
import com.managesystem.bgsystem.config.Interceptor.Entity.IPFilterBean;
import com.managesystem.bgsystem.config.Interceptor.annotation.IPCheck;
import com.managesystem.bgsystem.config.Interceptor.service.IPAdressService;
import com.managesystem.bgsystem.config.Interceptor.utils.RedisToolsUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Aspect
@Component
public class handlerAgent {
    @Autowired
    private RedisToolsUtils redisUtils;
    @Autowired
    private IPAdressService adressService;
    @Autowired
    private IPFilterBean ipFilterBean;

    @Pointcut(value = "@annotation(com.managesystem.bgsystem.config.Interceptor.annotation.IPCheck)")
    public void annotationPointCut() {
    }

    @Before("annotationPointCut()")
    public void before(JoinPoint joinPoint) {
        MethodSignature sign = (MethodSignature) joinPoint.getSignature();
        Integer timePerSecond = sign.getMethod().getAnnotation(IPCheck.class).timesPerSecond();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        Boolean canPass = checkIP(request.getRemoteAddr());
        try {
            if (!canPass){
                response.sendRedirect(ipFilterBean.getErrorpageURL());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String key = request.getRemoteAddr() + "&" + request.getServletPath();
        Integer v = (Integer) redisUtils.get(key);
        if (v == null) {
            redisUtils.set(key, 0, 1);
            v = 0;
        }
        if (v > timePerSecond - 1) {
            addIPData(request.getRemoteAddr());
        }
        redisUtils.incr(key, 1);
    }

    private Boolean checkIP(String IP) {
        Boolean canPass = false;
        IPDataLocation location = ipFilterBean.getLocation();
        switch (location) {
            case locationfile:
                canPass=adressService.ckeckIPLocalFile(IP);
                break;
            case romotefile:
            case database:
                canPass=adressService.ckeckIPDataBase(IP);
                break;
            default:
                break;
        }
        return canPass;
    }

    private void addIPData(String ip) {
        IPDataLocation location = ipFilterBean.getLocation();
        switch (location) {
            case locationfile:
                adressService.saveBlackLocalIP(ip);
                break;
            case romotefile:
            case database:
                adressService.saveBlackIPDataBase(ip);
                break;
            default:
                break;
        }
    }
}
