package com.hospital.repository;

import com.hospital.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPatientId(String patientId);
    Optional<Patient> findByEmail(String email);

    List<Patient> findByStatus(Patient.PatientStatus status);
    List<Patient> findByAssignedDoctorName(String doctorName);

    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "p.phone LIKE CONCAT('%', :query, '%') OR " +
           "p.patientId LIKE CONCAT('%', :query, '%')")
    List<Patient> searchPatients(@Param("query") String query);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.status = :status")
    Long countByStatus(@Param("status") Patient.PatientStatus status);

    @Query("SELECT COUNT(p) FROM Patient p")
    Long countAllPatients();

    List<Patient> findTop10ByOrderByCreatedAtDesc();
}
