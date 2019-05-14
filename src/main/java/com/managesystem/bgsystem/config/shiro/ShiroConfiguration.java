package com.managesystem.bgsystem.config.shiro;
import com.managesystem.bgsystem.config.shiro.realm.ApplicationShiroRealm;
import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/*
 * design all by zhichao zhang 20190420
 * Shiro配置，sessionc存redis
 * */
@Configuration
public class ShiroConfiguration {
    @Autowired
    private RedisSessionDAO redisSessionDAO;

    @Bean
    public ApplicationShiroRealm shiroRealm() {
        ApplicationShiroRealm shiroRealm = new ApplicationShiroRealm();
        return shiroRealm;
    }

    @Value(value = "${spring.redis.session.expire}")
    private Long sessionExpire;

    //Filter工厂，设置对应的过滤条件和跳转条件
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) throws Exception{
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, String> map = new HashMap<String, String>();
        map.put("manage/logout", "logout");
        map.put("manage/getCode", "anon");
        map.put("static/**", "anon");
        map.put("manage/login", "anon");
        map.put("manage/**", "authc");
        //登录
        shiroFilterFactoryBean.setLoginUrl("manage/login");
        //首页
        shiroFilterFactoryBean.setSuccessUrl("manage/index");
        //错误页面，认证不通过跳转
        shiroFilterFactoryBean.setUnauthorizedUrl("manage/error");

        try {
            /*  奇怪了，这里居然不支持utf-8 格式    utf-8  和 utf-8 BOM 有差别，不支持utf-8 BOM */
            Ini ini = Ini.fromResourcePath("classpath:shiro.ini");
            for (Map.Entry<String, String> tmp : ini.getSection("urls").entrySet()) {
                map.put(tmp.getKey(), tmp.getValue());
            }
        } catch (Exception e) {

        }


        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);

        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm());
        //配置sessionManager
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    //加入注解的使用，不加入这个注解不生效
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }


    /**
     * 配置sessionManager
     *
     * @return
     */
    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionIdCookie(sessionIdCookie());
        sessionManager.setSessionIdCookieEnabled(true);
        //注入RedisSessionDAO
        if (sessionExpire != null) {
            redisSessionDAO.setSessionInMemoryTimeout(sessionExpire);
        }
        sessionManager.setSessionDAO(redisSessionDAO);
        return sessionManager;
    }

    /**
     * 指定本系统sessionid, 问题: 与servlet容器名冲突, 如jetty, tomcat 等默认jsessionid,当跳出shiro servlet时如error-page容器会为jsessionid重新分配值导致登录会话丢失!
     *
     * @return
     */
    @Bean
    public SimpleCookie sessionIdCookie() {
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setName("shiro_session");
        return simpleCookie;
    }
}
