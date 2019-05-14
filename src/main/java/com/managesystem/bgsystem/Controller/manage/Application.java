package com.managesystem.bgsystem.Controller.manage;

import cn.hutool.crypto.SecureUtil;
import com.managesystem.bgsystem.Model.Entity.Admin;
import com.managesystem.bgsystem.Model.Entity.Roles;
import com.managesystem.bgsystem.Service.Interface.AdminService;
import com.managesystem.bgsystem.Service.Interface.OrganizationService;
import com.managesystem.bgsystem.Utils.RedisUtils;
import com.managesystem.bgsystem.Utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/*
 * design all by zhichao zhang 20190417
 * 登录操作，目前后台无注册
 *
 * */
@Slf4j
@Controller
@RequestMapping("manage")
public class Application {

    @Autowired
    private AdminService adminService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private RedisUtils redisUtils;


    @RequestMapping(value = {"/login", ""}, method = RequestMethod.GET)
    public String login() {
        return "manage/Application/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, ModelMap modelMap, HttpServletRequest request, HttpSession session) {
        String msg = "";
        String reqCode = "";
        Object codeObject = request.getSession(false).getAttribute("RANDOMCODEKEY");
        if (codeObject != null) {
            reqCode = codeObject.toString().toLowerCase();
        } else {
            return "manage/Application/login";
        }
        if (!(code.toLowerCase().equals(reqCode))) {
            msg = "验证码错误,请重新填写!";
            modelMap.addAttribute("msg", msg);
            return "manage/Application/login";
        }
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            msg = "用户名和密码不能为空,请重新填写!";
            modelMap.addAttribute("msg", msg);
            return "manage/Application/login";
        }
        password = SecureUtil.sha1(SecureUtil.md5(password));
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        subject.login(usernamePasswordToken);
        Admin admin = (Admin) subject.getPrincipal();
        session.setAttribute("isSuperAdmin", admin.isSuperAdmin());    /*  add by lizhifeng */
        Integer superFid = admin.getSuperFid();
        String superFidKey = "superFid_" + admin.getUnitId();
        List<Integer> childFidList = organizationService.findAllChildFid(admin.getUnitId());
        String childFids = "childFids_" + admin.getUnitId();
        redisUtils.set(superFidKey, superFid);
        redisUtils.set(childFids, childFidList);
        boolean isShow = admin.isSuperAdmin() || organizationService.isRoot(admin.getUnitId());
        session.setAttribute("isShow", isShow);

        modelMap.addAttribute("admin", admin);
        log.info("用户名：" + admin.getAdminname() + "，单位ID：" + admin.getUnitId() + ",单位名称：" + admin.getUnitname() + ",登录成功！");
        return "manage/Application/index";
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "manage/Application/index";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(ModelMap modelMap, HttpServletResponse response) {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (admin != null) {
            log.info("用户名：" + admin.getAdminname() + "，单位ID：" + admin.getUnitId() + ",单位名称：" + admin.getUnitname() + ",退出登录！");
            modelMap.addAttribute("msg", "退出成功!");
            SecurityUtils.getSubject().logout();
            String superFidKey = "superFid_" + admin.getUnitId();
            String childFids = "childFids_" + admin.getUnitId();
            redisUtils.del(superFidKey, childFids);
        }
        return "manage/Application/login";
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String error() {
        return "manage/Application/index";
    }

    @RequestMapping(value = "/getCode")
    public void getCode(HttpServletRequest request, HttpServletResponse response, Long time) {
        try {
            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);
            ValidateCodeUtils randomValidateCode = new ValidateCodeUtils();
            randomValidateCode.getRandcode(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "getAuths")
    @ResponseBody
    public String getAuths() {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        String authStrs = "";
        if (admin != null) {
            List<Roles> roles = admin.getRoles();
            for (Roles role : roles
            ) {
                authStrs += role.getAuthority().getAuthorityList() + ",";
            }
        }
        return authStrs;
    }

    @RequestMapping(value = "saveTheme")
    @ResponseBody
    public void saveTheme(String themeName) {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (admin != null) {
            admin.setThemeName(themeName);
            adminService.saveDirectAdmin(admin);
        }
    }
}
