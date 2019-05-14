package com.managesystem.bgsystem.Model.Entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "organizations", uniqueConstraints = {@UniqueConstraint(columnNames = {"fid", "name"})})
@Data
public class Organizations implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "varchar(100)")
    private String name;
    @Column(columnDefinition = "varchar(50)")
    private String phoneNum;//机构电话
    private String province;//所属省
    private String city;//所属市
    private String area;//所属区
    private String address;//机构地址
    private Date created;
    @Column(columnDefinition = "timestamp not null default CURRENT_TIMESTAMP")
    private Date updated;
    private Long parentID;//父组织ID
    private Integer fid;//单位的ID
}
