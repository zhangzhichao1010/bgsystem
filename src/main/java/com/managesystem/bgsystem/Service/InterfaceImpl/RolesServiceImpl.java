package com.managesystem.bgsystem.Service.InterfaceImpl;

import cn.hutool.crypto.SecureUtil;
import com.managesystem.bgsystem.Model.Entity.Admin;
import com.managesystem.bgsystem.Model.Entity.Authority;
import com.managesystem.bgsystem.Model.Entity.Roles;
import com.managesystem.bgsystem.Model.Pojo.Pager;
import com.managesystem.bgsystem.RepositoryInterface.AdminRepository;
import com.managesystem.bgsystem.RepositoryInterface.AuthorityRepository;
import com.managesystem.bgsystem.RepositoryInterface.RolesRepository;
import com.managesystem.bgsystem.Service.Interface.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RolesServiceImpl implements RolesService {
    @Autowired
    private RolesRepository rolesRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public Pager listRoles(int pageNum, int numPerPage, String searchWord, Long unitId) {
        Page<Roles> roles;
        long total;
        PageRequest pageable = PageRequest.of(pageNum, numPerPage, Sort.Direction.DESC, "updated");
        Roles role = new Roles();
        role.setUnitId(unitId);
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("unitId", ExampleMatcher.GenericPropertyMatchers.exact());
        if (!StringUtils.isEmpty(searchWord)) {
            role.setRolename(searchWord);
            matcher = matcher.withMatcher("rolename", ExampleMatcher.GenericPropertyMatchers.contains());
        }
        Example example = Example.of(role, matcher);
        roles = rolesRepository.findAll(example, pageable);
        total = rolesRepository.count(example);
        Pager pager = new Pager(pageNum, numPerPage, total, roles);
        return pager;
    }

    @Override
    public Roles saveRole(Roles role, Long authId) {
        if (role.getCreated() == null) {
            role.setCreated(new Date());
        }
        if (authId != null) {
            Authority authority = authorityRepository.findById(authId).get();
            role.setAuthority(authority);
        }
        Roles r = rolesRepository.save(role);
        return r;
    }

    @Override
    public Optional<Roles> findRoleById(long id) {
        if (id != 0) {
            Optional<Roles> role = rolesRepository.findById(id);
            return role;
        }
        return null;
    }

    @Override
    public Boolean delRoles(String ids) {
        if (!StringUtils.isEmpty(ids)) {
            for (String id : ids.split(",")
            ) {
                rolesRepository.deleteById(Long.parseLong(id.trim()));
            }
        }
        return true;
    }

    @Override
    public Pager listAdminRoles(int pageNum, int numPerPage, String searchWord, Long unitId, Integer fid) {
        Page<Roles> roles;
        long total;
        PageRequest pageable = PageRequest.of(pageNum, numPerPage, Sort.Direction.DESC, "updated");
        Roles role = new Roles();
        role.setUnitId(unitId);
        role.setFid(fid);
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("unitId", ExampleMatcher.GenericPropertyMatchers.exact()).withIgnorePaths("password").withMatcher("fid", ExampleMatcher.GenericPropertyMatchers.exact());
        if (!StringUtils.isEmpty(searchWord)) {
            role.setRolename(searchWord);
            matcher = matcher.withMatcher("rolename", ExampleMatcher.GenericPropertyMatchers.contains());
        }
        Example example = Example.of(role, matcher);
        roles = rolesRepository.findAll(example, pageable);
        total = rolesRepository.count(example);
        Pager pager = new Pager(pageNum, numPerPage, total, roles);
        return pager;
    }

    @Override
    public Admin saveAdmin(Admin admin, String roleIds) {
        if (admin.getCreated() == null) {
            admin.setCreated(new Date());
            admin.setType(1);
        }
        String password = admin.getPassword();
        if (!StringUtils.isEmpty(password)) {
            password = SecureUtil.sha1(SecureUtil.md5(password));
            admin.setPassword(password);
        }
        List<Roles> rolesList = new ArrayList<>();

        if (!StringUtils.isEmpty(roleIds)) {
            for (String roleId : roleIds.split(",")
            ) {
                Roles role = rolesRepository.findById(Long.parseLong(roleId.trim())).get();
                rolesList.add(role);
            }
            admin.setRoles(rolesList);
            Admin r = adminRepository.save(admin);
            return r;
        }
        return null;
    }

    @Override
    public Pager listAdmins(int pageNum, int numPerPage, String searchWord, Long unitId) {
        Page<Admin> admins;
        long total;
        PageRequest pageable = PageRequest.of(pageNum, numPerPage, Sort.Direction.DESC, "updated");
        Admin admin = new Admin();
        admin.setUnitId(unitId);
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("unitId", ExampleMatcher.GenericPropertyMatchers.caseSensitive()).withIgnorePaths("password");
        if (!StringUtils.isEmpty(searchWord)) {
            admin.setAdminname(searchWord);
            matcher = matcher.withMatcher("adminname", ExampleMatcher.GenericPropertyMatchers.contains());
        }
        Example example = Example.of(admin, matcher);
        admins = adminRepository.findAll(example, pageable);
        total = adminRepository.count(example);
        Pager pager = new Pager(pageNum, numPerPage, total, admins);
        return pager;
    }

    @Override
    public Optional<Admin> findAdminById(long id) {
        if (id != 0) {
            Optional<Admin> admin = adminRepository.findById(id);
            return admin;
        }
        return null;
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
    public Pager listRolesDetail(Long id) {
        Admin admin = adminRepository.findById(id).get();
        List<Roles> roles = admin.getRoles();
        long total = roles.size();
        Pager pager = new Pager(1, 10, total, roles);
        return pager;
    }

    @Override
    public void delOrgRoles(Long orgId) {
        List<Roles> rolesList = rolesRepository.findRolesByorgId(orgId);
        if (rolesList != null && rolesList.size() != 0) {
            for (Roles a : rolesList
            ) {
                rolesRepository.delete(a);
            }
        }
    }
}
