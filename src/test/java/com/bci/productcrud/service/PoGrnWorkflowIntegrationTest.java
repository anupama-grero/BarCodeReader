package com.bci.productcrud.service;

import com.bci.productcrud.exception.InsufficientQuantityException;
import com.bci.productcrud.model.*;
import com.bci.productcrud.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:productcrud-po-grn;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@Transactional
class PoGrnWorkflowIntegrationTest {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private GoodsReceivedNoteService goodsReceivedNoteService;

    @Test
    void shouldCreatePurchaseOrderAndApplyGrnStockInSequence() {
        Supplier supplier = new Supplier();
        supplier.setName("ABC Suppliers");
        supplier.setEmail("abc@example.com");
        supplier.setPhone("0771234567");
        supplier.setAddress("Colombo");
        supplier.setCompanyName("ABC Pvt Ltd");
        supplier = supplierRepository.save(supplier);

        Product product = new Product();
        product.setBarcode("RICE-5KG");
        product.setName("Rice 5kg");
        product.setDescription("Rice bag");
        product.setPrice(500.00);
        product.setQuantity(50);
        product.setSupplier(supplier);
        product = productRepository.save(product);

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setOrderDate(LocalDate.now());
        purchaseOrder.setExpectedDeliveryDate(LocalDate.now().plusDays(7));
        purchaseOrder.setStatus(PurchaseOrderStatus.DRAFT);

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setProduct(product);
        item.setQuantity(100);
        item.setUnitPrice(new BigDecimal("500"));
        purchaseOrder.addItem(item);

        PurchaseOrder createdPo = purchaseOrderService.create(purchaseOrder);
        assertNotNull(createdPo.getId());
        assertEquals("PO-001", createdPo.getPoNumber());
        assertEquals(new BigDecimal("50000.00"), createdPo.getTotalAmount());

        GoodsReceivedNote note = new GoodsReceivedNote();
        note.setPurchaseOrder(createdPo);
        note.setReceivedDate(LocalDate.now());
        note.setStatus(GoodsReceivedNoteStatus.RECEIVED);

        GoodsReceivedNoteItem grnItem = new GoodsReceivedNoteItem();
        grnItem.setProduct(product);
        grnItem.setOrderedQuantity(100);
        grnItem.setReceivedQuantity(60);
        grnItem.setUnitPrice(new BigDecimal("500"));
        note.addItem(grnItem);

        GoodsReceivedNote createdGrn = goodsReceivedNoteService.create(note);
        assertEquals(110, productRepository.findById(product.getId()).orElseThrow().getQuantity());
        assertEquals(PurchaseOrderStatus.PARTIALLY_RECEIVED, purchaseOrderService.findById(createdPo.getId()).getStatus());

        GoodsReceivedNote secondGrn = new GoodsReceivedNote();
        secondGrn.setPurchaseOrder(createdPo);
        secondGrn.setReceivedDate(LocalDate.now().plusDays(1));
        secondGrn.setStatus(GoodsReceivedNoteStatus.RECEIVED);

        GoodsReceivedNoteItem secondItem = new GoodsReceivedNoteItem();
        secondItem.setProduct(product);
        secondItem.setOrderedQuantity(100);
        secondItem.setReceivedQuantity(40);
        secondItem.setUnitPrice(new BigDecimal("500"));
        secondGrn.addItem(secondItem);

        GoodsReceivedNote secondCreatedGrn = goodsReceivedNoteService.create(secondGrn);
        assertEquals(150, productRepository.findById(product.getId()).orElseThrow().getQuantity());
        assertEquals(PurchaseOrderStatus.RECEIVED, purchaseOrderService.findById(createdPo.getId()).getStatus());
        assertNotNull(secondCreatedGrn.getId());
    }

    @Test
    void shouldRejectReceivedQuantityAboveRemainingOnPurchaseOrder() {
        Supplier supplier = new Supplier();
        supplier.setName("DEF Suppliers");
        supplier.setEmail("def@example.com");
        supplier.setPhone("0777654321");
        supplier.setAddress("Kandy");
        supplier.setCompanyName("DEF Pvt Ltd");
        supplier = supplierRepository.save(supplier);

        Product product = new Product();
        product.setBarcode("BEANS-1KG");
        product.setName("Beans 1kg");
        product.setDescription("Beans");
        product.setPrice(250.00);
        product.setQuantity(25);
        product.setSupplier(supplier);
        product = productRepository.save(product);

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setOrderDate(LocalDate.now());
        purchaseOrder.setExpectedDeliveryDate(LocalDate.now().plusDays(4));
        purchaseOrder.setStatus(PurchaseOrderStatus.DRAFT);

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setProduct(product);
        item.setQuantity(10);
        item.setUnitPrice(new BigDecimal("250"));
        purchaseOrder.addItem(item);

        PurchaseOrder createdPo = purchaseOrderService.create(purchaseOrder);

        GoodsReceivedNote note = new GoodsReceivedNote();
        note.setPurchaseOrder(createdPo);
        note.setReceivedDate(LocalDate.now());
        note.setStatus(GoodsReceivedNoteStatus.RECEIVED);

        GoodsReceivedNoteItem grnItem = new GoodsReceivedNoteItem();
        grnItem.setProduct(product);
        grnItem.setOrderedQuantity(10);
        grnItem.setReceivedQuantity(11);
        grnItem.setUnitPrice(new BigDecimal("250"));
        note.addItem(grnItem);

        assertThrows(InsufficientQuantityException.class, () -> goodsReceivedNoteService.create(note));
    }
}
