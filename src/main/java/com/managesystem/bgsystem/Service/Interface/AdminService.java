package com.managesystem.bgsystem.Service.Interface;

import com.managesystem.bgsystem.Model.Entity.Admin;
import com.managesystem.bgsystem.Model.Pojo.Pager;

import java.util.Optional;

public interface AdminService {
    Pager listAdmins(int pageNum, int numPerPage, String searchWord, Integer fid);

    Integer saveAdmin(Admin admin);

    Boolean delAdmin(String ids);

    Optional<Admin> findAdminById(long id);

    Admin findAdminByName(String adminname);

    Integer getSuperFid(Long unitId);

    void delOrgAdmins(Long orgId);

    void saveDirectAdmin(Admin admin);
}
