package com.managesystem.bgsystem.Service.Interface;

import com.managesystem.bgsystem.Model.Entity.Admin;
import com.managesystem.bgsystem.Model.Entity.Roles;
import com.managesystem.bgsystem.Model.Pojo.Pager;

import java.util.Optional;

public interface RolesService {
    Pager listRoles(int pageNum, int numPerPage, String searchWord, Long unitId);

    Roles saveRole(Roles role, Long authId);

    Optional<Roles> findRoleById(long id);

    Boolean delRoles(String ids);

    Admin saveAdmin(Admin admin, String roleIds);

    Pager listAdmins(int pageNum, int numPerPage, String searchWord, Long unitId);

    Optional<Admin> findAdminById(long id);

    Boolean delAdmin(String ids);

    Pager listRolesDetail(Long id);

    Pager listAdminRoles(int pageNum, int numPerPage, String searchWord, Long unitId, Integer fid);

    void delOrgRoles(Long orgId);

}
