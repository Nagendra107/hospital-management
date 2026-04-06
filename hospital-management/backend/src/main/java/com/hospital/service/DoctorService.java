package com.hospital.service;

import com.hospital.model.Doctor;
import com.hospital.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public Doctor createDoctor(Doctor doctor) {
        long count = doctorRepository.count() + 1;
        doctor.setDoctorId(String.format("DR-%05d", count));
        return doctorRepository.save(doctor);
    }

    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));

        doctor.setFirstName(doctorDetails.getFirstName());
        doctor.setLastName(doctorDetails.getLastName());
        doctor.setEmail(doctorDetails.getEmail());
        doctor.setPhone(doctorDetails.getPhone());
        doctor.setSpecialization(doctorDetails.getSpecialization());
        doctor.setDepartment(doctorDetails.getDepartment());
        doctor.setQualification(doctorDetails.getQualification());
        doctor.setLicenseNumber(doctorDetails.getLicenseNumber());
        doctor.setExperienceYears(doctorDetails.getExperienceYears());
        doctor.setConsultationDays(doctorDetails.getConsultationDays());
        doctor.setConsultationHours(doctorDetails.getConsultationHours());
        doctor.setConsultationFee(doctorDetails.getConsultationFee());
        doctor.setStatus(doctorDetails.getStatus());
        doctor.setBio(doctorDetails.getBio());

        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    public List<Doctor> searchDoctors(String query) {
        return doctorRepository.searchDoctors(query);
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    public List<Doctor> getActiveDoctors() {
        return doctorRepository.findByStatus(Doctor.DoctorStatus.ACTIVE);
    }

    public Long countActiveDoctors() {
        return doctorRepository.countActiveDoctors();
    }

    public List<String> getAllSpecializations() {
        return doctorRepository.findAllSpecializations();
    }

    public List<String> getAllDepartments() {
        return doctorRepository.findAllDepartments();
    }
}
