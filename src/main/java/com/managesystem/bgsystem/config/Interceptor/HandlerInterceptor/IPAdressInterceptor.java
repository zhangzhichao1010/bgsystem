package com.managesystem.bgsystem.config.Interceptor.HandlerInterceptor;

import com.managesystem.bgsystem.config.Interceptor.service.IPAdressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class IPAdressInterceptor implements HandlerInterceptor {
    @Autowired
    private IPAdressService iPAdressService;

    @Value("${spring.ip.filter.errorpageURL}")
    private String errorpageURL;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Boolean canPass = false;
        canPass = iPAdressService.ckeckRemoteIP(request.getRemoteAddr());
        if (!canPass) response.sendRedirect(errorpageURL);
        return canPass;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }
}
