package com.managesystem.bgsystem.Utils;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SpecificationBuilder {

    /*  用法 类似 StringBuilder   非线程安全  */

    public SpecificationBuilder() { }

    private class Match implements Comparable {
        byte order;
        String path;
        Object value;
        String op;

        public Match(byte order, String path, Object value, String op) {
            this.order = order;
            this.path = path;
            this.value = value;
            this.op = op;
        }

        @Override
        public int compareTo(Object o) {
            if (o instanceof Match)
                return ((Match) o).order - this.order;
            else {
                throw new Error("类型错误");
            }
        }
    }

    private class Search {
        String searchword;
        String[] searchPath;

        public Search(String[] path, String searchword) {
            this.searchPath = path;
            this.searchword = searchword;
        }
    }


    private final ArrayList<Search> searches = new ArrayList<>();
    private final ArrayList<Match> matches = new ArrayList<>();

    private void addMatchP(byte order, String path, Object value, String op) {
        if (value instanceof List) {
            List list = (List) value;
            if (list.size() == 0) {
                return;
            }
        }
        Match match = new Match(order, path, value, op);
        matches.add(match);
    }


    public SpecificationBuilder addIndexMatch(String path, String op, Object value) {
        if (value == null) {
            return this;
        }
        byte order;
        if (value instanceof Number) {
            order = 11;
        } else if (value instanceof String) {
            order = 12;
        } else if (value instanceof List) {
            order = 13;
        } else if (value instanceof Date) {
            order = 14;
        } else {
            throw new Error("参数错误");
        }
        addMatchP(order, path, value, op);
        return this;
    }

    public SpecificationBuilder addIndexMatch(String path, Object value) {
        return this.addMatch(path, "=", value);
    }


    public SpecificationBuilder addMatch(String path, Object value) {
        return this.addMatch(path, "=", value);
    }


    public SpecificationBuilder addMatch(String path, String op, Object value) {
        if (value == null) {
            return this;
        }
        byte order;
        if (value instanceof Number) {
            order = 21;
        } else if (value instanceof String) {
            order = 22;
        } else if (value instanceof List) {
            order = 23;
        } else if (value instanceof Date) {
            order = 24;
        } else {
            throw new Error("参数错误");
        }
        addMatchP(order, path, value, op);
        return this;
    }


    public SpecificationBuilder addLikeSearch(String[] path, String searchword) {
        if (searchword == null || searchword.trim().length() == 0 || searchword.trim().matches("[%]+")) {
            return this;
        }
        Search search = new Search(path, searchword);
        searches.add(search);
        return this;
    }


    public <T> Specification<T> getSpecification() {

        if (matches.size() == 0 && searches.size() == 0) {
            return null;
        }


        Specification<T> spec = (Specification<T>) (root, query, cb) -> {

            Predicate predicate = null;

            if (matches.size() > 0) {
                Collections.sort(matches);

                for (Match match : matches) {
                    predicate = getPredicate(predicate, root, cb, match.path, match.op, match.value);
                }
            }

            if (searches.size() > 0) {
                for (Search search : searches) {
                    predicate = getPredicate(predicate, root, cb, search.searchword, search.searchPath);
                }
            }

            return predicate;
        };


        return spec;
    }


    public Predicate getPredicate(Predicate p, Root<?> root, CriteriaBuilder cb, String path, String op, Object value) {


        Path tmp = root.get(path);

        Predicate p1 = null;

        if (op.equals("=")) {
            p1 = cb.equal(tmp, value);
        } else if (op.equals("!=")) {
            p1 = cb.notEqual(tmp, value);
        } else if (op.equals(">=")) {
            if (value instanceof Long) {
                p1 = cb.ge(tmp, (Long) value);
            } else if (value instanceof Integer) {
                p1 = cb.ge(tmp, (Integer) value);
            } else if (value instanceof Date) {
                p1 = cb.greaterThanOrEqualTo(tmp.as(Date.class), (Date) value);
            }
        } else if (op.equals(">")) {
            if (value instanceof Long) {
                p1 = cb.gt(tmp, (Long) value);
            } else if (value instanceof Integer) {
                p1 = cb.gt(tmp, (Integer) value);
            } else if (value instanceof Date) {
                p1 = cb.greaterThan(tmp.as(Date.class), (Date) value);
            }
        } else if (op.equals("<=")) {
            if (value instanceof Long) {
                p1 = cb.le(tmp, (Long) value);
            } else if (value instanceof Integer) {
                p1 = cb.le(tmp, (Integer) value);
            } else if (value instanceof Date) {
                p1 = cb.lessThanOrEqualTo(tmp.as(Date.class), (Date) value);
            }
        } else if (op.equals("<")) {
            if (value instanceof Long) {
                p1 = cb.lt(tmp, (Long) value);
            } else if (value instanceof Integer) {
                p1 = cb.lt(tmp, (Integer) value);
            } else if (value instanceof Date) {
                p1 = cb.lessThan(tmp.as(Date.class), (Date) value);
            }
        } else if (op.equals("in")) {
            p1 = getIn(root, cb, path, value, true);
        } else if (op.equals("notin")) {
            p1 = getIn(root, cb, path, value, false);
        } else {
            throw new Error("illegal op" + op);
        }


        if (p == null) {
            return p1;
        }
        return cb.and(p, p1);
    }


    private static <T> Predicate getPredicate(Predicate p1, Root<T> root, CriteriaBuilder cb, String searchword, String[] path) {

        if (path == null || path.length == 0) {
            return p1;
        }

        if (searchword == null || searchword.trim().length() == 0 || searchword.trim().matches("[%]+")) {
            return p1;
        }

        searchword = "%" + searchword.trim() + "%";

        Predicate p = null;

        if (path.length == 1) {
            Path<String> stringPath = root.get(path[0]);
            assert stringPath != null;
            p = cb.like(stringPath, searchword);
        }

        if (path.length > 1) {
            Path<String> stringPath = root.get(path[0]);
            assert stringPath != null;
            p = cb.like(stringPath, searchword);
            for (int i = 1; i < path.length; i++) {
                stringPath = root.get(path[i]);
                assert stringPath != null;
                Predicate tmp = cb.like(stringPath, searchword);
                p = cb.or(p, tmp);
            }
        }

        if (p1 == null) {
            return p;
        } else {
            return cb.and(p1, p);
        }
    }


    private Predicate getIn(Root root, CriteriaBuilder cb, String path, Object value, boolean in) {
        Path tmp = root.get(path);
        Predicate predicate = null;

        if (value instanceof List) {
            List list = (List) value;
            predicate = tmp.in(list);
        } else {
            throw new Error("集合错误");
        }

        if (in == false) {
            predicate = cb.not(predicate);
        }
        return predicate;
    }

}
