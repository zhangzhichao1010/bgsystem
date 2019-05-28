package com.managesystem.bgsystem.config.ipfilter.interceptor;

import com.managesystem.bgsystem.config.ipfilter.Entity.FetchType;
import com.managesystem.bgsystem.config.ipfilter.agent.handlerAgent;
import com.managesystem.bgsystem.config.ipfilter.annotation.XSSFilter;
import com.managesystem.bgsystem.config.ipfilter.utils.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.reflect.misc.MethodUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParamsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        XSSFilter xssFilter = request.getHttpServletMapping().getMappingMatch().getDeclaringClass().getAnnotation(XSSFilter.class);
        /*if (xssFilter == null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            String[] attackFields = xssFilter.attackField();
            request = getParams(attackFields);
            filterChain.doFilter(request, servletResponse);
        }*/
        servletRequest = getParams(new String[]{"script"});
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private ParameterWrapper getParams(String[] attackFields) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        Map<String, String[]> stringMap = request.getParameterMap();
        HashMap<String, String[]> newParams = new HashMap<>();
        if (stringMap != null) {
            for (int j = 0; j < attackFields.length; j++) {
                for (String key : stringMap.keySet()
                        ) {
                    String[] values = stringMap.get(key);
                    for (int i = 0; i < values.length; i++) {
                        String a = values[i];
                        values[i] = values[i].replaceAll(attackFields[j], "");
                    }
                    newParams.put(key, values);
                }
            }
        }
        return new ParameterWrapper(request, newParams);
    }

    @Override
    public void destroy() {

    }
}
