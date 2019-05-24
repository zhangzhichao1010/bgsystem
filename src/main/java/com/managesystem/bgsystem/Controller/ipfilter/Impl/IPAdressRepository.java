package com.managesystem.bgsystem.Controller.ipfilter.Impl;

import com.managesystem.bgsystem.Controller.ipfilter.Entity.IPAdress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IPAdressRepository extends JpaRepository<IPAdress, Long>, JpaSpecificationExecutor<IPAdress> {
}
