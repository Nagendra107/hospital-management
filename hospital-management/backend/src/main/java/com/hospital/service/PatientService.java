package com.hospital.service;

import com.hospital.model.Patient;
import com.hospital.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Optional<Patient> getPatientByPatientId(String patientId) {
        return patientRepository.findByPatientId(patientId);
    }

    public Patient createPatient(Patient patient) {
        // Generate patient ID
        long count = patientRepository.count() + 1;
        patient.setPatientId(String.format("PT-%05d", count));
        return patientRepository.save(patient);
    }

    public Patient updatePatient(Long id, Patient patientDetails) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        patient.setFirstName(patientDetails.getFirstName());
        patient.setLastName(patientDetails.getLastName());
        patient.setEmail(patientDetails.getEmail());
        patient.setPhone(patientDetails.getPhone());
        patient.setDateOfBirth(patientDetails.getDateOfBirth());
        patient.setGender(patientDetails.getGender());
        patient.setBloodGroup(patientDetails.getBloodGroup());
        patient.setAddress(patientDetails.getAddress());
        patient.setCity(patientDetails.getCity());
        patient.setState(patientDetails.getState());
        patient.setMedicalHistory(patientDetails.getMedicalHistory());
        patient.setAllergies(patientDetails.getAllergies());
        patient.setEmergencyContactName(patientDetails.getEmergencyContactName());
        patient.setEmergencyContactPhone(patientDetails.getEmergencyContactPhone());
        patient.setStatus(patientDetails.getStatus());
        patient.setWard(patientDetails.getWard());
        patient.setBedNumber(patientDetails.getBedNumber());
        patient.setAssignedDoctorName(patientDetails.getAssignedDoctorName());

        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    public List<Patient> searchPatients(String query) {
        return patientRepository.searchPatients(query);
    }

    public List<Patient> getPatientsByStatus(Patient.PatientStatus status) {
        return patientRepository.findByStatus(status);
    }

    public Patient admitPatient(Long id, String ward, String bed, String doctorName) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        patient.setStatus(Patient.PatientStatus.ADMITTED);
        patient.setWard(ward);
        patient.setBedNumber(bed);
        patient.setAssignedDoctorName(doctorName);
        patient.setAdmittedAt(LocalDateTime.now());
        return patientRepository.save(patient);
    }

    public Patient dischargePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        patient.setStatus(Patient.PatientStatus.DISCHARGED);
        patient.setDischargedAt(LocalDateTime.now());
        return patientRepository.save(patient);
    }

    public List<Patient> getRecentPatients() {
        return patientRepository.findTop10ByOrderByCreatedAtDesc();
    }

    // Dashboard stats
    public Long getTotalPatients() { return patientRepository.countAllPatients(); }
    public Long getAdmittedCount() { return patientRepository.countByStatus(Patient.PatientStatus.ADMITTED); }
    public Long getCriticalCount() { return patientRepository.countByStatus(Patient.PatientStatus.CRITICAL); }
    public Long getDischargedCount() { return patientRepository.countByStatus(Patient.PatientStatus.DISCHARGED); }
}
