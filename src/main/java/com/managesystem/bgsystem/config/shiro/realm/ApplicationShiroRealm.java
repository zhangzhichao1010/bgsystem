package com.managesystem.bgsystem.config.shiro.realm;

import com.alibaba.druid.util.StringUtils;
import com.managesystem.bgsystem.Model.Entity.Admin;
import com.managesystem.bgsystem.Model.Entity.Roles;
import com.managesystem.bgsystem.Service.Interface.AdminService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * design all by zhichao zhang 20190415
 * shiro登录注册
 * */
@Component
public class ApplicationShiroRealm extends AuthorizingRealm {
    @Autowired
    private AdminService adminService;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String adminname = token.getUsername();
        String password = String.valueOf(token.getPassword());
        if (StringUtils.isEmpty(adminname)) {
            throw new UnknownAccountException("请输入用户名!");
        }
        if (StringUtils.isEmpty(password)) {
            throw new UnknownAccountException("请输入密码!");
        }
        Admin admin = adminService.findAdminByName(adminname);
        if (admin == null) {
            throw new UnknownAccountException("用户名不存在!");
        }
        if (!admin.getPassword().equals(password)) {
            throw new IncorrectCredentialsException("用户名或密码错误!");
        }
        if (admin.getStatus() == 1) {
            throw new LockedAccountException("账号被锁定,请联系管理员!");
        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(admin, password, getName());

        /*记住我，将用户信息存到Cookie中，浏览器关闭后，下次重启，依然免密登陆*/
        token.setRememberMe(true);
        return info;

    }

    /*
     * 权限管理
     * */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Admin adminQ = (Admin) principalCollection.getPrimaryPrincipal();
        //查询用户名称
        Admin admin = adminService.findAdminByName(adminQ.getAdminname());
        if (admin == null) {
            throw new UnknownAccountException("用户不存在");
        }
        //添加角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        if (admin.getRoles() == null) {
            throw new UnknownAccountException("用户角色不存在");
        }
        for (Roles role : admin.getRoles()) {
            //添加角色
            simpleAuthorizationInfo.addRole(role.getRolename());
            for (String permission : role.getAuthority().getAuthorityList().split(",")) {
                //添加权限
                simpleAuthorizationInfo.addStringPermission(permission);
            }
        }
        return simpleAuthorizationInfo;

    }


    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        Admin adminQ = (Admin) principals.getPrimaryPrincipal();
        if (adminQ == null) {
            return false;
        }
        return adminQ.isSuperAdmin() || super.isPermitted(principals, permission);
    }


    @Override
    public boolean hasRole(PrincipalCollection principals, String roleIdentifier) {
        Admin adminQ = (Admin) principals.getPrimaryPrincipal();
        if (adminQ == null) {
            return false;
        }
        return adminQ.isSuperAdmin() || super.hasRole(principals, roleIdentifier);
    }
}
