package com.bci.productcrud.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_barcode", columnNames = "barcode")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @NotBlank(message = "Product name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(length = 100)
    private String category;

    @Column(length = 100)
    private String brand;

    @Column(length = 50)
    private String size;

    @Column(length = 50)
    private String color;

    @NotBlank(message = "Barcode is required")
    @Column(nullable = false, unique = true, length = 100)
    private String barcode;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be zero or greater")
    @Column(name = "price", nullable = false)
    private Double price = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ProductStatus status = ProductStatus.ACTIVE;

    private String description;

    @NotNull(message = "Available quantity is required")
    @PositiveOrZero(message = "Quantity cannot be negative")
    @Column(nullable = false)
    private Integer quantity = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    @JsonBackReference
    private Supplier supplier;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<Inventory> inventories = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<GoodsReceivedNoteItem> goodsReceivedNoteItems = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<SalesReceiptItem> salesReceiptItems = new ArrayList<>();

    @Column(updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductName() {
        return name;
    }

    public void setProductName(String productName) {
        this.name = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getPurchasePrice() {
        return price == null ? BigDecimal.ZERO : BigDecimal.valueOf(price);
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.price = purchasePrice == null ? 0.0 : purchasePrice.doubleValue();
    }

    public BigDecimal getSellingPrice() {
        return price == null ? BigDecimal.ZERO : BigDecimal.valueOf(price);
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.price = sellingPrice == null ? 0.0 : sellingPrice.doubleValue();
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price == null ? 0.0 : price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity == null ? 0 : quantity;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    @JsonProperty("supplierId")
    public Long getSupplierId() {
        return supplier != null ? supplier.getId() : null;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public List<Inventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<Inventory> inventories) {
        this.inventories = inventories;
    }

    public List<PurchaseOrderItem> getPurchaseOrderItems() {
        return purchaseOrderItems;
    }

    public void setPurchaseOrderItems(List<PurchaseOrderItem> purchaseOrderItems) {
        this.purchaseOrderItems = purchaseOrderItems;
    }

    public List<GoodsReceivedNoteItem> getGoodsReceivedNoteItems() {
        return goodsReceivedNoteItems;
    }

    public void setGoodsReceivedNoteItems(List<GoodsReceivedNoteItem> goodsReceivedNoteItems) {
        this.goodsReceivedNoteItems = goodsReceivedNoteItems;
    }

    public List<SalesReceiptItem> getSalesReceiptItems() {
        return salesReceiptItems;
    }

    public void setSalesReceiptItems(List<SalesReceiptItem> salesReceiptItems) {
        this.salesReceiptItems = salesReceiptItems;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void onCreate() {
        if (name == null || name.isBlank()) {
            name = "Unnamed Product";
        }
        if (price == null) {
            price = 0.0;
        }
        if (status == null) {
            status = ProductStatus.ACTIVE;
        }
        if (quantity == null) {
            quantity = 0;
        }
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        if (name == null || name.isBlank()) {
            name = "Unnamed Product";
        }
        if (price == null) {
            price = 0.0;
        }
        if (status == null) {
            status = ProductStatus.ACTIVE;
        }
        if (quantity == null) {
            quantity = 0;
        }
        updatedAt = Instant.now();
    }
}
