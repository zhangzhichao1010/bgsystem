package com.managesystem.bgsystem.Controller.manage;

import com.managesystem.bgsystem.Model.Entity.Admin;
import com.managesystem.bgsystem.Model.Entity.Roles;
import com.managesystem.bgsystem.Model.Pojo.Pager;
import com.managesystem.bgsystem.Service.Interface.AdminService;
import com.managesystem.bgsystem.Service.Interface.AuthorityService;
import com.managesystem.bgsystem.Service.Interface.RolesService;
import com.managesystem.bgsystem.Utils.CXConstants;
import com.managesystem.bgsystem.Utils.DWZJsonUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Optional;

/*
 * design all by zhichao zhang 20190417
 * 用户角色管理：每级组织可以添加指定角色信息，并且赋予角色权限
 * 管理员管理：每级组织可以操作本组织下管理员
 *
 * */
@Controller
@RequestMapping(value = "manage/RolesController")
public class RolesController {
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private RolesService rolesService;
    private final String navTab_Role = "roleManage";
    private final String navTab_admin = "adminManage";
    private final String role_url = "/manage/RolesController/listRoles";
    private final String admin_url = "/manage/RolesController/listAdmins";

    @RequestMapping(value = "listRoles")
    @RequiresPermissions(value = "kmhz")
    public String listRoles(ModelMap model, @RequestParam(required = false) String pageNum, @RequestParam(required = false) String numPerPage, @RequestParam(required = false) String searchWord, Long unitId, String unitname, @RequestParam(value = "fid") Integer fid) {
        if (pageNum == null) {
            pageNum = CXConstants.global_pageNum;
        }
        if (numPerPage == null) {
            numPerPage = CXConstants.global_numPerPage;
        }
        Pager pager = rolesService.listRoles(Integer.parseInt(pageNum) - 1, Integer.parseInt(numPerPage), searchWord, unitId);
        long total = pager.getTotal();
        model.addAttribute("pager", pager);
        model.addAttribute("total", total);
        if (!StringUtils.isEmpty(searchWord)) {
            model.addAttribute("searchWord", searchWord);
        }
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("numPerPage", numPerPage);
        model.addAttribute("unitId", unitId);
        model.addAttribute("unitname", unitname);
        model.addAttribute("fid", fid);
        return "manage/RolesController/listRoles";
    }

    @RequestMapping(value = "addRole")
    @RequiresPermissions(value = "kmhz-1")
    public String addRole(Long unitId, ModelMap modelMap, String unitname, @RequestParam(value = "fid") Integer fid) {
        modelMap.addAttribute("unitId", unitId);
        modelMap.addAttribute("unitname", unitname);
        modelMap.addAttribute("fid", fid);
        return "manage/RolesController/addRole";
    }

    @RequestMapping(value = "editRole")
    @RequiresPermissions(value = "kmhz-2")
    public String editRole(Long id, ModelMap modelMap) {
        Optional<Roles> optional = rolesService.findRoleById(id);
        Roles role = optional.get();
        Long unitId = role.getUnitId();
        String unitname = role.getUnitname();
        Integer fid = role.getFid();
        modelMap.addAttribute("unitId", unitId);
        modelMap.addAttribute("unitname", unitname);
        modelMap.addAttribute("role", role);
        modelMap.addAttribute("fid", fid);
        return "manage/RolesController/addRole";
    }

    @RequestMapping(value = "suggestAuthority")
    public String suggestAuthority(ModelMap model, @RequestParam(required = false) String pageNum, @RequestParam(required = false) String numPerPage, @RequestParam(required = false) String searchWord, @RequestParam(value = "unitId") Long unitId) {
        if (pageNum == null) {
            pageNum = CXConstants.global_pageNum;
        }
        if (numPerPage == null) {
            numPerPage = CXConstants.global_numPerPage;
        }
        Pager pager = authorityService.listSuggestAuthority(Integer.parseInt(pageNum) - 1, Integer.parseInt(numPerPage), searchWord, unitId);
        long total = pager.getTotal();
        model.addAttribute("pager", pager);
        model.addAttribute("total", total);
        if (!StringUtils.isEmpty(searchWord)) {
            model.addAttribute("searchWord", searchWord);
        }
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("unitId", unitId);
        model.addAttribute("numPerPage", numPerPage);
        return "manage/RolesController/suggestAuthority";
    }

