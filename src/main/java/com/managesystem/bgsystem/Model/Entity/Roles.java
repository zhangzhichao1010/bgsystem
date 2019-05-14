package com.managesystem.bgsystem.Model.Entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "roles")
@Data
public class Roles implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "varchar(100) comment '角色名称'")
    private String rolename;
    @Column(columnDefinition = "bigint comment '所属单位ID'")
    private Long unitId;
    @Column(columnDefinition = "varchar(255) comment '所属单位ID'")
    private String unitname;//所属单位名称
    @Column(columnDefinition = "text comment '角色介绍：功能介绍'")
    private String roleDetail;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private Authority authority;
    private Integer fid;//单位FID
    private Date created;
    @Column(columnDefinition = "timestamp not null default CURRENT_TIMESTAMP")
    private Date updated;
    @Transient
    private String name;

    public String getName() {
        return this.rolename;
    }
}
