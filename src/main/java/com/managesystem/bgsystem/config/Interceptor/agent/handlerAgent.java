package com.managesystem.bgsystem.config.Interceptor.agent;

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
import java.lang.reflect.Method;

@Aspect
@Component
public class handlerAgent {
    @Autowired
    private RedisToolsUtils redisUtils;
    @Autowired
    private IPAdressService adressService;

    @Pointcut(value = "@annotation(com.managesystem.bgsystem.config.Interceptor.annotation.IPCheck)")
    public void annotationPointCut() {
    }

    @Before("annotationPointCut()")

    public void before(JoinPoint joinPoint) {
        MethodSignature sign = (MethodSignature) joinPoint.getSignature();
        Integer timePerSecond = sign.getMethod().getAnnotation(IPCheck.class).timesPerSecond();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String key = request.getRemoteAddr() + "&" + request.getServletPath();
        Integer v = (Integer) redisUtils.get(key);
        if (v == null) {
            redisUtils.set(key, 0, 1);
            v = 0;
        }
        if (v > timePerSecond - 1) {
            adressService.saveBlackRemoteIP(request.getRemoteAddr());
        }
        redisUtils.incr(key, 1);
    }
}
