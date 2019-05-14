package com.managesystem.bgsystem.Service.InterfaceImpl;

import cn.hutool.crypto.SecureUtil;
import com.managesystem.bgsystem.Model.Entity.Admin;
import com.managesystem.bgsystem.Model.Entity.Organizations;
import com.managesystem.bgsystem.Model.Pojo.Pager;
import com.managesystem.bgsystem.RepositoryInterface.AdminRepository;
import com.managesystem.bgsystem.RepositoryInterface.OrganizationsRepository;
import com.managesystem.bgsystem.Service.Interface.AdminService;
import com.managesystem.bgsystem.Utils.AdminUtils;
import com.managesystem.bgsystem.Utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private OrganizationsRepository organizationsRepository;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Pager listAdmins(int pageNum, int numPerPage, String searchWord, Integer fid) {
        Page<Admin> admins = null;
        long total;
        PageRequest pageable = PageRequest.of(pageNum, numPerPage, Sort.Direction.DESC, "updated");
        if (AdminUtils.isSuperAdmin()) {
            admins = adminRepository.findAll(pageable);
        } else {
            Specification specification = new Specification<Admin>() {
                @Override
                public Predicate toPredicate(Root<Admin> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("fid"));
                    String childFids = "childFids_" + AdminUtils.getUnitId();
                    List<Integer> fidList = redisUtils.get(childFids, List.class);
                    for (Integer fid : fidList
                    ) {
                        in.value(fid);
                    }
                    if (!StringUtils.isEmpty(searchWord)) {
                        Predicate adminname = criteriaBuilder.like(root.get("adminname"), searchWord);
                        return criteriaBuilder.and(in, adminname);
                    } else {
                        return criteriaBuilder.and(in);
                    }
                }
            };
            admins = adminRepository.findAll(specification, pageable);
        }

        total = admins.getTotalElements();
        Pager pager = new Pager(pageNum, numPerPage, total, admins);
        return pager;
    }

    @Override
    public Integer saveAdmin(Admin admin) {
        int code = 200;
        Admin adminD = adminRepository.findById(admin.getId()).get();
        String password = admin.getPassword();
        password = SecureUtil.sha1(SecureUtil.md5(password));
        adminD.setStatus(admin.getStatus());
        adminD.setType(1);
        adminD.setPassword(password);
        try {
            adminRepository.save(adminD);
        } catch (Exception e) {
            e.printStackTrace();
            code = 300;
        }
        return code;
    }

    @Override
    public Admin findAdminByName(String adminname) {
        Admin admin = new Admin();
        admin.setAdminname(adminname);
        admin.setStatus(0);
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("adminname", ExampleMatcher.GenericPropertyMatchers.exact()).withMatcher("status", ExampleMatcher.GenericPropertyMatchers.exact());
        Example example = Example.of(admin, matcher);
        Optional<Admin> optionalAdmin = adminRepository.findOne(example);
        Admin admin1 = null;
        if (optionalAdmin.isPresent())
            admin1 = optionalAdmin.get();
        return admin1;
    }

    @Override
    public Boolean delAdmin(String ids) {
        if (!StringUtils.isEmpty(ids)) {
            for (String id : ids.split(",")
            ) {
                adminRepository.deleteById(Long.parseLong(id.trim()));
            }
        }
        return true;
    }

    @Override
    public Optional<Admin> findAdminById(long id) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        return optionalAdmin;
    }


    @Override
    public Integer getSuperFid(Long unitId) {

        Integer superFid = 0;
        if (unitId == null) return null;
        while (superFid == 0) {
            Organizations organizations = organizationsRepository.findById(unitId).get();
            if (organizations != null && organizations.getParentID() != 0) {
                unitId = organizations.getParentID();
            } else {
                superFid = organizations.getFid();
            }
        }
        return superFid;
    }

    @Override
    public void delOrgAdmins(Long orgId) {
        List<Admin> adminList = adminRepository.findAdminsByorgId(orgId);
        if (adminList != null && adminList.size() != 0) {
            for (Admin a : adminList
            ) {
                adminRepository.delete(a);
            }
        }
    }

    @Override
    public void saveDirectAdmin(Admin admin) {
        adminRepository.save(admin);
    }
}
