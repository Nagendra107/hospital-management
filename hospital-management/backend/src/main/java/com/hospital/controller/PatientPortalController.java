package com.hospital.controller;

import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.PatientUser;
import com.hospital.repository.DoctorRepository;
import com.hospital.security.JwtUtils;
import com.hospital.service.PatientPortalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/portal")
@CrossOrigin(origins = "*")
public class PatientPortalController {

    @Autowired private PatientPortalService portalService;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private DoctorRepository doctorRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody PatientUser patientUser) {
        try {
            PatientUser saved = portalService.register(patientUser);
            String token = jwtUtils.generateToken(saved.getEmail(), "PATIENT");
            return ResponseEntity.ok(Map.of(
                "message", "Registration successful!",
                "token", token,
                "user", Map.of(
                    "id", saved.getId(),
                    "email", saved.getEmail(),
                    "firstName", saved.getFirstName(),
                    "lastName", saved.getLastName(),
                    "fullName", saved.getFullName(),
                    "role", "PATIENT"
                )
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        Optional<PatientUser> userOpt = portalService.findByEmail(email);
        if (userOpt.isEmpty() || !portalService.checkPassword(userOpt.get(), password)) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
        }

        PatientUser user = userOpt.get();
        if (!user.isActive()) {
            return ResponseEntity.status(403).body(Map.of("message", "Account is deactivated"));
        }

        String token = jwtUtils.generateToken(user.getEmail(), "PATIENT");
        return ResponseEntity.ok(Map.of(
            "token", token,
            "user", Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "fullName", user.getFullName(),
                "patientRecordId", user.getPatientRecordId() != null ? user.getPatientRecordId() : 0,
                "role", "PATIENT"
            ),
            "message", "Login successful"
        ));
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(portalService.getProfile(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody PatientUser details) {
        try {
            return ResponseEntity.ok(portalService.updateProfile(id, details));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/profile/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            portalService.changePassword(id, body.get("oldPassword"), body.get("newPassword"));
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/appointments/{patientUserId}")
    public ResponseEntity<?> getMyAppointments(@PathVariable Long patientUserId) {
        return ResponseEntity.ok(portalService.getMyAppointments(patientUserId));
    }

    @PostMapping("/appointments/{patientUserId}")
    public ResponseEntity<?> bookAppointment(@PathVariable Long patientUserId,
                                              @RequestBody Appointment appointment) {
        try {
            return ResponseEntity.ok(portalService.bookAppointment(patientUserId, appointment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/appointments/{appointmentId}/cancel/{patientUserId}")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long appointmentId,
                                                @PathVariable Long patientUserId) {
        try {
            return ResponseEntity.ok(portalService.cancelAppointment(appointmentId, patientUserId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/bills/{patientUserId}")
    public ResponseEntity<?> getMyBills(@PathVariable Long patientUserId) {
        return ResponseEntity.ok(portalService.getMyBills(patientUserId));
    }

    @GetMapping("/doctors")
    public ResponseEntity<?> getDoctors() {
        return ResponseEntity.ok(doctorRepository.findByStatus(Doctor.DoctorStatus.ACTIVE));
    }

    @GetMapping("/doctors/specializations")
    public ResponseEntity<?> getSpecializations() {
        return ResponseEntity.ok(doctorRepository.findAllSpecializations());
    }
}
