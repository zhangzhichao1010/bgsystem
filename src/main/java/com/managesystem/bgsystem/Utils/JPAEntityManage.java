package com.managesystem.bgsystem.Utils;

import com.managesystem.bgsystem.Model.Pojo.PageData;
import com.managesystem.bgsystem.Model.Pojo.Pager;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JPAEntityManage<T> {
    Pager findAll(String sql, Class entity, PageData pageRequest, Object... params);

    Pager findAll(String sql, Class entity);

    Pager findAll(String sql, Class entity, Object... params);

    Pager findAll(String sql, Class entity, PageData pageData);

    void saveEntity(Class entity);

    void delEntity(Class entity);

    T updateEntity(Class entity);

    T findById(Class entity, Object primaryKey);

    Integer count(Class entity);

    T findOne(String sql, Class entity, Object... params);
}
