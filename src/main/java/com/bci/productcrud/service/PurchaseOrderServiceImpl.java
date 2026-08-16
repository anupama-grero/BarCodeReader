package com.bci.productcrud.service;

import com.bci.productcrud.exception.InvalidPurchaseOrderException;
import com.bci.productcrud.exception.ResourceNotFoundException;
import com.bci.productcrud.model.*;
import com.bci.productcrud.repository.PurchaseOrderRepository;
import com.bci.productcrud.repository.SupplierRepository;
import com.bci.productcrud.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
                                   SupplierRepository supplierRepository,
                                   ProductRepository productRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
    }

    @Override
    public PurchaseOrder create(PurchaseOrder purchaseOrder) {
        validatePurchaseOrder(purchaseOrder);
        if (purchaseOrder.getSupplier() != null && purchaseOrder.getSupplier().getId() != null) {
            Supplier supplier = supplierRepository.findById(purchaseOrder.getSupplier().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id " + purchaseOrder.getSupplier().getId()));
            purchaseOrder.setSupplier(supplier);
        } else {
            throw new InvalidPurchaseOrderException("Supplier is required");
        }

        if (purchaseOrder.getPoNumber() == null || purchaseOrder.getPoNumber().isBlank()) {
            purchaseOrder.setPoNumber(generatePoNumber());
        }
        if (purchaseOrderRepository.existsByPoNumber(purchaseOrder.getPoNumber())) {
            throw new InvalidPurchaseOrderException("Purchase order number already exists: " + purchaseOrder.getPoNumber());
        }

        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                throw new InvalidPurchaseOrderException("Product is required for each purchase order item");
            }
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + item.getProduct().getId()));
            item.setProduct(product);
            if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidPurchaseOrderException("Unit price cannot be negative");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new InvalidPurchaseOrderException("Quantity must be greater than 0");
            }
            item.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        purchaseOrder.recalculateTotal();
        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrder> findAll() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrder findById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrder findByPoNumber(String poNumber) {
        return purchaseOrderRepository.findByPoNumber(poNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with number " + poNumber));
    }

    @Override
    public PurchaseOrder update(Long id, PurchaseOrder request) {
        PurchaseOrder purchaseOrder = findById(id);
        validatePurchaseOrder(request);
        if (request.getSupplier() != null && request.getSupplier().getId() != null) {
            Supplier supplier = supplierRepository.findById(request.getSupplier().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id " + request.getSupplier().getId()));
            purchaseOrder.setSupplier(supplier);
        } else {
            throw new InvalidPurchaseOrderException("Supplier is required");
        }
        purchaseOrder.setOrderDate(request.getOrderDate());
        purchaseOrder.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        purchaseOrder.setStatus(request.getStatus() == null ? PurchaseOrderStatus.DRAFT : request.getStatus());

        purchaseOrder.getItems().clear();
        for (PurchaseOrderItem item : request.getItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                throw new InvalidPurchaseOrderException("Product is required for each purchase order item");
            }
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + item.getProduct().getId()));
            PurchaseOrderItem poItem = new PurchaseOrderItem();
            poItem.setProduct(product);
            poItem.setQuantity(item.getQuantity());
            poItem.setUnitPrice(item.getUnitPrice());
            poItem.setPurchaseOrder(purchaseOrder);
            if (poItem.getUnitPrice() == null || poItem.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidPurchaseOrderException("Unit price cannot be negative");
            }
            if (poItem.getQuantity() == null || poItem.getQuantity() <= 0) {
                throw new InvalidPurchaseOrderException("Quantity must be greater than 0");
            }
            poItem.setLineTotal(poItem.getUnitPrice().multiply(BigDecimal.valueOf(poItem.getQuantity())));
            purchaseOrder.getItems().add(poItem);
        }
        purchaseOrder.recalculateTotal();
        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    public void delete(Long id) {
        PurchaseOrder purchaseOrder = findById(id);
        purchaseOrderRepository.delete(purchaseOrder);
    }

    @Override
    public void updateStatus(Long id, String status) {
        PurchaseOrder purchaseOrder = findById(id);
        try {
            purchaseOrder.setStatus(PurchaseOrderStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new InvalidPurchaseOrderException("Invalid purchase order status: " + status);
        }
        purchaseOrderRepository.save(purchaseOrder);
    }

    public void validatePurchaseOrder(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null) {
            throw new InvalidPurchaseOrderException("Purchase order is required");
        }
        if (purchaseOrder.getSupplier() == null || purchaseOrder.getSupplier().getId() == null) {
            throw new InvalidPurchaseOrderException("Supplier is required");
        }
        if (purchaseOrder.getOrderDate() == null) {
            throw new InvalidPurchaseOrderException("Order date is required");
        }
        if (purchaseOrder.getExpectedDeliveryDate() == null) {
            throw new InvalidPurchaseOrderException("Expected delivery date is required");
        }
        if (purchaseOrder.getExpectedDeliveryDate().isBefore(purchaseOrder.getOrderDate())) {
            throw new InvalidPurchaseOrderException("Expected delivery date cannot be before order date");
        }
        if (purchaseOrder.getItems() == null || purchaseOrder.getItems().isEmpty()) {
            throw new InvalidPurchaseOrderException("At least one product must be added to the purchase order");
        }
        if (purchaseOrder.getStatus() == null) {
            purchaseOrder.setStatus(PurchaseOrderStatus.DRAFT);
        }
    }

    private String generatePoNumber() {
        long next = purchaseOrderRepository.count() + 1;
        return "PO-" + String.format("%03d", next);
    }
}
