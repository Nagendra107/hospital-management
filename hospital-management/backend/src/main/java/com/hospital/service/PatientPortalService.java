package com.hospital.service;

import com.hospital.model.Appointment;
import com.hospital.model.Bill;
import com.hospital.model.Patient;
import com.hospital.model.PatientUser;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.BillRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.PatientUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientPortalService {

    @Autowired private PatientUserRepository patientUserRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private BillRepository billRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public PatientUser register(PatientUser patientUser) {
        if (patientUserRepository.existsByEmail(patientUser.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        patientUser.setPassword(passwordEncoder.encode(patientUser.getPassword()));

        Patient patient = new Patient();
        patient.setFirstName(patientUser.getFirstName());
        patient.setLastName(patientUser.getLastName());
        patient.setEmail(patientUser.getEmail());
        patient.setPhone(patientUser.getPhone());
        patient.setDateOfBirth(patientUser.getDateOfBirth());
        patient.setGender(patientUser.getGender());
        patient.setBloodGroup(patientUser.getBloodGroup());
        patient.setStatus(Patient.PatientStatus.OUTPATIENT);
        long count = patientRepository.count() + 1;
        patient.setPatientId(String.format("PT-%05d", count));

        Patient savedPatient = patientRepository.save(patient);
        patientUser.setPatientRecordId(savedPatient.getId());

        PatientUser saved = patientUserRepository.save(patientUser);
        saved.setPassword(null);
        return saved;
    }

    public Optional<PatientUser> findByEmail(String email) {
        return patientUserRepository.findByEmail(email);
    }

    public boolean checkPassword(PatientUser user, String raw) {
        return passwordEncoder.matches(raw, user.getPassword());
    }

    public PatientUser getProfile(Long id) {
        PatientUser user = patientUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        user.setPassword(null);
        return user;
    }

    public PatientUser updateProfile(Long id, PatientUser d) {
        PatientUser user = patientUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        user.setFirstName(d.getFirstName());
        user.setLastName(d.getLastName());
        user.setPhone(d.getPhone());
        user.setDateOfBirth(d.getDateOfBirth());
        user.setGender(d.getGender());
        user.setBloodGroup(d.getBloodGroup());
        user.setAddress(d.getAddress());
        user.setCity(d.getCity());
        user.setState(d.getState());
        user.setEmergencyContactName(d.getEmergencyContactName());
        user.setEmergencyContactPhone(d.getEmergencyContactPhone());
        user.setMedicalHistory(d.getMedicalHistory());
        user.setAllergies(d.getAllergies());

        if (user.getPatientRecordId() != null) {
            patientRepository.findById(user.getPatientRecordId()).ifPresent(p -> {
                p.setFirstName(d.getFirstName());
                p.setLastName(d.getLastName());
                p.setPhone(d.getPhone());
                p.setDateOfBirth(d.getDateOfBirth());
                p.setGender(d.getGender());
                p.setBloodGroup(d.getBloodGroup());
                p.setMedicalHistory(d.getMedicalHistory());
                p.setAllergies(d.getAllergies());
                patientRepository.save(p);
            });
        }

        PatientUser saved = patientUserRepository.save(user);
        saved.setPassword(null);
        return saved;
    }

    public List<Appointment> getMyAppointments(Long uid) {
        PatientUser u = patientUserRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (u.getPatientRecordId() == null) return new ArrayList<>();
        return appointmentRepository.findByPatientId(u.getPatientRecordId());
    }

    public Appointment bookAppointment(Long uid, Appointment a) {
        PatientUser u = patientUserRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("Not found"));
        a.setPatientId(u.getPatientRecordId());
        a.setPatientName(u.getFullName());
        a.setStatus(Appointment.AppointmentStatus.SCHEDULED);
        a.setAppointmentId(String.format("APT-%05d", appointmentRepository.count() + 1));
        return appointmentRepository.save(a);
    }

    public Appointment cancelAppointment(Long apptId, Long uid) {
        PatientUser u = patientUserRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("Not found"));
        Appointment a = appointmentRepository.findById(apptId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (!a.getPatientId().equals(u.getPatientRecordId())) {
            throw new RuntimeException("Unauthorized");
        }
        a.setStatus(Appointment.AppointmentStatus.CANCELLED);
        return appointmentRepository.save(a);
    }

    public List<Bill> getMyBills(Long uid) {
        PatientUser u = patientUserRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (u.getPatientRecordId() == null) return new ArrayList<>();
        return billRepository.findByPatientId(u.getPatientRecordId());
    }

    public void changePassword(Long id, String oldPwd, String newPwd) {
        PatientUser user = patientUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPwd));
        patientUserRepository.save(user);
    }
}
