package com.managesystem.bgsystem.Utils;

import com.managesystem.bgsystem.Model.Pojo.PageData;
import com.managesystem.bgsystem.Model.Pojo.Pager;
import com.managesystem.bgsystem.exception.NormalException;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class JPAEntity<T> implements JPAEntityManage<T> {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Pager findAll(String sql, Class entity, PageData pageRequest, Object... params) {
        Query query;
        if (sql.matches("^[select|Select|SELECT]")) {
            query = entityManager.createNativeQuery(sql, entity);
        } else {
            query = entityManager.createQuery(sql, entity);
        }
        if (pageRequest != null) {
            sql = pageDo(sql, pageRequest);
        }
        if (sql.indexOf("?1") != -1) {
            query = paramsDo(query, sql, params);
        }
        int total = count(entity);
        List datalist = query.getResultList();
        if (pageRequest != null) {
            return new Pager(pageRequest.getPageNum(), pageRequest.getNumberPerPage(), total, datalist);
        } else {
            return new Pager(Integer.parseInt(CXConstants.global_pageNum), Integer.parseInt(CXConstants.global_numPerPage), total, datalist);
        }
    }

    @Override
    public Pager findAll(String sql, Class entity) {
        return findAll(sql, entity, null, null);
    }

    @Override
    public Pager findAll(String sql, Class entity, Object... params) {
        return findAll(sql, entity, null, params);
    }

    private Query paramsDo(Query query, String sql, Object... params) {
        List<Object> paramsList = Arrays.asList(params);
        int size = paramsList.size();
        if (size == 0) throw new NormalException("传入参数为空，数据检索错误");
        for (int i = 0; i < size; i++
        ) {
            if (paramsList.get(i).getClass().equals(Date.class)) {
                query.setParameter(i + 1, (Date) paramsList.get(i), TemporalType.DATE);
            } else {
                query.setParameter(i + 1, paramsList.get(i));
            }
        }
        return query;
    }

    private String pageDo(String sql, PageData pageRequest) {
        Integer pageNum = pageRequest.getPageNum();
        Integer numPerPage = pageRequest.getNumberPerPage();
        if (pageNum == 0) {
            pageNum = 1;
        } else {
            pageNum = pageNum * numPerPage;
        }
        if (pageRequest.getOrderType() != null) {
            sql += String.format("order by %s %s", pageRequest.getOrderPath(), pageRequest.getOrderType());
        }
        sql += String.format("limit %d,%d", pageNum, numPerPage);
        return sql;
    }

    @Override
    public Pager findAll(String sql, Class entity, PageData pageData) {
        return findAll(sql, entity, pageData);
    }

    @Override
    public void saveEntity(Class entity) {
        entityManager.persist(entity);
    }

    @Override
    public void delEntity(Class entity) {
        entityManager.remove(entity);
    }

    @Override
    public T updateEntity(Class entity) {
        return (T) entityManager.merge(entity);
    }

    @Override
    public T findById(Class entity, Object primaryKey) {
        return (T) entityManager.find(entity, primaryKey);
    }

    @Override
    public T findOne(String sql, Class entity, Object... params) {
        Query query = entityManager.createNativeQuery(sql, entity);
        if (sql.indexOf("?") != -1) {
            query = paramsDo(query, sql, params);
        }
        return (T) query.getSingleResult();
    }

    @Override
    public Integer count(Class entity) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> critQuery = criteriaBuilder.createQuery(Long.class);
        Root root = critQuery.from(entity);
        critQuery.select(criteriaBuilder.countDistinct(root));
        return entityManager.createQuery(critQuery).getSingleResult().intValue();
    }
}
