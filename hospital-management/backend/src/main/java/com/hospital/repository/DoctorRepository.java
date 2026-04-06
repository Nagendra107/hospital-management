package com.hospital.repository;

import com.hospital.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByDoctorId(String doctorId);
    Optional<Doctor> findByEmail(String email);

    List<Doctor> findByStatus(Doctor.DoctorStatus status);
    List<Doctor> findBySpecialization(String specialization);
    List<Doctor> findByDepartment(String department);

    @Query("SELECT d FROM Doctor d WHERE " +
           "LOWER(d.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.specialization) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.department) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "d.doctorId LIKE CONCAT('%', :query, '%')")
    List<Doctor> searchDoctors(@Param("query") String query);

    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.status = 'ACTIVE'")
    Long countActiveDoctors();

    @Query("SELECT DISTINCT d.specialization FROM Doctor d")
    List<String> findAllSpecializations();

    @Query("SELECT DISTINCT d.department FROM Doctor d WHERE d.department IS NOT NULL")
    List<String> findAllDepartments();
}
