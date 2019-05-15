package com.managesystem.bgsystem.config.Interceptor.Repository;

import com.managesystem.bgsystem.config.Interceptor.Entity.IPAdress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IPAdressRepository extends JpaRepository<IPAdress, Long>, JpaSpecificationExecutor<IPAdress> {
}
