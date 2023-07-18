package com.meineAngebote.company;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface CompanyRepository extends JpaRepository<Company, Long> {

  Optional<Company> findByEmail(String email);

  @Transactional
  @Modifying
  @Query("UPDATE Company a " +
      "SET a.enabled = TRUE WHERE a.email = ?1")
  int enableCompany(String email);

}

