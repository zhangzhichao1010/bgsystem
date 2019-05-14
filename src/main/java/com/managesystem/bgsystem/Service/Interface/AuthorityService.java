package com.managesystem.bgsystem.Service.Interface;

import com.managesystem.bgsystem.Model.Entity.Authority;
import com.managesystem.bgsystem.Model.Pojo.Pager;

import java.util.Optional;

public interface AuthorityService {
    public Pager listAuthority(int pageNum, int numPerPage, String searchWord);
    public Pager listSuggestAuthority(int pageNum, int numPerPage, String searchWord, Long unitId);
    public Authority saveAuthority(Authority school);
    public Boolean delAuthority(String ids);
    public Optional<Authority> findById(long id);
}
