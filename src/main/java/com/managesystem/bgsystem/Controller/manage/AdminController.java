package com.managesystem.bgsystem.Controller.manage;

import com.managesystem.bgsystem.Model.Entity.Admin;
import com.managesystem.bgsystem.Model.Pojo.Pager;
import com.managesystem.bgsystem.Service.Interface.AdminService;
import com.managesystem.bgsystem.Service.Interface.RolesService;
import com.managesystem.bgsystem.Utils.AdminUtils;
import com.managesystem.bgsystem.Utils.CXConstants;
import com.managesystem.bgsystem.Utils.DWZJsonUtils;
import com.managesystem.bgsystem.Utils.JPAEntityManage;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.validation.constraints.NotNull;

/*
 * design all by zhichao zhang 20190417
 * 管理员快捷操作：允许修改，删除，锁定FID下管理员，需要权限操作
 *
 * */
@Controller
@RequestMapping(value = "manage/AdminController")
public class AdminController {
    Logger logger = LoggerFactory.getLogger(AuthorityController.class);
    private final String navTab = "adminController";
    private final String url = "/manage/AdminController/listAdmins";
    @Autowired
    private AdminService adminService;
    @Autowired
    private RolesService rolesService;
    @Autowired
    private JPAEntityManage entityUtils;

    @RequiresPermissions(value = {"wred"})
    @RequestMapping(value = "listAdmins")
    public String listAdmins(ModelMap model, @RequestParam(required = false) String pageNum, @RequestParam(required = false) String numPerPage, @RequestParam(required = false) String searchWord) {
        if (pageNum == null) {
            pageNum = CXConstants.global_pageNum;
        }
        if (numPerPage == null) {
            numPerPage = CXConstants.global_numPerPage;
        }
        Integer fid = AdminUtils.getFid();
        Pager pager = adminService.listAdmins(Integer.parseInt(pageNum) - 1, Integer.parseInt(numPerPage), searchWord, fid);
        long total = pager.getTotal();
        model.addAttribute("pager", pager);
        model.addAttribute("total", total);
        if (!StringUtils.isEmpty(searchWord)) {
            model.addAttribute("searchWord", searchWord);
        }
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("numPerPage", numPerPage);
        return "manage/AdminController/listAdmins";
    }

    @RequiresPermissions(value = {"wred-2"})
    @RequestMapping(value = "editAdmin")
    public String editAdmin(@NotNull Long id, ModelMap modelMap) {
        if (id != null) {
            Admin admin = adminService.findAdminById(id).get();
            modelMap.addAttribute("admin", admin);
        }
        return "manage/AdminController/addAdmin";
    }

    @RequiresPermissions(value = {"wred-2"})
    @RequestMapping(value = "saveAdmin")
    @ResponseBody
    public String saveAdmin(Admin admin) {
        String json = DWZJsonUtils.getJson("300", "用户名已存在，请重新输入！", navTab, "closeCurrent", url);
        if (adminService.saveAdmin(admin) == 200) {
            json = DWZJsonUtils.getJson("200", "\\u64cd\\u4f5c\\u6210\\u529f", navTab, "closeCurrent", url);
        }
        return json;
    }

    @RequiresPermissions(value = {"wred-3"})
    @RequestMapping(value = "delAdmin")
    @ResponseBody
    public String delAdmin(String ids) {
        String json = DWZJsonUtils.getJson("300", "\\u64cd\\u4f5c\\u5931\\u8d25", navTab, "forward", url);
        if (adminService.delAdmin(ids)) {
            json = DWZJsonUtils.getJson("200", "\\u64cd\\u4f5c\\u6210\\u529f", navTab, "forward", url);
        }
        return json;
    }

    @RequestMapping(value = "listRoles")
    public String listRoles(ModelMap modelMap, Long id) {
        Pager pager = rolesService.listRolesDetail(id);
        long total = pager.getTotal();
        modelMap.addAttribute("pager", pager);
        modelMap.addAttribute("total", total);
        return "manage/RolesController/rolesDetail";
    }
}
