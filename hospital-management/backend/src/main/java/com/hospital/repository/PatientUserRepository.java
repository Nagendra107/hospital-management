package com.hospital.repository;

import com.hospital.model.PatientUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PatientUserRepository extends JpaRepository<PatientUser, Long> {
    Optional<PatientUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
