package com.bci.productcrud.service;

import com.bci.productcrud.exception.*;
import com.bci.productcrud.model.*;
import com.bci.productcrud.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class GoodsReceivedNoteServiceImpl implements GoodsReceivedNoteService {

    private final GoodsReceivedNoteRepository goodsReceivedNoteRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;

    public GoodsReceivedNoteServiceImpl(GoodsReceivedNoteRepository goodsReceivedNoteRepository,
                                       PurchaseOrderRepository purchaseOrderRepository,
                                       ProductRepository productRepository) {
        this.goodsReceivedNoteRepository = goodsReceivedNoteRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public GoodsReceivedNote create(GoodsReceivedNote goodsReceivedNote) {
        validateGoodsReceivedNote(goodsReceivedNote);

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(goodsReceivedNote.getPurchaseOrder().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id " + goodsReceivedNote.getPurchaseOrder().getId()));

        int totalReceived = 0;
        for (GoodsReceivedNoteItem item : goodsReceivedNote.getItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                throw new InvalidGoodsReceivedNoteException("Product is required");
            }
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + item.getProduct().getId()));
            item.setProduct(product);
            if (item.getReceivedQuantity() == null || item.getReceivedQuantity() < 0) {
                throw new InvalidGoodsReceivedNoteException("Received quantity cannot be negative");
            }
            if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidGoodsReceivedNoteException("Unit price cannot be negative");
            }

            int remaining = getRemainingQuantityForProduct(purchaseOrder, product);
            if (item.getReceivedQuantity() > remaining) {
                throw new InsufficientQuantityException("Received quantity cannot exceed the remaining ordered quantity for product " + product.getName());
            }
            item.setOrderedQuantity(getOrderedQuantityForProduct(purchaseOrder, product));
            item.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getReceivedQuantity())));
            totalReceived += item.getReceivedQuantity();
        }

        if (goodsReceivedNote.getGrnNumber() == null || goodsReceivedNote.getGrnNumber().isBlank()) {
            goodsReceivedNote.setGrnNumber(generateGrnNumber());
        }
        if (goodsReceivedNoteRepository.existsByGrnNumber(goodsReceivedNote.getGrnNumber())) {
            throw new InvalidGoodsReceivedNoteException("GRN number already exists: " + goodsReceivedNote.getGrnNumber());
        }

        goodsReceivedNote.setPurchaseOrder(purchaseOrder);
        goodsReceivedNote.recalculateTotal();
        GoodsReceivedNote saved = goodsReceivedNoteRepository.save(goodsReceivedNote);

        for (GoodsReceivedNoteItem item : saved.getItems()) {
            Product product = item.getProduct();
            int newStock = product.getQuantity() + item.getReceivedQuantity();
            product.setQuantity(newStock);
            productRepository.save(product);
        }

        updateReceivingStatus(purchaseOrder.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoodsReceivedNote> findAll() {
        return goodsReceivedNoteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public GoodsReceivedNote findById(Long id) {
        return goodsReceivedNoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goods received note not found with id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public GoodsReceivedNote findByGrnNumber(String grnNumber) {
        return goodsReceivedNoteRepository.findByGrnNumber(grnNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Goods received note not found with number " + grnNumber));
    }

    @Override
    public GoodsReceivedNote update(Long id, GoodsReceivedNote request) {
        GoodsReceivedNote existing = findById(id);
        validateGoodsReceivedNote(request);
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(request.getPurchaseOrder().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id " + request.getPurchaseOrder().getId()));
        existing.setPurchaseOrder(purchaseOrder);
        existing.setReceivedDate(request.getReceivedDate());
        existing.setStatus(request.getStatus() == null ? GoodsReceivedNoteStatus.RECEIVED : request.getStatus());
        existing.setRemarks(request.getRemarks());

        existing.getItems().clear();
        for (GoodsReceivedNoteItem item : request.getItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                throw new InvalidGoodsReceivedNoteException("Product is required");
            }
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + item.getProduct().getId()));
            GoodsReceivedNoteItem grnItem = new GoodsReceivedNoteItem();
            grnItem.setProduct(product);
            grnItem.setOrderedQuantity(item.getOrderedQuantity());
            grnItem.setReceivedQuantity(item.getReceivedQuantity());
            grnItem.setUnitPrice(item.getUnitPrice());
            grnItem.setGoodsReceivedNote(existing);
            if (grnItem.getReceivedQuantity() == null || grnItem.getReceivedQuantity() < 0) {
                throw new InvalidGoodsReceivedNoteException("Received quantity cannot be negative");
            }
            if (grnItem.getUnitPrice() == null || grnItem.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidGoodsReceivedNoteException("Unit price cannot be negative");
            }
            int remaining = getRemainingQuantityForProduct(purchaseOrder, product);
            if (grnItem.getReceivedQuantity() > remaining) {
                throw new InsufficientQuantityException("Received quantity cannot exceed the remaining ordered quantity for product " + product.getName());
            }
            grnItem.setLineTotal(grnItem.getUnitPrice().multiply(BigDecimal.valueOf(grnItem.getReceivedQuantity())));
            existing.getItems().add(grnItem);
        }
        existing.recalculateTotal();
        return goodsReceivedNoteRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        GoodsReceivedNote note = findById(id);
        goodsReceivedNoteRepository.delete(note);
    }

    @Override
    public void updateReceivingStatus(Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id " + purchaseOrderId));

        int totalOrdered = 0;
        int totalReceived = 0;
        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
            totalOrdered += item.getQuantity();
            List<GoodsReceivedNoteItem> items = goodsReceivedNoteRepository.findAll().stream()
                    .filter(note -> note.getPurchaseOrder().getId().equals(purchaseOrder.getId()))
                    .flatMap(note -> note.getItems().stream())
                    .filter(grnItem -> grnItem.getProduct().getId().equals(item.getProduct().getId()))
                    .toList();
            int receivedForProduct = items.stream().mapToInt(GoodsReceivedNoteItem::getReceivedQuantity).sum();
            totalReceived += receivedForProduct;
        }

        if (totalReceived <= 0) {
            purchaseOrder.setStatus(PurchaseOrderStatus.PENDING);
        } else if (totalReceived >= totalOrdered) {
            purchaseOrder.setStatus(PurchaseOrderStatus.RECEIVED);
        } else {
            purchaseOrder.setStatus(PurchaseOrderStatus.PARTIALLY_RECEIVED);
        }

        purchaseOrderRepository.save(purchaseOrder);
    }

    private void validateGoodsReceivedNote(GoodsReceivedNote note) {
        if (note == null) {
            throw new InvalidGoodsReceivedNoteException("Goods received note is required");
        }
        if (note.getPurchaseOrder() == null || note.getPurchaseOrder().getId() == null) {
            throw new InvalidGoodsReceivedNoteException("Purchase order is required");
        }
        if (note.getReceivedDate() == null) {
            throw new InvalidGoodsReceivedNoteException("Received date is required");
        }
        if (note.getItems() == null || note.getItems().isEmpty()) {
            throw new InvalidGoodsReceivedNoteException("At least one item must be received");
        }
    }

    private int getRemainingQuantityForProduct(PurchaseOrder purchaseOrder, Product product) {
        int ordered = purchaseOrder.getItems().stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(product.getId()))
                .mapToInt(item -> item.getQuantity() == null ? 0 : item.getQuantity())
                .sum();

        int received = goodsReceivedNoteRepository.findAll().stream()
                .filter(note -> note.getPurchaseOrder() != null && note.getPurchaseOrder().getId().equals(purchaseOrder.getId()))
                .flatMap(note -> note.getItems().stream())
                .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(product.getId()))
                .mapToInt(item -> item.getReceivedQuantity() == null ? 0 : item.getReceivedQuantity())
                .sum();

        return ordered - received;
    }

    private int getOrderedQuantityForProduct(PurchaseOrder purchaseOrder, Product product) {
        return purchaseOrder.getItems().stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(product.getId()))
                .mapToInt(item -> item.getQuantity() == null ? 0 : item.getQuantity())
                .sum();
    }

    private String generateGrnNumber() {
        long next = goodsReceivedNoteRepository.count() + 1;
        return "GRN-" + String.format("%03d", next);
    }
}
