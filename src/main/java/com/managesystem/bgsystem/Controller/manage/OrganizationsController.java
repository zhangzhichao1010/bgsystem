package com.managesystem.bgsystem.Controller.manage;

import com.managesystem.bgsystem.Model.Entity.Organizations;
import com.managesystem.bgsystem.Model.Pojo.Pager;
import com.managesystem.bgsystem.Service.Interface.OrganizationService;
import com.managesystem.bgsystem.Utils.CXConstants;
import com.managesystem.bgsystem.Utils.DWZJsonUtils;
import com.zzz.ipmanage.Entity.FetchType;
import com.zzz.ipmanage.annotation.IPCheck;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
 * 组织管理模块：组织可以无限极添加，只有超级管理员添加一级目录，每级组织可以添加下属机构，
 * 权限分配实现逐级分配模式，子组织权限不得超过父组织，各级组织有直属管理，保证FID一致，数据层
 * 根据FID取对应数据
 *
 * */
@Controller
@RequestMapping(value = "manage/OrganizationsController")
@Slf4j
public class OrganizationsController {
    private final String navTab_Organization = "organizationManage";
    private final String org_url = "/manage/OrganizationsController/listOrganizations";
    @Autowired
    private OrganizationService organizationService;

    @RequestMapping(value = "listOrganizations")
    @RequiresPermissions(value = "wqew")
    @IPCheck(timesPerSecond = 5, fetch = FetchType.LAZY, allowTryTimes = 2)
    public String listOrganizations(ModelMap model, @RequestParam(required = false) String pageNum, @RequestParam(required = false) String numPerPage, @RequestParam(required = false) String searchWord,
                                    @RequestParam(required = false, value = "parentID") Long parentID, @RequestParam(required = false, value = "unitlevel") String unitlevel, @RequestParam(required = false) String fid) {
        if (pageNum == null) {
            pageNum = CXConstants.global_pageNum;
        }
        if (numPerPage == null) {
            numPerPage = CXConstants.global_numPerPage;
        }
        Pager pager = organizationService.listOrganizations(Integer.parseInt(pageNum) - 1, Integer.parseInt(numPerPage), searchWord, parentID);
        long total = pager.getTotal();
        model.addAttribute("pager", pager);
        model.addAttribute("total", total);
        if (!StringUtils.isEmpty(searchWord)) {
            model.addAttribute("searchWord", searchWord);
        }
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("numPerPage", numPerPage);
        model.addAttribute("fid", fid);
        model.addAttribute("parentID", parentID);
        if (StringUtils.isEmpty(unitlevel)) {
            unitlevel = "";
        } else {
            unitlevel = unitlevel.replaceAll("\\*", "&");
        }
        model.addAttribute("unitlevel", unitlevel);
        return "manage/OrganizationController/listOrganizations";
    }

    @RequestMapping(value = "addOrganization")
    @RequiresPermissions(value = "wqew-1")
    public String addOrganization(@RequestParam(required = false, value = "parentID") Long parentID, ModelMap modelMap, @RequestParam(required = false, value = "unitlevel") String unitlevel, @RequestParam(required = false) String fid) {
        modelMap.addAttribute("parentID", parentID);
        modelMap.addAttribute("unitlevel", unitlevel);
        modelMap.addAttribute("fid", fid);
        return "manage/OrganizationController/addOrganization";
    }

    @RequestMapping(value = "editOrganization")
    @RequiresPermissions(value = "wqew-2")
    public String editOrganization(@RequestParam(value = "id") long id, ModelMap modelMap, @RequestParam(required = false, value = "unitlevel") String unitlevel) {
        Optional<Organizations> optional = organizationService.findOrganizationById(id);
        Organizations org = optional.get();
        Long parentID = org.getParentID();
        Integer fid = org.getFid();
        modelMap.addAttribute("org", org);
        modelMap.addAttribute("fid", fid);
        modelMap.addAttribute("parentID", parentID);
        modelMap.addAttribute("unitlevel", unitlevel);
        return "manage/OrganizationController/addOrganization";
    }

    @ResponseBody
    @RequestMapping(value = "saveOrganization")
    @RequiresPermissions(value = {"wqew-1", "wqew-2"}, logical = Logical.OR)
    public String saveOrganization(Organizations organization, @RequestParam(required = false, value = "unitlevel") String unitlevel) {
        String json = "";
        Integer code = organizationService.saveOrganization(organization);
        if (code == 300) {
            json = DWZJsonUtils.getJson("300", "非超级管理员不可以添加同级机构", navTab_Organization, "closeCurrent", org_url + "?parentID=" + organization.getParentID() + "&unitlevel=" + unitlevel);
        } else if (code == 200) {
            json = DWZJsonUtils.getJson("200", "\\u64cd\\u4f5c\\u6210\\u529f", navTab_Organization, "closeCurrent", org_url + "?parentID=" + organization.getParentID() + "&unitlevel=" + unitlevel);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping(value = "delOrganization")
    @RequiresPermissions(value = "wqew-3")
    public String delOrganization(String ids, @RequestParam(required = false, value = "parentID") String parentID, @RequestParam(required = false, value = "unitlevel") String unitlevel) {
        String json = DWZJsonUtils.getJson("300", "\\u64cd\\u4f5c\\u5931\\u8d25", navTab_Organization, "forward", org_url + "?parentID=" + parentID + "&unitlevel=" + unitlevel);
        String msg = organizationService.delOrganizations(ids);
        if (StringUtils.isEmpty(msg)) {
            json = DWZJsonUtils.getJson("200", "\\u64cd\\u4f5c\\u6210\\u529f", navTab_Organization, "forward", org_url + "?parentID=" + parentID + "&unitlevel=" + unitlevel);
        } else {
            json = DWZJsonUtils.getJson("200", "不可删除：" + msg, navTab_Organization, "forward", org_url + "?parentID=" + parentID + "&unitlevel=" + unitlevel);
        }
        return json;
    }

}
