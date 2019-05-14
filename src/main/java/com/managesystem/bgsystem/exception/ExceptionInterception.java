package com.managesystem.bgsystem.exception;

import com.managesystem.bgsystem.Utils.DWZJsonUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.UnexpectedTypeException;

/*
 * 异常拦截
 * */
@ControllerAdvice(basePackages = {"com.managesystem.bgsystem.Controller.manage", "com.managesystem.bgsystem.Service.InterfaceImpl"})
public class ExceptionInterception {
    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseBody
    Object handleUnauthorizedException(Exception e, HttpServletRequest request) {
        String url = "/manage/login";
        String json = DWZJsonUtils.getJson("300", "对不起，您没有操作权限!", "", "forward", url);

        return json;
    }

    @ExceptionHandler(value = NormalException.class)
    @ResponseBody
    Object handleNormalException(Exception e, HttpServletRequest request) {
        String json = DWZJsonUtils.getJson("300", e.getMessage());
        return json;
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    @ResponseBody
    Object numberFormatException(Exception e, HttpServletRequest request) {
        e.printStackTrace();
        String json = DWZJsonUtils.getJson("300", "表单数据类型错误!");
        return json;
    }

    @ExceptionHandler(value = {UnauthenticatedException.class})
    @ResponseBody
    Object unauthenticatedException(Exception e, HttpServletRequest request) {
        e.printStackTrace();
        String json = DWZJsonUtils.getJson("300", "登录超时!");
        return json;
    }

    @ExceptionHandler(value = {AuthorizationException.class})
    @ResponseBody
    Object authenticatedException(Exception e, HttpServletRequest request) {
        e.printStackTrace();
        String json = DWZJsonUtils.getJson("300", "没有访问权限!");
        return json;
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    Object handleException(Exception e) {
        e.printStackTrace();
        String json = DWZJsonUtils.getJson("300", "数据错误!");
        return json;
    }

    @ExceptionHandler(value = {UnknownAccountException.class, LockedAccountException.class, IncorrectCredentialsException.class})
    Object handleShiroException(Exception e) {
        ModelAndView modelAndView = new ModelAndView();
        String msg = e.getMessage();
        modelAndView.addObject("msg", msg);
        modelAndView.setViewName("manage/Application/login");
        return modelAndView;
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    String handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        e.printStackTrace();
        String msg = e.getMessage();
        String url = new String(request.getRequestURL());
        String json = DWZJsonUtils.getJson("300", msg, "", "forward", url);
        return json;
    }

    @ExceptionHandler(value = {UnexpectedTypeException.class})
    String handleValidationTypeException(MethodArgumentNotValidException e, HttpServletRequest request) {
        e.printStackTrace();
        String msg = e.getMessage();
        String url = new String(request.getRequestURL());
        String json = DWZJsonUtils.getJson("300", msg, "", "forward", url);
        return json;
    }
}
