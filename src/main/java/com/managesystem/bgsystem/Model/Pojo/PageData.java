package com.managesystem.bgsystem.Model.Pojo;

import lombok.Data;

@Data
public class PageData {
    private Integer pageNum;
    private Integer numberPerPage;
    private String orderPath;
    private String orderType = "asc";

    public PageData(Integer pageNum, Integer numberPerPage) {
        this.pageNum = pageNum;
        this.numberPerPage = numberPerPage;
    }

    public PageData(Integer pageNum, Integer numberPerPage, String orderPath, String orderType) {
        this.pageNum = pageNum;
        this.numberPerPage = numberPerPage;
        this.orderPath = orderPath;
        this.orderType = orderType;
    }
}
