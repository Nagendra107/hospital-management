package com.hospital.repository;

import com.hospital.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentId(String appointmentId);

    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByAppointmentDate(LocalDate date);
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :date AND a.doctorId = :doctorId")
    List<Appointment> findByDateAndDoctor(@Param("date") LocalDate date, @Param("doctorId") Long doctorId);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate >= :startDate AND a.appointmentDate <= :endDate")
    List<Appointment> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate = :today")
    Long countTodaysAppointments(@Param("today") LocalDate today);

    List<Appointment> findTop10ByOrderByCreatedAtDesc();

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = CURRENT_DATE ORDER BY a.appointmentTime ASC")
    List<Appointment> findTodaysAppointments();
}
