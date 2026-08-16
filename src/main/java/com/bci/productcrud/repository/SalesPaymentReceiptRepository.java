package com.bci.productcrud.repository;

import com.bci.productcrud.model.SalesPaymentReceipt;
import com.bci.productcrud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesPaymentReceiptRepository extends JpaRepository<SalesPaymentReceipt, Long> {
    List<SalesPaymentReceipt> findByCashier(User cashier);
}
