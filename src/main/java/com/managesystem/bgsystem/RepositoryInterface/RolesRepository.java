package com.managesystem.bgsystem.RepositoryInterface;

import com.managesystem.bgsystem.Model.Entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RolesRepository extends JpaRepository<Roles,Long> {
    @Query(value = "select * from roles where unit_id=?1", nativeQuery = true)
    List<Roles> findRolesByorgId(Long orgId);
}