    @RequestMapping(value = "saveRole")
    @ResponseBody
    @RequiresPermissions(value = {"kmhz-1", "kmhz-2"}, logical = Logical.OR)
    public String saveRole(Roles role, @RequestParam Long authId) {
        Long unitId = role.getUnitId();
        String unitname = role.getUnitname();
        Integer fid = role.getFid();
        String json = DWZJsonUtils.getJson("300", "\\u64cd\\u4f5c\\u5931\\u8d25", navTab_Role, "closeCurrent", role_url + "?unitId=" + unitId + "&unitname=" + unitname + "&fid=" + fid);
        if (rolesService.saveRole(role, authId) != null) {
            json = DWZJsonUtils.getJson("200", "\\u64cd\\u4f5c\\u6210\\u529f", navTab_Role, "closeCurrent", role_url + "?unitId=" + unitId + "&unitname=" + unitname + "&fid=" + fid);
        }
        return json;
    }

    @RequestMapping(value = "delRole")
    @ResponseBody
    @RequiresPermissions(value = "kmhz-3")
    public String delRole(String ids, long unitId, String unitname, Integer fid) {
        String json = DWZJsonUtils.getJson("300", "\\u64cd\\u4f5c\\u5931\\u8d25", navTab_Role, "forward", role_url + "?unitId=" + unitId + "&unitname=" + unitname + "&fid=" + fid);
        if (rolesService.delRoles(ids)) {
            json = DWZJsonUtils.getJson("200", "\\u64cd\\u4f5c\\u6210\\u529f", navTab_Role, "forward", role_url + "?unitId=" + unitId + "&unitname=" + unitname + "&fid=" + fid);
        }
        return json;
    }

    @RequestMapping(value = "listAdmins")
    @RequiresPermissions(value = "safj")
    public String listAdmins(ModelMap model, @RequestParam(required = false) String pageNum, @RequestParam(required = false) String numPerPage, @RequestParam(required = false) String searchWord, Long unitId, String unitname, @RequestParam(value = "fid") Integer fid) {
        if (pageNum == null) {
            pageNum = CXConstants.global_pageNum;
        }
        if (numPerPage == null) {
            numPerPage = CXConstants.global_numPerPage;
        }
        Pager pager = rolesService.listAdmins(Integer.parseInt(pageNum) - 1, Integer.parseInt(numPerPage), searchWord, unitId);
        long total = pager.getTotal();
        model.addAttribute("pager", pager);
        model.addAttribute("total", total);
        if (!StringUtils.isEmpty(searchWord)) {
            model.addAttribute("searchWord", searchWord);
        }
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("numPerPage", numPerPage);
        model.addAttribute("unitId", unitId);
        model.addAttribute("unitname", unitname);
        model.addAttribute("fid", fid);
        return "manage/RolesController/listAdmins";
    }

    @RequestMapping(value = "addAdmin")
    @RequiresPermissions(value = "safj-1")
    public String addAdmin(Long unitId, ModelMap modelMap, String unitname, @RequestParam(value = "fid") Integer fid) {
        modelMap.addAttribute("unitId", unitId);
        modelMap.addAttribute("unitname", unitname);
        modelMap.addAttribute("fid", fid);
        return "manage/RolesController/addAdmin";
    }

