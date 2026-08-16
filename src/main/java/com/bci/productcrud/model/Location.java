package com.bci.productcrud.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "locations", uniqueConstraints = {
        @UniqueConstraint(name = "uk_locations_name", columnNames = "location_name")
})
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;

    @NotBlank(message = "Location name is required")
    @Column(name = "location_name", nullable = false, length = 100)
    private String locationName;

    @NotBlank(message = "Location address is required")
    @Column(nullable = false, length = 255)
    private String address;

    @OneToMany(mappedBy = "location")
    @JsonIgnore
    private List<Inventory> inventories = new ArrayList<>();

    public Location() {}

    public Location(String locationName, String address) {
        this.locationName = locationName;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Inventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<Inventory> inventories) {
        this.inventories = inventories;
    }
}
