package com.managesystem.bgsystem.Service.Interface;

import com.managesystem.bgsystem.Model.Entity.Organizations;
import com.managesystem.bgsystem.Model.Pojo.Pager;

import java.util.List;
import java.util.Optional;

public interface OrganizationService {
    Pager listOrganizations(int pageNum, int numPerPage, String searchWord, Long parentID);

    Integer saveOrganization(Organizations org);

    String delOrganizations(String ids);

    Optional<Organizations> findOrganizationById(long id);

    Boolean hasChildren(Long id);

    List<Organizations> getAll();

    List<Organizations> getChildren(Organizations organizations);

    List<Long> getAllOrgId();

    List<String> getOrgNameList();

    Organizations getOne(Long id);

    Organizations getRootOrganizations(Long fid);

    List<Organizations> getAllByFid(Long fid);

    List<Organizations> findAllUnitname(Integer fid);

    List<Organizations> findAllChildUnit(Long uintId);

    Boolean isRoot(Long id);

    List<Integer> findAllChildFid(Long unitId);

}
