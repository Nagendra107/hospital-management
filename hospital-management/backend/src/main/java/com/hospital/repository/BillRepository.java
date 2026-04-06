package com.hospital.repository;

import com.hospital.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByBillNumber(String billNumber);
    List<Bill> findByPatientId(Long patientId);
    List<Bill> findByStatus(Bill.BillStatus status);

    @Query("SELECT SUM(b.totalAmount) FROM Bill b WHERE b.status = 'PAID'")
    Double getTotalRevenue();

    @Query("SELECT SUM(b.dueAmount) FROM Bill b WHERE b.status = 'PENDING' OR b.status = 'PARTIAL'")
    Double getTotalPending();

    List<Bill> findTop10ByOrderByCreatedAtDesc();
}
