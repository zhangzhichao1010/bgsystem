package com.managesystem.bgsystem.config.ipfilter.agent;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;


@Aspect
@Component
public class xssAgent {
    @Pointcut(value = "@annotation(com.managesystem.bgsystem.config.ipfilter.annotation.XSSFilter)")
    public void annotationPointCut() {
    }
    @Before("annotationPointCut()")
    public void xssBedore(JoinPoint joinPoint) {
     /*   MethodSignature sign = (MethodSignature) joinPoint.getSignature();
        String[] attackFields = sign.getMethod().getAnnotation(XSSFilter.class).attackField();
        request.getHttpServletMapping().getMappingMatch().getDeclaringClass().getAnnotation(XSSFilter.class);*/
    }

}
