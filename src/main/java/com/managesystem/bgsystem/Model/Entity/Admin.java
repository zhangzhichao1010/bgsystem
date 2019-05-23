package com.managesystem.bgsystem.Model.Entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "admin")
@Data
public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "varchar(100)", unique = true)
    @NotNull(message = "用户名不能为空！")
    private String adminname;
    @Column(columnDefinition = "varchar(100) comment '真实姓名'")
    private String realname;
    @NotNull(message = "密码不能为空！")
    private String password;
    @Column(columnDefinition = "tinyint unsigned")
    private Integer status;//0:正常使用；1：锁定不可操作
    @Column(columnDefinition = "tinyint unsigned")
    private Integer type;//管理员类型1:单位管理员255：超级管理员
    private Long unitId;//组织ID
    private String unitname;//组织名称
    private Integer fid;//单位FID
    @Column(columnDefinition = "varchar(20)")
    private String themeName;//后台页面主题
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "roles")
    private List<Roles> roles;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;
    @Column(columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updated;
    @Transient
    private String name;

    public String getName() {
        return this.adminname;
    }


    public boolean isSuperAdmin() {
        return type == 255;
    }
}
