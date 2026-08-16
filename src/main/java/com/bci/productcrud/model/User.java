package com.bci.productcrud.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_user_email", columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Username is required")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "contact_no", length = 20)
    private String contactNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserStatus status = UserStatus.ACTIVE;

    @OneToMany(mappedBy = "createdBy")
    @JsonIgnore
    private List<PurchaseOrder> createdPurchaseOrders = new ArrayList<>();

    @OneToMany(mappedBy = "approvedBy")
    @JsonIgnore
    private List<PurchaseOrder> approvedPurchaseOrders = new ArrayList<>();

    @OneToMany(mappedBy = "receivedBy")
    @JsonIgnore
    private List<GoodsReceivedNote> receivedGoodsNotes = new ArrayList<>();

    @OneToMany(mappedBy = "cashier")
    @JsonIgnore
    private List<SalesPaymentReceipt> salesReceipts = new ArrayList<>();

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public List<PurchaseOrder> getCreatedPurchaseOrders() {
        return createdPurchaseOrders;
    }

    public void setCreatedPurchaseOrders(List<PurchaseOrder> createdPurchaseOrders) {
        this.createdPurchaseOrders = createdPurchaseOrders;
    }

    public List<PurchaseOrder> getApprovedPurchaseOrders() {
        return approvedPurchaseOrders;
    }

    public void setApprovedPurchaseOrders(List<PurchaseOrder> approvedPurchaseOrders) {
        this.approvedPurchaseOrders = approvedPurchaseOrders;
    }

    public List<GoodsReceivedNote> getReceivedGoodsNotes() {
        return receivedGoodsNotes;
    }

    public void setReceivedGoodsNotes(List<GoodsReceivedNote> receivedGoodsNotes) {
        this.receivedGoodsNotes = receivedGoodsNotes;
    }

    public List<SalesPaymentReceipt> getSalesReceipts() {
        return salesReceipts;
    }

    public void setSalesReceipts(List<SalesPaymentReceipt> salesReceipts) {
        this.salesReceipts = salesReceipts;
    }
}
