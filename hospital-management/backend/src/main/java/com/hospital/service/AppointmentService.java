package com.hospital.service;

import com.hospital.model.Appointment;
import com.hospital.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment createAppointment(Appointment appointment) {
        long count = appointmentRepository.count() + 1;
        appointment.setAppointmentId(String.format("APT-%05d", count));
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, Appointment details) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        appointment.setAppointmentDate(details.getAppointmentDate());
        appointment.setAppointmentTime(details.getAppointmentTime());
        appointment.setType(details.getType());
        appointment.setStatus(details.getStatus());
        appointment.setReason(details.getReason());
        appointment.setNotes(details.getNotes());
        appointment.setDiagnosis(details.getDiagnosis());
        appointment.setPrescription(details.getPrescription());
        appointment.setFee(details.getFee());
        appointment.setIsPaid(details.getIsPaid());

        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> getTodaysAppointments() {
        return appointmentRepository.findTodaysAppointments();
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date);
    }

    public Appointment updateStatus(Long id, Appointment.AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    public Long countTodaysAppointments() {
        return appointmentRepository.countTodaysAppointments(LocalDate.now());
    }

    public List<Appointment> getRecentAppointments() {
        return appointmentRepository.findTop10ByOrderByCreatedAtDesc();
    }
}
