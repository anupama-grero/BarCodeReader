package com.bci.productcrud.repository;

import com.bci.productcrud.model.Inventory;
import com.bci.productcrud.model.Location;
import com.bci.productcrud.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductAndLocation(Product product, Location location);
    List<Inventory> findByProduct(Product product);
    List<Inventory> findByLocation(Location location);
    boolean existsByProductAndLocation(Product product, Location location);
}
