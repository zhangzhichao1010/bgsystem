package com.managesystem.bgsystem.Utils;

import com.managesystem.bgsystem.Model.Entity.Admin;
import com.managesystem.bgsystem.Model.Entity.Roles;
import com.managesystem.bgsystem.exception.NormalException;
import org.apache.shiro.SecurityUtils;

import java.util.List;

public class AdminUtils {
    public static Integer getFid() {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        Integer fid = null;
        if (admin != null) {
            fid = admin.getFid();
        } else {
            throw new NormalException("登录超时，请重新登录！");
        }
        return fid;
    }

    public static Integer getAdminType() {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        Integer type = null;
        if (admin != null) {
            type = admin.getType();
        } else {
            throw new NormalException("登录超时，请重新登录！");
        }
        return type;
    }

    public static Admin getAdmin() {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (admin != null) {
            admin.setPassword(null);
            return admin;
        } else {
            throw new NormalException("登录超时，请重新登录！");
        }
    }

    public static Long getOrgId() {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (admin != null) {
            return admin.getUnitId();
        } else {
            throw new NormalException("登录超时，请重新登录！");
        }
    }

    public static Boolean isSuperAdmin() {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (admin == null) {
            //  如果 session 过期  应当跳转到 登陆页面。
            throw new NormalException("登录超时，请重新登录！");
        }
        Integer adminType = admin.getType();
        if (adminType == 255) {
            return true;
        }
        return false;
    }


    public static Long getAdminId() {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (admin != null) {
            return admin.getId();
        }
        return null;
    }

    public static List<Roles> getRoles() {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (admin == null) {
            throw new NormalException("登录超时，请重新登录！");
        }
        return admin.getRoles();
    }

    public static Long getUnitId() {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        Long unitId = null;
        if (admin != null) {
            unitId = admin.getUnitId();
        } else {
            throw new NormalException("登录超时，请重新登录！");
        }
        return unitId;
    }


}
