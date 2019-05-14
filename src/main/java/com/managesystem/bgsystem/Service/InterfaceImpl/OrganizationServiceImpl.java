package com.managesystem.bgsystem.Service.InterfaceImpl;
import com.managesystem.bgsystem.Model.Entity.Admin;
import com.managesystem.bgsystem.Model.Entity.Organizations;
import com.managesystem.bgsystem.Model.Pojo.Pager;
import com.managesystem.bgsystem.RepositoryInterface.OrganizationsRepository;
import com.managesystem.bgsystem.Service.Interface.AdminService;
import com.managesystem.bgsystem.Service.Interface.OrganizationService;
import com.managesystem.bgsystem.Service.Interface.RolesService;
import com.managesystem.bgsystem.Utils.AdminUtils;
import com.managesystem.bgsystem.Utils.SpecificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    @Autowired
    private OrganizationsRepository organizationsRepository;
    @Autowired
    private AdminService adminService;
    @Autowired
    private RolesService rolesService;

    @Override
    public Pager listOrganizations(int pageNum, int numPerPage, String searchWord, Long parentID) {
        Page<Organizations> organizations;
        long total;
        PageRequest pageable = PageRequest.of(pageNum, numPerPage, Sort.Direction.DESC, "updated");
      /*  Specification<Organizations> specification = new Specification<Organizations>() {
            @Override
            public Predicate toPredicate(Root<Organizations> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = null;
                Predicate predicate1 = null;
                Predicate predicate2 = null;
                Predicate predicate3 = null;
                if (parentID != null) {
                    Path path2 = root.get("parentID");
                    predicate2 = cb.equal(path2, parentID);
                } else {
                    Path path1 = root.get("parentID");
                    predicate2 = cb.equal(path1, 0);
                }
                if (!StringUtils.isEmpty(searchWord)) {
                    Path path0 = root.get("name");
                    predicate1 = cb.equal(path0, searchWord);
                }
                Boolean isSuprerAdmin = AdminUtils.isSuperAdmin();
                if (!isSuprerAdmin) {
                    Admin admin = AdminUtils.getAdmin();
                    Long orgId = admin.getUnitId();
                    List<Long> orgList = findChildrenOrgs(orgId, new ArrayList<>());
                    orgList.add(orgId);
                    Predicate path3 = root.in("parentID", orgList);
                    predicate3 = cb.in(path3);
                }
                if (searchWord != null && !isSuprerAdmin) {
                    predicate = cb.and(predicate1, predicate2, predicate3);
                } else if (searchWord == null && !isSuprerAdmin) {
                    predicate = cb.and(predicate2, predicate3);
                } else if (isSuprerAdmin && searchWord == null) {
                    predicate = predicate2;
                } else if (isSuprerAdmin && searchWord != null) {
                    predicate = cb.and(predicate1, predicate2);
                }
                return predicate;
            }
        };
        organizations = organizationsRepository.findAll(specification, pageable);
        total = organizations.getTotalElements();*/
        Organizations organization = new Organizations();
        ExampleMatcher matcher = ExampleMatcher.matching();
        if (!StringUtils.isEmpty(searchWord)) {
            organization.setName(searchWord);
            matcher = matcher.withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        }
        if (parentID != null) {
            organization.setParentID(parentID);
        } else {
            if (AdminUtils.getAdminType() != 255) {
                organization.setId(AdminUtils.getUnitId());
                matcher = matcher.withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact());
            } else {
                organization.setParentID(0L);
            }
        }
        matcher = matcher.withMatcher("parentID", ExampleMatcher.GenericPropertyMatchers.exact());
        Example example = Example.of(organization, matcher);
        organizations = organizationsRepository.findAll(example, pageable);
        total = organizations.getTotalElements();
        Pager pager = new Pager(pageNum, numPerPage, total, organizations);
        return pager;
    }

    private List<Long> findChildrenOrgs(Long orgId, List<Long> orgList) {
        List<Organizations> organizationsList = organizationsRepository.findChildOrgs(orgId);
        if (organizationsList.size() > 0) {
            for (Organizations o : organizationsList
                    ) {
                orgList.add(o.getId());
                findChildrenOrgs(o.getId(), orgList);
            }
        } else {
            return orgList;
        }
        return null;
    }

    @Override
    public Integer saveOrganization(Organizations org) {
        Admin admin = AdminUtils.getAdmin();
        if (admin.getType() == 255) {
            org.setParentID(0L);
        } else if (admin.getType() != 255 && org.getParentID() == null) {
            return 300;
        }
        if (org.getCreated() == null) {
            org.setCreated(new Date());
        }
        Organizations orga = organizationsRepository.save(org);
        return 200;
    }

    @Override
    public Optional<Organizations> findOrganizationById(long id) {
        if (id != 0) {
            Optional<Organizations> org = organizationsRepository.findById(id);
            return org;
        }
        return null;
    }

    @Override
    public String delOrganizations(String ids) {

        Boolean isSuperAdmid = AdminUtils.isSuperAdmin();
        String msg = "";
        if (!StringUtils.isEmpty(ids)) {
            for (String id : ids.split(",")) {
                Long idLong = Long.parseLong(id.trim());
                Organizations org = organizationsRepository.findById(idLong).get();
                if (!isSuperAdmid && org.getParentID() == 0) {
                    msg += "组织ID:" + org.getId() + "不可删除,";
                    continue;
                }
                if (!hasChildren(idLong)) {
                    // 超级管理员   && 没有下级机构  && 没有账号  && 没有代表  &&  没有相关活动   才可以删除
                    adminService.delOrgAdmins(org.getId());
                    rolesService.delOrgRoles(org.getId());
                    organizationsRepository.delete(org);
                } else {
                    msg += "组织ID:" + org.getId() + "不可删除,";
                }
            }
        }
        return msg;
    }


    @Override
    public Boolean hasChildren(Long id) {

        if (id == null) {
            return false;
        }
        Organizations org = organizationsRepository.findById(id).get();

        if (org == null) {
            return false;
        }

        Organizations condition = new Organizations();
        condition.setParentID(id);

        ExampleMatcher matcher = ExampleMatcher.matching();
        matcher = matcher.withMatcher("parentID", ExampleMatcher.GenericPropertyMatchers.exact());
        Example example = Example.of(condition, matcher);

        Optional<Organizations> organization = organizationsRepository.findOne(example);

        if (organization.isPresent()) {
            return true;
        }

        return false;

    }


    @Override
    public List<Organizations> getAll() {

        /*  这里代码我也不知道怎么写。      //  通过递归获取该untiId的所有子机构 */
        Admin admin = AdminUtils.getAdmin();

        if (admin.getAdminname().equals("root")) {
            SpecificationBuilder specificationBuilder = new SpecificationBuilder();
            specificationBuilder.addMatch("parentID", "!=", 0);
            Specification<Organizations> specification = specificationBuilder.getSpecification();
            return organizationsRepository.findAll(specification);
        }

        Long untiId = admin.getUnitId();
        Organizations organization = getOne(untiId);

        return getChildren(organization);
    }

    @Override
    public List<Long> getAllOrgId() {
        List<Organizations> organizationsList = getAll();
        if (organizationsList != null && organizationsList.size() > 0) {
            ArrayList<Long> ids = new ArrayList<>(organizationsList.size());
            for (Organizations organizations : organizationsList) {
                ids.add(organizations.getId());
            }
            return ids;
        }
        return null;
    }


    @Override
    public List<String> getOrgNameList() {
        List<Organizations> organizationsList = getAll();
        if (organizationsList != null && organizationsList.size() > 0) {
            ArrayList<String> orgNameLsit = new ArrayList<>(organizationsList.size());
            for (Organizations organizations : organizationsList) {
                orgNameLsit.add(organizations.getName());
            }
            return orgNameLsit;
        }
        return null;
    }


    @Override
    public Organizations getOne(Long id) {
        return organizationsRepository.getOne(id);
    }

    @Override
    public Organizations getRootOrganizations(Long fid) {
        SpecificationBuilder specificationBuilder = new SpecificationBuilder();
        specificationBuilder.addMatch("fid", "=", fid);
        specificationBuilder.addMatch("parentID", "=", 0);
        Specification<Organizations> spec = specificationBuilder.getSpecification();
        Optional<Organizations> organizations = organizationsRepository.findOne(spec);
        if (organizations != null) {
            return organizations.get();
        }
        return null;
    }


    @Override
    public List<Organizations> getAllByFid(Long fid) {
        Organizations organizations = getRootOrganizations(fid);
        if (organizations != null) {
            return getChildren(organizations);
        }
        return null;
    }

    @Override
    public List<Organizations> getChildren(Organizations organization) {

        List<Organizations> organizationsList = new ArrayList<>();
        organizationsList.add(organization);

        SpecificationBuilder specificationBuilder = new SpecificationBuilder();
        specificationBuilder.addMatch("parentID", "=", organization.getId());
        Specification<Organizations> spec = specificationBuilder.getSpecification();

        List<Organizations> tmp = organizationsRepository.findAll(spec);
        for (Organizations organizations : tmp) {
            List<Organizations> children = getChildren(organizations);
            organizationsList.addAll(children);
        }

        return organizationsList;
    }

    @Override
    public List<Organizations> findAllUnitname(Integer fid) {
        List<Organizations> organizations = organizationsRepository.findAllUnitnameByFid(fid);
        List<Organizations> organizationsList = null;
        if (organizations.size() > 0) {
            Long parentId = organizations.get(0).getParentID();
            if (parentId != 0) {
                organizationsList = findFirstOrgId(parentId);
            }
            organizationsList.addAll(organizations);
        }
        return organizationsList;
    }

    private List<Organizations> findFirstOrgId(Long orgID) {
        List<Organizations> organizationsList = new ArrayList<>();
        int size = 0;
        while (size == 0) {
            Organizations organizations = organizationsRepository.findById(orgID).get();
            if (organizations != null && organizations.getParentID() != 0) {
                organizationsList.add(organizations);
            } else {
                organizationsList.add(organizations);
                size = 1;
            }
        }
        return organizationsList;
    }


    @Override
    public List<Organizations> findAllChildUnit(Long unitId) {
        /*if (unitId == null) return null;
        String key = unitId + "childOrgs";
        List<Organizations> orgList = (List<Organizations>) redisUtils.get(key);
        if (orgList == null) {
            Organizations org = organizationsRepository.findById(unitId).get();
            List<Organizations> organizationsChildList = new ArrayList<>();
            organizationsChildList = findBelowOrgs(unitId, organizationsChildList);
            organizationsChildList.add(org);
            orgList = organizationsChildList;
            redisUtils.set(key, organizationsChildList);
            redisUtils.expire(key,1*60*60);
        }
        return orgList;*/
        if (unitId == null) return null;
        Organizations org = organizationsRepository.findById(unitId).get();
        List<Organizations> organizationsChildList = new ArrayList<>();
        organizationsChildList = findBelowOrgs(unitId, organizationsChildList);
        organizationsChildList.add(org);
        return organizationsChildList;
    }

    @Override
    public Boolean isRoot(Long id) {
        if (id == null) {
            return Boolean.TRUE;
        }
        Optional<Organizations> optionalOrganizations = organizationsRepository.findById(id);
        if (optionalOrganizations.isPresent()) {
            return optionalOrganizations.get().getParentID() == 0;
        } else {
            return Boolean.TRUE;
        }
    }

    private List<Organizations> findBelowOrgs(Long id, List<Organizations> organizationsChildList) {
        List<Organizations> organizations = organizationsRepository.findChildOrgs(id);
        if (organizations.size() > 0) {
            for (Organizations org : organizations
                    ) {
                if (org.getParentID() != 0) {
                    organizationsChildList.add(org);
                    findBelowOrgs(org.getId(), organizationsChildList);
                }
            }
        }
        return organizationsChildList;
    }

    @Override
    public List<Integer> findAllChildFid(Long unitId) {
        if (unitId == null) return null;
        Organizations org = organizationsRepository.findById(unitId).get();
        List<Integer> fidList = new ArrayList<>();
        fidList = findBelowOrgsFid(unitId, fidList);
        fidList.add(org.getFid());
        return fidList;
    }

    private List<Integer> findBelowOrgsFid(Long id, List<Integer> fidList) {
        List<Organizations> organizations = organizationsRepository.findChildOrgs(id);
        if (organizations.size() > 0) {
            for (Organizations org : organizations
                    ) {
                if (org.getParentID() != 0) {
                    fidList.add(org.getFid());
                    findBelowOrgsFid(org.getId(), fidList);
                }
            }
        }
        return fidList;
    }
}
