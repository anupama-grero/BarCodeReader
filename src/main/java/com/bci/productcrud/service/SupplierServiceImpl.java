package com.bci.productcrud.service;

import com.bci.productcrud.exception.DuplicateSupplierException;
import com.bci.productcrud.exception.SupplierNotFoundException;
import com.bci.productcrud.model.Supplier;
import com.bci.productcrud.model.SupplierStatus;
import com.bci.productcrud.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public Supplier create(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier is required");
        }
        if (supplierRepository.existsByEmail(supplier.getEmail())) {
            throw new DuplicateSupplierException("A supplier with email " + supplier.getEmail() + " already exists");
        }
        if (supplier.getContactPerson() == null || supplier.getContactPerson().isBlank()) {
            supplier.setContactPerson(supplier.getSupplierName() != null ? supplier.getSupplierName() : "Main Contact");
        }
        if (supplier.getStatus() == null) {
            supplier.setStatus(SupplierStatus.ACTIVE);
        }
        supplier.setId(null);
        return supplierRepository.save(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Supplier findById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Supplier findByEmail(String email) {
        return supplierRepository.findByEmail(email)
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with email " + email));
    }

    @Override
    public Supplier update(Long id, Supplier request) {
        Supplier supplier = findById(id);

        supplierRepository.findByEmail(request.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateSupplierException("A supplier with email " + request.getEmail() + " already exists");
                });

        supplier.setName(request.getName());
        supplier.setContactPerson(request.getContactPerson() == null || request.getContactPerson().isBlank()
                ? (supplier.getSupplierName() != null ? supplier.getSupplierName() : "Main Contact")
                : request.getContactPerson());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setCompanyName(request.getCompanyName());
        if (request.getStatus() != null) {
            supplier.setStatus(request.getStatus());
        }
        return supplierRepository.save(supplier);
    }

    @Override
    public void delete(Long id) {
        Supplier supplier = findById(id);
        supplierRepository.delete(supplier);
    }
}
