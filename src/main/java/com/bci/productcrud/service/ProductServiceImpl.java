package com.bci.productcrud.service;

import com.bci.productcrud.exception.DuplicateBarcodeException;
import com.bci.productcrud.exception.ProductNotFoundException;
import com.bci.productcrud.exception.SupplierNotFoundException;
import com.bci.productcrud.model.Product;
import com.bci.productcrud.model.Supplier;
import com.bci.productcrud.repository.ProductRepository;
import com.bci.productcrud.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public ProductServiceImpl(ProductRepository productRepository, SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public Product create(Product product) {
        if (productRepository.existsByBarcode(product.getBarcode())) {
            throw new DuplicateBarcodeException("A product with barcode " + product.getBarcode() + " already exists");
        }

        if (product.getSupplier() != null && product.getSupplier().getId() != null) {
            Supplier supplier = supplierRepository.findById(product.getSupplier().getId())
                    .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with id " + product.getSupplier().getId()));
            product.setSupplier(supplier);
        } else {
            product.setSupplier(null);
        }

        product.setId(null);
        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Product findByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with barcode " + barcode));
    }

    @Override
    public Product update(Long id, Product request) {
        Product product = findById(id);

        productRepository.findByBarcode(request.getBarcode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateBarcodeException("A product with barcode " + request.getBarcode() + " already exists");
                });

        product.setBarcode(request.getBarcode());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        if (request.getSupplier() != null && request.getSupplier().getId() != null) {
            Supplier supplier = supplierRepository.findById(request.getSupplier().getId())
                    .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with id " + request.getSupplier().getId()));
            product.setSupplier(supplier);
        } else {
            product.setSupplier(null);
        }

        return productRepository.save(product);
    }

    @Override
    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }
}
