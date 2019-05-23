package com.managesystem.bgsystem.config.Interceptor.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ipadress", indexes = {@Index(name = "ipdata", columnList = "type"),
        @Index(name = "ipdata", columnList = "romoteIP")})
public class IPAdress implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public Long romoteIP;
    @Column(columnDefinition = "tinyint(1) comment '0:黑名单；1:白名单'")
    public Integer type;

    public Long getId() {
        return id;
    }

    public Long getRomoteIP() {
        return romoteIP;
    }

    public Integer getType() {
        return type;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRomoteIP(Long romoteIP) {
        this.romoteIP = romoteIP;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
