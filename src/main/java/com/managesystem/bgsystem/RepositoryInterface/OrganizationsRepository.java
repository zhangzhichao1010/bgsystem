package com.managesystem.bgsystem.RepositoryInterface;

import com.managesystem.bgsystem.Model.Entity.Organizations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrganizationsRepository extends JpaSpecificationExecutor<Organizations>, JpaRepository<Organizations, Long> {
    @Query(value = "select * from organizations where fid=?1 and parentid!=0", nativeQuery = true)
    List<Organizations> findAllUnitnameByFid(Integer fid);

    @Query(value = "select * from organizations where parentid!=0", nativeQuery = true)
    List<Organizations> findAllUnitname();

    @Query(value = "select * from organizations where parentid=?1", nativeQuery = true)
    List<Organizations> findChildOrgs(Long orgId);
}
