package com.managesystem.bgsystem.config.aspect;

import com.managesystem.bgsystem.Model.Entity.Admin;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/*
 * design all by zhichao zhang 20190415
 * 日志拦截处理
 * */
@Aspect
@Configuration
@Slf4j
public class LogAspectConfig {
    private volatile Long beginTime;

    @Pointcut("execution(* com.managesystem.bgsystem.manage..*(..))")
    public void excudeLogging() {
    }

    @Before(value = "excudeLogging()")
    public void beforeAction() {
        beginTime = System.nanoTime();
    }

    @After(value = "excudeLogging()")
    public void AfterAction() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (admin != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            HttpServletResponse response = requestAttributes.getResponse();
            String url = request.getRequestURL().toString();
            String params = "";
            Map<String, String[]> paramsMap = request.getParameterMap();
            Long timeCost = System.nanoTime() - beginTime;
            if (!paramsMap.isEmpty()) {
                params = "参数列表：";
                for (Map.Entry<String, String[]> map : paramsMap.entrySet()) {
                    StringBuffer stringBuffer = new StringBuffer();
                    String[] values = map.getValue();
                    for (String value : values) {
                        stringBuffer.append(value + "&");
                    }
                    params += map.getKey() + ":" + stringBuffer;
                }
            }
            String method = request.getMethod();
            int status = response.getStatus();
            log.info("用户ID：" + admin.getId() + ",用户名：" + admin.getAdminname() + ",单位名称："
                    + admin.getUnitname() + ",访问地址：" + url + ",请求方式：" + method + "," + params + ",请求响应状态：" + status + ",响应时间（纳秒）：" + timeCost);
        }
    }
}
