package com.bci.productcrud.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "goods_received_note_items")
public class GoodsReceivedNoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "goods_received_note_id", nullable = false)
    @JsonBackReference
    private GoodsReceivedNote goodsReceivedNote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer orderedQuantity;

    @NotNull(message = "Received quantity is required")
    @Min(value = 0, message = "Received quantity cannot be negative")
    @Column(nullable = false)
    private Integer receivedQuantity;

    @NotNull(message = "Unit price is required")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal lineTotal = BigDecimal.ZERO;

    @PrePersist
    @PreUpdate
    protected void calculateLineTotal() {
        if (this.receivedQuantity != null && this.unitPrice != null) {
            this.lineTotal = this.unitPrice.multiply(BigDecimal.valueOf(this.receivedQuantity)).setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GoodsReceivedNote getGoodsReceivedNote() { return goodsReceivedNote; }
    public void setGoodsReceivedNote(GoodsReceivedNote goodsReceivedNote) { this.goodsReceivedNote = goodsReceivedNote; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getOrderedQuantity() { return orderedQuantity; }
    public void setOrderedQuantity(Integer orderedQuantity) { this.orderedQuantity = orderedQuantity; }
    public Integer getReceivedQuantity() { return receivedQuantity; }
    public void setReceivedQuantity(Integer receivedQuantity) { this.receivedQuantity = receivedQuantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