    @RequestMapping(value = "suggestRoles")
    public String suggestRoles(Long unitId, ModelMap model, @RequestParam(required = false) String pageNum, @RequestParam(required = false) String numPerPage, @RequestParam(required = false) String searchWord, @RequestParam(value = "fid") Integer fid) {
        if (pageNum == null) {
            pageNum = CXConstants.global_pageNum;
        }
        if (numPerPage == null) {
            numPerPage = CXConstants.global_numPerPage;
        }
        Pager pager = rolesService.listAdminRoles(Integer.parseInt(pageNum) - 1, Integer.parseInt(numPerPage), searchWord, unitId, fid);
        long total = pager.getTotal();
        model.addAttribute("pager", pager);
        model.addAttribute("total", total);
        if (!StringUtils.isEmpty(searchWord)) {
            model.addAttribute("searchWord", searchWord);
        }
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("numPerPage", numPerPage);
        model.addAttribute("unitId", unitId);
        model.addAttribute("fid", fid);
        return "manage/RolesController/suggestRoles";
    }

    @RequestMapping(value = "editAdmin")
    @RequiresPermissions(value = "safj-2")
    public String editAdmin(Long id, ModelMap modelMap) {
        Optional<Admin> optional = rolesService.findAdminById(id);
        Admin admin = optional.get();
        Long unitId = admin.getUnitId();
        Integer fid = admin.getFid();
        String unitname = admin.getUnitname();
        modelMap.addAttribute("fid", fid);
        modelMap.addAttribute("unitId", unitId);
        modelMap.addAttribute("unitname", unitname);
        modelMap.addAttribute("admin", admin);
        String roleIds = "";
        String roleNames = "";
        List<Roles> rolesList = admin.getRoles();
        if (rolesList != null) {
            for (Roles role : rolesList) {
                roleIds += role.getId() + ",";
                roleNames += role.getRolename() + ",";
            }
            modelMap.addAttribute("roleIds", roleIds);
            modelMap.addAttribute("roleNames", roleNames);
        }
        return "manage/RolesController/addAdmin";
    }

    @RequestMapping(value = "saveAdmin")
    @ResponseBody
    @RequiresPermissions(value = {"safj-2", "safj-1"}, logical = Logical.OR)
    public String saveAdmin(Admin admin, String roleIds) {
        Long unitId = admin.getUnitId();
        String unitname = admin.getUnitname();
        Integer fid = admin.getFid();
        String json = DWZJsonUtils.getJson("300", "\\u64cd\\u4f5c\\u5931\\u8d25", navTab_admin, "closeCurrent", admin_url + "?unitId=" + unitId + "&unitname=" + unitname + "&fid=" + fid);
        if (admin.getId() == null && adminService.findAdminByName(admin.getAdminname()) != null) {
            json = DWZJsonUtils.getJson("300", "用户名已存在，请重新填写!", navTab_admin, "closeCurrent", admin_url + "?unitId=" + unitId + "&unitname=" + unitname + "&fid=" + fid);
            return json;
        }
        if (rolesService.saveAdmin(admin, roleIds) != null) {
            json = DWZJsonUtils.getJson("200", "\\u64cd\\u4f5c\\u6210\\u529f", navTab_admin, "closeCurrent", admin_url + "?unitId=" + unitId + "&unitname=" + unitname + "&fid=" + fid);
        }
        return json;
    }

    @RequestMapping(value = "delAdmin")
    @ResponseBody
    @RequiresPermissions(value = "safj-3")
    public String delAdmin(String ids, Long unitId, String unitname, @RequestParam(value = "fid") Integer fid) {
        String json = DWZJsonUtils.getJson("300", "\\u64cd\\u4f5c\\u5931\\u8d25", navTab_admin, "forward", admin_url + "?unitId=" + unitId + "&unitname=" + unitname + "&fid=" + fid);
        if (rolesService.delAdmin(ids)) {
            json = DWZJsonUtils.getJson("200", "\\u64cd\\u4f5c\\u6210\\u529f", navTab_admin, "forward", admin_url + "?unitId=" + unitId + "&unitname=" + unitname + "&fid=" + fid);
        }
        return json;
    }

    @RequestMapping(value = "listRolesDetail")
    public String listRolesDetail(Long id, ModelMap modelMap) {
        Pager pager = rolesService.listRolesDetail(id);
        long total = pager.getTotal();
        modelMap.addAttribute("pager", pager);
        return "manage/RolesController/rolesDetail";
    }
}
