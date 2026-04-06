package com.hospital.controller;

import com.hospital.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired private PatientService patientService;
    @Autowired private DoctorService doctorService;
    @Autowired private AppointmentService appointmentService;
    @Autowired private BillService billService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Patient stats
        stats.put("totalPatients", patientService.getTotalPatients());
        stats.put("admittedPatients", patientService.getAdmittedCount());
        stats.put("criticalPatients", patientService.getCriticalCount());
        stats.put("dischargedToday", patientService.getDischargedCount());

        // Doctor & appointments
        stats.put("activeDoctors", doctorService.countActiveDoctors());
        stats.put("todayAppointments", appointmentService.countTodaysAppointments());

        // Financial
        stats.put("totalRevenue", billService.getTotalRevenue());
        stats.put("pendingPayments", billService.getTotalPending());

        // Recent data
        stats.put("recentPatients", patientService.getRecentPatients());
        stats.put("todayAppointmentsList", appointmentService.getTodaysAppointments());

        return ResponseEntity.ok(stats);
    }
}
