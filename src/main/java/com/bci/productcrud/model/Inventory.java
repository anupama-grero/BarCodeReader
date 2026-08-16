package com.bci.productcrud.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "inventories", uniqueConstraints = {
        @UniqueConstraint(name = "uk_inventory_product_location", columnNames = {"product_id", "location_id"})
})
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    @JsonIgnore
    private Location location;

    @NotNull(message = "Quantity on hand is required")
    @Min(value = 0, message = "Quantity on hand cannot be negative")
    @Column(name = "quantity_on_hand", nullable = false)
    private Integer quantityOnHand = 0;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    public Inventory() {
        this.lastUpdated = Instant.now();
    }

    @PrePersist
    @PreUpdate
    protected void setLastUpdated() {
        this.lastUpdated = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
