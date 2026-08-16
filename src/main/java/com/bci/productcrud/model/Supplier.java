package com.bci.productcrud.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "suppliers",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
    }
)
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long id;

    @NotBlank(message = "Supplier name is required")
    @Size(max = 100, message = "Supplier name cannot exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String supplierName;

    @Size(max = 100, message = "Contact person cannot exceed 100 characters")
    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9\\s-]{7,15}$", message = "Please provide a valid phone number")
    @Column(nullable = false, length = 20)
    private String phone;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    @Column(length = 255)
    private String address;

    @Size(max = 200, message = "Bank details cannot exceed 200 characters")
    @Column(name = "bank_details", length = 200)
    private String bankDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private SupplierStatus status = SupplierStatus.ACTIVE;

    @Size(max = 150, message = "Company name cannot exceed 150 characters")
    @Column(name = "company_name", length = 150)
    private String companyName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "supplier", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "supplier")
    @JsonIgnore
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();

    public Supplier() {
        this.contactPerson = "Main Contact";
    }

    public Supplier(String supplierName, String email, String phone,
                    String address, String companyName) {
        this.supplierName = supplierName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.companyName = companyName;
        this.contactPerson = supplierName != null && !supplierName.isBlank() ? supplierName : "Main Contact";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return supplierName;
    }

    public void setName(String name) {
        this.supplierName = name;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(String bankDetails) {
        this.bankDetails = bankDetails;
    }

    public SupplierStatus getStatus() {
        return status;
    }

    public void setStatus(SupplierStatus status) {
        this.status = status;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }

    @PrePersist
    protected void onCreate() {
        if (supplierName == null || supplierName.isBlank()) {
            supplierName = "Unknown Supplier";
        }
        if (contactPerson == null || contactPerson.isBlank()) {
            contactPerson = supplierName != null && !supplierName.isBlank() ? supplierName : "Not Provided";
        }
        if (status == null) {
            status = SupplierStatus.ACTIVE;
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        if (supplierName == null || supplierName.isBlank()) {
            supplierName = "Unknown Supplier";
        }
        if (contactPerson == null || contactPerson.isBlank()) {
            contactPerson = supplierName != null && !supplierName.isBlank() ? supplierName : "Not Provided";
        }
        if (status == null) {
            status = SupplierStatus.ACTIVE;
        }
        updatedAt = LocalDateTime.now();
    }
}
