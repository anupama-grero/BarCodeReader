package com.bci.productcrud.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "goods_received_notes")
public class GoodsReceivedNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grn_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String grnNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "po_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by")
    private User receivedBy;

    @NotNull(message = "Received date is required")
    @Column(nullable = false)
    private LocalDate receivedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private GoodsReceivedNoteStatus status = GoodsReceivedNoteStatus.RECEIVED;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(length = 1000)
    private String remarks;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "goodsReceivedNote", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    @Valid
    private List<GoodsReceivedNoteItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.grnNumber == null || this.grnNumber.isBlank()) {
            this.grnNumber = generateGrnNumber();
        }
        recalculateTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        recalculateTotal();
    }

    public void addItem(GoodsReceivedNoteItem item) {
        items.add(item);
        item.setGoodsReceivedNote(this);
        recalculateTotal();
    }

    public void removeItem(GoodsReceivedNoteItem item) {
        items.remove(item);
        item.setGoodsReceivedNote(null);
        recalculateTotal();
    }

    public void recalculateTotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (GoodsReceivedNoteItem item : items) {
            if (item.getLineTotal() != null) {
                sum = sum.add(item.getLineTotal());
            }
        }
        this.totalAmount = sum.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private String generateGrnNumber() {
        return "GRN-" + String.format("%03d", System.currentTimeMillis() % 1000);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getGrnNumber() { return grnNumber; }
    public void setGrnNumber(String grnNumber) { this.grnNumber = grnNumber; }
    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrder purchaseOrder) { this.purchaseOrder = purchaseOrder; }
    public User getReceivedBy() { return receivedBy; }
    public void setReceivedBy(User receivedBy) { this.receivedBy = receivedBy; }
    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }
    public GoodsReceivedNoteStatus getStatus() { return status; }
    public void setStatus(GoodsReceivedNoteStatus status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<GoodsReceivedNoteItem> getItems() { return items; }
    public void setItems(List<GoodsReceivedNoteItem> items) { this.items = items; }
}
