package com.managesystem.bgsystem.Utils;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class PredicateUtil {

    /*  设计的败笔  一开始都在管理员登陆的状态下设计的。 根本没有考虑过前端所有的查询并没有fid的概念   */

    public static Predicate matchOrgId(Predicate p, Root<?> root, CriteriaBuilder cb, Long orgId) {
        Path<String> tmp = root.get("organizations");
        Predicate p1 = cb.equal(tmp, orgId);
        return cb.and(p, p1);
    }

    public static Predicate matchYear(Predicate p, Root<?> root, CriteriaBuilder cb, Integer year) {
        Path<String> tmp = root.get("year");
        Predicate p1 = cb.equal(tmp, year);
        return cb.and(p, p1);
    }

    public static Predicate matchClassify(Predicate p, Root<?> root, CriteriaBuilder cb, Long classifyId) {
        Path<String> tmp = root.get("classify");
        Predicate p1 = cb.equal(tmp, classifyId);
        return cb.and(p, p1);
    }


    public static Predicate matchActivity(Predicate p, Root<?> root, CriteriaBuilder cb, Long activityId) {
        Path<String> tmp = root.get("activity");
        Predicate p1 = cb.equal(tmp, activityId);
        return cb.and(p, p1);
    }


    public static Predicate userIdNotIn(Predicate p, Root<?> root, CriteriaBuilder cb, List<Long> inSet) {
        Path<String> tmp = root.get("id");
        Predicate predicate = tmp.in(inSet);
        return cb.and(p, cb.not(predicate));
    }

    public static Predicate userInOrgIds(Predicate p, Root<?> root, CriteriaBuilder cb, List<Long> inSet) {
        return p;
    }


    public static Predicate userInOrgNames(Predicate p, Root<?> root, CriteriaBuilder cb, List<String> inSet) {
        Path<String> tmp = root.get("unitname");
        Predicate predicate = tmp.in(inSet);
        return cb.and(p, predicate);
    }


    public static Predicate matchClassifyName(Predicate p, Root<?> root, CriteriaBuilder cb, String classifyName) {
        Path<String> tmp = root.get("classifyName");
        Predicate p1 = cb.equal(tmp, classifyName);
        return cb.and(p, p1);
    }

    public static Predicate matchParentID(Predicate p, Root<?> root, CriteriaBuilder cb, Long parentID) {
        Path<String> tmp = root.get("parentID");
        Predicate p1 = cb.equal(tmp, parentID);
        return cb.and(p, p1);
    }


    public static Predicate matchFid(Root<?> root, CriteriaBuilder cb, Long fid) {
        Path<String> tmp = root.get("fid");
        Predicate p1 = cb.equal(tmp, fid);
        return p1;
    }

}
