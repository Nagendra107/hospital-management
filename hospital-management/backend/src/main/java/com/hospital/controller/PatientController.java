package com.hospital.controller;

import com.hospital.model.Patient;
import com.hospital.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatient(@PathVariable Long id) {
        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Patient>> search(@RequestParam String q) {
        return ResponseEntity.ok(patientService.searchPatients(q));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Patient>> byStatus(@PathVariable Patient.PatientStatus status) {
        return ResponseEntity.ok(patientService.getPatientsByStatus(status));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Patient>> recent() {
        return ResponseEntity.ok(patientService.getRecentPatients());
    }

    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient patient) {
        return ResponseEntity.ok(patientService.createPatient(patient));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @Valid @RequestBody Patient patient) {
        return ResponseEntity.ok(patientService.updatePatient(id, patient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok(Map.of("message", "Patient deleted successfully"));
    }

    @PostMapping("/{id}/admit")
    public ResponseEntity<Patient> admit(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(patientService.admitPatient(id,
                body.get("ward"), body.get("bedNumber"), body.get("doctorName")));
    }

    @PostMapping("/{id}/discharge")
    public ResponseEntity<Patient> discharge(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.dischargePatient(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(Map.of(
                "total", patientService.getTotalPatients(),
                "admitted", patientService.getAdmittedCount(),
                "critical", patientService.getCriticalCount(),
                "discharged", patientService.getDischargedCount()
        ));
    }
}
