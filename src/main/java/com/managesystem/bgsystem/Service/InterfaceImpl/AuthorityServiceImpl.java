package com.managesystem.bgsystem.Service.InterfaceImpl;

import com.managesystem.bgsystem.Model.Entity.Authority;
import com.managesystem.bgsystem.Model.Entity.Organizations;
import com.managesystem.bgsystem.Model.Entity.Roles;
import com.managesystem.bgsystem.Model.Pojo.Pager;
import com.managesystem.bgsystem.RepositoryInterface.AuthorityRepository;
import com.managesystem.bgsystem.RepositoryInterface.OrganizationsRepository;
import com.managesystem.bgsystem.RepositoryInterface.RolesRepository;
import com.managesystem.bgsystem.Service.Interface.AuthorityService;
import com.managesystem.bgsystem.Utils.AdminUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorityServiceImpl implements AuthorityService {
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private OrganizationsRepository organizationsRepository;
    @Autowired
    private RolesRepository rolesRepository;

    @Override
    public Pager listAuthority(int pageNum, int numPerPage, String searchWord) {
        Page<Authority> authorities;
        long total;
        PageRequest pageable = PageRequest.of(pageNum, numPerPage, Sort.Direction.DESC, "updated");
        ExampleMatcher matcher = ExampleMatcher.matching();
        Authority authority = new Authority();
        if (!StringUtils.isEmpty(searchWord)) {
            authority.setAuthorityname(searchWord);
            matcher = matcher.withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());

        }
        Example example = Example.of(authority, matcher);
        authorities = authorityRepository.findAll(example, pageable);
        total = authorityRepository.count(example);
        Pager pager = new Pager(pageNum, numPerPage, total, authorities);
        return pager;
    }


    @Override
    public Pager listSuggestAuthority(int pageNum, int numPerPage, String searchWord, Long unitId) {
        Page<Authority> authorities;
        long total;
        PageRequest pageable = PageRequest.of(pageNum, numPerPage, Sort.Direction.DESC, "updated");
        ExampleMatcher matcher = ExampleMatcher.matching();
        if (unitId == null) {
            unitId = AdminUtils.getUnitId();
        }
        Organizations org = organizationsRepository.findById(unitId).get();
        Long parentOrgID = org.getParentID();
        if (parentOrgID == 0) {
            Authority authority = new Authority();
            if (!StringUtils.isEmpty(searchWord)) {
                authority.setAuthorityname(searchWord);
                matcher = matcher.withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());

            }
            Example example = Example.of(authority, matcher);
            authorities = authorityRepository.findAll(example, pageable);
            total = authorityRepository.count(example);
            Pager pager = new Pager(pageNum, numPerPage, total, authorities);
            return pager;
        } else {
            Roles r = new Roles();
            r.setUnitId(parentOrgID);
            matcher = matcher.withMatcher("unitId", ExampleMatcher.GenericPropertyMatchers.exact());
            Example example = Example.of(r, matcher);
            List<Authority> authorityList = new ArrayList<>();
            List<Roles> rolesList = rolesRepository.findAll(example, pageable).getContent();
            for (Roles role : rolesList
            ) {
                authorityList.add(role.getAuthority());
            }
            total = rolesList.size();
            Pager pager = new Pager(pageNum, numPerPage, total, authorityList);
            return pager;
        }

    }

    @Override
    public Boolean delAuthority(String ids) {
        if (!StringUtils.isEmpty(ids)) {
            for (String id : ids.split(",")
            ) {
                if (!StringUtils.isEmpty(id)) {
                    authorityRepository.deleteById(Long.parseLong(id.trim()));
                }
            }
        }
        return true;
    }

    @Override
    public Authority saveAuthority(Authority authority) {
        if (authority.getCreated() == null) {
            authority.setCreated(new Date());
        }
        Authority authority1 = authorityRepository.save(authority);
        return authority1;
    }

    @Override
    public Optional<Authority> findById(long id) {
        if (id != 0) {
            Optional<Authority> authority = authorityRepository.findById(id);
            return authority;
        }
        return null;
    }
}
