package com.managesystem.bgsystem.Model.Entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "authority")
@Data
public class Authority implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "varchar(100) comment '权限名称'")
    private String authorityname;
    @Column(columnDefinition = "text comment '权限列表'")
    private String authorityList;
    @Column(columnDefinition = "text comment '权限简介'")
    private String authorityDetail;
    private Date created;
    @Column(columnDefinition = "timestamp not null default CURRENT_TIMESTAMP")
    private Date updated;
}
