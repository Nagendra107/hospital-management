package com.hospital.controller;

import com.hospital.model.Bill;
import com.hospital.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bills")
@CrossOrigin(origins = "*")
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping
    public ResponseEntity<List<Bill>> getAll() {
        return ResponseEntity.ok(billService.getAllBills());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return billService.getBillById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Bill>> byPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(billService.getBillsByPatient(patientId));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Bill>> recent() {
        return ResponseEntity.ok(billService.getRecentBills());
    }

    @PostMapping
    public ResponseEntity<Bill> create(@RequestBody Bill bill) {
        return ResponseEntity.ok(billService.createBill(bill));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bill> update(@PathVariable Long id, @RequestBody Bill bill) {
        return ResponseEntity.ok(billService.updateBill(id, bill));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Bill> pay(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Double amount = Double.parseDouble(body.get("amount").toString());
        String method = (String) body.get("paymentMethod");
        return ResponseEntity.ok(billService.processPay(id, amount, method));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        billService.deleteBill(id);
        return ResponseEntity.ok(Map.of("message", "Bill deleted"));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Double>> stats() {
        return ResponseEntity.ok(Map.of(
                "totalRevenue", billService.getTotalRevenue(),
                "totalPending", billService.getTotalPending()
        ));
    }
}
