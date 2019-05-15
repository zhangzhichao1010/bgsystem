package com.managesystem.bgsystem.RepositoryInterface;

import com.managesystem.bgsystem.Model.Entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
