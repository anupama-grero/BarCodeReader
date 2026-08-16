package com.bci.productcrud.repository;

import com.bci.productcrud.model.Product;
import com.bci.productcrud.model.SalesPaymentReceipt;
import com.bci.productcrud.model.SalesReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesReceiptItemRepository extends JpaRepository<SalesReceiptItem, Long> {
    List<SalesReceiptItem> findBySalesPaymentReceipt(SalesPaymentReceipt salesPaymentReceipt);
    List<SalesReceiptItem> findByProduct(Product product);
}
