package com.hospital.service;

import com.hospital.model.Bill;
import com.hospital.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BillService {

    @Autowired
    private BillRepository billRepository;

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Optional<Bill> getBillById(Long id) {
        return billRepository.findById(id);
    }

    public Bill createBill(Bill bill) {
        long count = billRepository.count() + 1;
        bill.setBillNumber(String.format("BILL-%05d", count));
        calculateTotals(bill);
        return billRepository.save(bill);
    }

    public Bill updateBill(Long id, Bill billDetails) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found with id: " + id));

        bill.setConsultationFee(billDetails.getConsultationFee());
        bill.setMedicationCharges(billDetails.getMedicationCharges());
        bill.setLabCharges(billDetails.getLabCharges());
        bill.setRoomCharges(billDetails.getRoomCharges());
        bill.setSurgeryCharges(billDetails.getSurgeryCharges());
        bill.setOtherCharges(billDetails.getOtherCharges());
        bill.setDiscount(billDetails.getDiscount());
        bill.setPaymentMethod(billDetails.getPaymentMethod());
        bill.setNotes(billDetails.getNotes());

        calculateTotals(bill);
        return billRepository.save(bill);
    }

    public Bill processPay(Long id, Double amount, String paymentMethod) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        bill.setPaidAmount(bill.getPaidAmount() + amount);
        bill.setDueAmount(bill.getTotalAmount() - bill.getPaidAmount());
        bill.setPaymentMethod(paymentMethod);

        if (bill.getDueAmount() <= 0) {
            bill.setStatus(Bill.BillStatus.PAID);
            bill.setPaidAt(LocalDateTime.now());
            bill.setDueAmount(0.0);
        } else {
            bill.setStatus(Bill.BillStatus.PARTIAL);
        }

        return billRepository.save(bill);
    }

    public void deleteBill(Long id) {
        billRepository.deleteById(id);
    }

    public List<Bill> getBillsByPatient(Long patientId) {
        return billRepository.findByPatientId(patientId);
    }

    public List<Bill> getRecentBills() {
        return billRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public Double getTotalRevenue() {
        Double revenue = billRepository.getTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }

    public Double getTotalPending() {
        Double pending = billRepository.getTotalPending();
        return pending != null ? pending : 0.0;
    }

    private void calculateTotals(Bill bill) {
        double subtotal = (bill.getConsultationFee() != null ? bill.getConsultationFee() : 0)
                + (bill.getMedicationCharges() != null ? bill.getMedicationCharges() : 0)
                + (bill.getLabCharges() != null ? bill.getLabCharges() : 0)
                + (bill.getRoomCharges() != null ? bill.getRoomCharges() : 0)
                + (bill.getSurgeryCharges() != null ? bill.getSurgeryCharges() : 0)
                + (bill.getOtherCharges() != null ? bill.getOtherCharges() : 0);

        double discount = bill.getDiscount() != null ? bill.getDiscount() : 0;
        double afterDiscount = subtotal - discount;
        double tax = afterDiscount * 0.05; // 5% GST

        bill.setTaxAmount(Math.round(tax * 100.0) / 100.0);
        bill.setTotalAmount(Math.round((afterDiscount + tax) * 100.0) / 100.0);
        bill.setDueAmount(bill.getTotalAmount() - (bill.getPaidAmount() != null ? bill.getPaidAmount() : 0));
    }
}
