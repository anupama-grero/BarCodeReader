package com.bci.productcrud.repository;

import com.bci.productcrud.model.Payment;
import com.bci.productcrud.model.SalesPaymentReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findBySalesPaymentReceipt(SalesPaymentReceipt receipt);
}
