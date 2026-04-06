package com.hospital.config;

import com.hospital.model.*;
import com.hospital.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private BillRepository billRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        try {
            seedUsers();
            seedDoctors();
            seedPatients();
            seedAppointments();
            seedBills();
            System.out.println("✅ Sample data seeded successfully!");
        } catch (Exception e) {
            System.out.println("⚠️ Seeding skipped (data may already exist): " + e.getMessage());
        }
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;
        userRepository.save(new User("admin", passwordEncoder.encode("admin123"), "admin@hospital.com", "System Administrator", User.Role.ADMIN));
        userRepository.save(new User("drpatel", passwordEncoder.encode("doctor123"), "kpatel@hospital.com", "Dr. Kavita Patel", User.Role.DOCTOR));
        userRepository.save(new User("reception", passwordEncoder.encode("staff123"), "reception@hospital.com", "Reception Desk", User.Role.RECEPTIONIST));
        userRepository.save(new User("nurse01", passwordEncoder.encode("nurse123"), "nurse01@hospital.com", "Nurse Asha", User.Role.NURSE));
    }

    private void seedDoctors() {
        if (doctorRepository.count() > 0) return;
        String[][] data = {
            {"Kavita","Patel","kpatel@hospital.com","9876543210","Cardiology","DR-00001","MBBS, MD","15","750.00"},
            {"Suresh","Verma","sverma@hospital.com","9876543211","Gynecology","DR-00002","MBBS, MS","12","700.00"},
            {"Anand","Singh","asingh@hospital.com","9876543212","Orthopedics","DR-00003","MBBS, MS Ortho","10","650.00"},
            {"Meena","Rao","mrao@hospital.com","9876543213","Neurology","DR-00004","MBBS, DM Neuro","18","900.00"},
            {"Riya","Mehta","rmehta@hospital.com","9876543214","Oncology","DR-00005","MBBS, MD Onco","20","1000.00"},
            {"Deepak","Iyer","diyer@hospital.com","9876543215","Pediatrics","DR-00006","MBBS, MD Paeds","8","600.00"},
        };
        for (String[] d : data) {
            Doctor doc = new Doctor();
            doc.setFirstName(d[0]); doc.setLastName(d[1]); doc.setEmail(d[2]);
            doc.setPhone(d[3]); doc.setSpecialization(d[4]); doc.setDoctorId(d[5]);
            doc.setQualification(d[6]); doc.setDepartment(d[4]);
            doc.setExperienceYears(Integer.parseInt(d[7]));
            doc.setConsultationFee(Double.parseDouble(d[8]));
            doc.setConsultationDays("MON,TUE,WED,THU,FRI");
            doc.setConsultationHours("09:00-17:00");
            doc.setDateOfJoining(LocalDate.of(2020, 1, 15));
            doc.setRating(4.5); doc.setTotalPatients(0);
            doc.setStatus(Doctor.DoctorStatus.ACTIVE);
            doctorRepository.save(doc);
        }
    }

    private void seedPatients() {
        if (patientRepository.count() > 0) return;
        String[][] data = {
            {"Arjun","Sharma","arjun@email.com","9800000001","1985-03-15","Male","A+","PT-00001","CRITICAL"},
            {"Priya","Malhotra","priya@email.com","9800000002","1992-07-22","Female","B+","PT-00002","ADMITTED"},
            {"Rohit","Kumar","rohit@email.com","9800000003","1978-11-30","Male","O+","PT-00003","OBSERVATION"},
            {"Deepa","Gupta","deepa@email.com","9800000004","1990-05-18","Female","AB+","PT-00004","DISCHARGED"},
            {"Sunil","Naik","sunil@email.com","9800000005","1965-09-25","Male","B-","PT-00005","ADMITTED"},
            {"Meena","Joshi","meena@email.com","9800000006","2000-12-10","Female","O-","PT-00006","OUTPATIENT"},
        };
        for (String[] d : data) {
            Patient p = new Patient();
            p.setFirstName(d[0]); p.setLastName(d[1]); p.setEmail(d[2]);
            p.setPhone(d[3]);
            p.setDateOfBirth(LocalDate.parse(d[4]));
            p.setGender(d[5]); p.setBloodGroup(d[6]); p.setPatientId(d[7]);
            p.setStatus(Patient.PatientStatus.valueOf(d[8]));
            patientRepository.save(p);
        }
    }

    private void seedAppointments() {
        if (appointmentRepository.count() > 0) return;
        // Skip appointment seeding to avoid complexity
    }

    private void seedBills() {
        if (billRepository.count() > 0) return;
        // Skip bill seeding to avoid complexity
    }
}
