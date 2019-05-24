package com.managesystem.bgsystem.config.Interceptor.agent;

import com.managesystem.bgsystem.config.Interceptor.Entity.FetchType;
import com.managesystem.bgsystem.config.Interceptor.Entity.IPDataLocation;
import com.managesystem.bgsystem.config.Interceptor.Entity.IPFilterBean;
import com.managesystem.bgsystem.config.Interceptor.annotation.IPCheck;
import com.managesystem.bgsystem.config.Interceptor.service.BlackIPLocalService;
import com.managesystem.bgsystem.config.Interceptor.service.IPAdressService;
import com.managesystem.bgsystem.config.Interceptor.utils.RedisToolsUtils;
import com.managesystem.bgsystem.config.Interceptor.utils.StringUtils;
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
    @Autowired
    private BlackIPLocalService blackIPLocalService;

    @Pointcut(value = "@annotation(com.managesystem.bgsystem.config.Interceptor.annotation.IPCheck)")
    public void annotationPointCut() {
    }

    @Before("annotationPointCut()")
    public void before(JoinPoint joinPoint) {
        MethodSignature sign = (MethodSignature) joinPoint.getSignature();
        IPCheck ipCheck = sign.getMethod().getAnnotation(IPCheck.class);
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        Boolean canPass = checkIP(request.getRemoteAddr());
        try {
            if (!canPass) {
                response.sendRedirect(ipFilterBean.getErrorpageURL());
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String key = request.getRemoteAddr() + "&" + request.getServletPath();
        String remoteIP = request.getRemoteAddr();
        if (FetchType.EAGER.equals(ipCheck.fetch())) {
            countTimes(key, ipCheck, remoteIP, 1);
        } else {
            countTimes(key, ipCheck, remoteIP, 2);
        }
    }

    private void countTimes(String key, IPCheck ipCheck, String remoteIP, int type) {
        Integer v = (Integer) redisUtils.get(key);
        if (v == null) {
            redisUtils.set(key, 0, 1);
            v = 0;
        }
        if (v > ipCheck.timesPerSecond() - 1) {
            denyIP(key, remoteIP, type, ipCheck);
        }
        redisUtils.incr(key, 1);
    }

    private void denyIP(String key, String remoteIP, int type, IPCheck ipCheck) {
        if (type == 1) {
            addIPData(remoteIP);
        } else {
            addTemIPData(ipCheck, key, remoteIP);
        }
    }

    private void addTemIPData(IPCheck ipCheck, String key, String remoteIP) {
        Integer allowTryTime = ipCheck.allowTryTimes();
        Long interruptTime = ipCheck.interruptedTime();
        Integer tryTime = redisUtils.get(key + "Temporary", Integer.class);
        if (allowTryTime == -1) {
            redisUtils.set(key, 0, 1);
            addTemporaryIP(remoteIP, interruptTime);
        } else {
            if (tryTime != null && tryTime == allowTryTime) {
                redisUtils.del(key + "Temporary");
                addIPData(remoteIP);
            } else {
                redisUtils.set(key, 0, 1);
                addTemporaryIP(remoteIP, interruptTime);
                if (tryTime == null) {
                    redisUtils.set(key + "Temporary", 1);
                } else {
                    redisUtils.incr(key + "Temporary", 1);
                }
            }
        }
    }

    private Boolean checkIP(String IP) {
        return blackIPLocalService.ckeckIP(IP);
    }

    private void addTemporaryIP(String IP, Long interruptTime) {
        String temBlackIP = redisUtils.get("BlackIPOnly_Temporary", String.class);
        redisUtils.setnx("BlackIPOnly_Temporary", IP, interruptTime);
    }

    private void addIPData(String ip) {
        IPDataLocation location = ipFilterBean.getLocation();
        switch (location) {
            case locationfile:
                blackIPLocalService.saveBlackLocalIP(ip);
                break;
            case romotefile: {
                adressService.saveBlackIPRemote();
                addRedisBlackIP(ip);
                break;
            }
            case database: {
                adressService.saveBlackIPDataBase();
                addRedisBlackIP(ip);
                break;
            }
            default:
                break;
        }
    }

    private void addRedisBlackIP(String IP) {
        String blackIps = redisUtils.get("BlackIPOnly_PERSIST", String.class);
        blackIps += "," + IP;
        redisUtils.set("BlackIPOnly_PERSIST", blackIps);
    }
}
