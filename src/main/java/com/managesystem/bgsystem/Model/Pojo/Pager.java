package com.managesystem.bgsystem.Model.Pojo;

//import java.io.Serializable;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xiangyang.li
 * Date: 15-6-3
 * Time: 下午5:23
 * To change this template use File | Settings | File Templates.
 */
public class Pager implements Serializable {
    private String result;
    private String message;
    private int pageSize;
    private int currentPage;
    private long total;
    private Object data;

    public Pager(String result, Object data) {
        this.result = result;
        this.data = data;
    }

    public Pager(String result, String message) {
        this.result = result;
        this.message = message;
    }

    public Pager(int pageSize, int currentPage, int total, Object data, String result ) {
        this.result = result;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.total = total;
        this.data = data;
    }

    public Pager(int pageSize, int currentPage, long total, Object data) {
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.total = total;
        this.data = data;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Object getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public  String toString(){
        return "Pager:[currentPage:"+currentPage+"]";
    }

    @Override
    public int hashCode() {
        return super.hashCode();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
