package com.managesystem.bgsystem.config.ipfilter.agent;
import com.managesystem.bgsystem.config.ipfilter.interceptor.ParameterWrapper;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class xssAgent {
  /*  @Pointcut(value = "@annotation(com.managesystem.bgsystem.config.ipfilter.annotation.XSSFilter)")
    public void annotationPointCut() {
    }

    @Before("annotationPointCut()")
    public void xssBedore(JoinPoint joinPoint, ProceedingJoinPoint joinPoint1) {
        MethodSignature sign = (MethodSignature) joinPoint.getSignature();
        String[] attackFields = sign.getMethod().getAnnotation(XSSFilter.class).attackField();

    }
*/
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
}
