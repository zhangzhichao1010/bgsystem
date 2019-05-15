package com.managesystem.bgsystem.Controller.manage;

import com.managesystem.bgsystem.Model.Entity.Authority;
import com.managesystem.bgsystem.Model.Pojo.Pager;
import com.managesystem.bgsystem.Service.Interface.AuthorityService;
import com.managesystem.bgsystem.Utils.CXConstants;
import com.managesystem.bgsystem.Utils.DWZJsonUtils;
import org.apache.shiro.authz.annotation.Logical;
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

import java.util.Optional;

/*
 * design all by zhichao zhang 20190417
 * 权限管理模块，权限批量管理
 *
 * */
@Controller
@RequestMapping("manage/AuthorityController")
public class AuthorityController {
    String navTab = "authorityManage";
    Logger logger = LoggerFactory.getLogger(AuthorityController.class);
    String url = "/manage/AuthorityController/listAuthority";
    @Autowired
    private AuthorityService authorityService;

    @RequiresPermissions(value = {"xwgl"})
    @RequestMapping(value = "listAuthority")
    public String listAuthority(ModelMap model, @RequestParam(required = false) String pageNum, @RequestParam(required = false) String numPerPage, @RequestParam(required = false) String searchWord) {
        if (pageNum == null) {
            pageNum = CXConstants.global_pageNum;
        }
        if (numPerPage == null) {
            numPerPage = CXConstants.global_numPerPage;
        }
        Pager pager = authorityService.listAuthority(Integer.parseInt(pageNum) - 1, Integer.parseInt(numPerPage), searchWord);
        long total = pager.getTotal();
        model.addAttribute("pager", pager);
        model.addAttribute("total", total);
        if (!StringUtils.isEmpty(searchWord)) {
            model.addAttribute("searchWord", searchWord);
        }
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("numPerPage", numPerPage);
        return "manage/AuthorityController/listAuthority";
    }

    @RequiresPermissions(value = {"xwgl-1"})
    @RequestMapping(value = "addAuthority")
    public String addAuthority() {
        return "manage/AuthorityController/privilege";
    }

    @RequiresPermissions(value = {"xwgl-1", "xwgl-2"}, logical = Logical.OR)
    @RequestMapping(value = "saveAuthority")
    @ResponseBody
    public String saveAuthority(Authority authority) {
        String json = DWZJsonUtils.getJson("300", "\\u64cd\\u4f5c\\u5931\\u8d25", navTab, "closeCurrent", url);
        if (authorityService.saveAuthority(authority) != null) {
            json = DWZJsonUtils.getJson("200", "\\u64cd\\u4f5c\\u6210\\u529f", navTab, "closeCurrent", url);
        }
        return json;
    }

    @RequiresPermissions(value = {"xwgl-2"})
    @RequestMapping(value = "editAuthority")
    public String editAuthority(ModelMap modelMap, @RequestParam Long id) {
        Optional<Authority> optional = authorityService.findById(id);
        Authority authority = optional.get();
        modelMap.addAttribute("authority", authority);
        return "manage/AuthorityController/privilege";
    }

    @RequiresPermissions(value = {"xwgl-3"})
    @RequestMapping(value = "delAuthority")
    @ResponseBody
    public String delAuthority(String ids) {
        String json = DWZJsonUtils.getJson("300", "\\u64cd\\u4f5c\\u5931\\u8d25", navTab, "forward", url);
        if (authorityService.delAuthority(ids)) {
            json = DWZJsonUtils.getJson("200", "\\u64cd\\u4f5c\\u6210\\u529f", navTab, "forward", url);
        }
        return json;
    }
}
