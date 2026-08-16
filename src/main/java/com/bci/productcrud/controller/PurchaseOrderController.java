package com.bci.productcrud.controller;

import com.bci.productcrud.model.*;
import com.bci.productcrud.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final SupplierService supplierService;
    private final ProductService productService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService,
                                  SupplierService supplierService,
                                  ProductService productService) {
        this.purchaseOrderService = purchaseOrderService;
        this.supplierService = supplierService;
        this.productService = productService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        return "purchase-orders/list";
    }

    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("purchaseOrder", new PurchaseOrder());
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("products", productService.findAll());
        return "purchase-orders/form";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("purchaseOrder", purchaseOrderService.findById(id));
        return "purchase-orders/view";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("purchaseOrder", purchaseOrderService.findById(id));
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("products", productService.findAll());
        return "purchase-orders/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("purchaseOrder") PurchaseOrder purchaseOrder,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("suppliers", supplierService.findAll());
            model.addAttribute("products", productService.findAll());
            return "purchase-orders/form";
        }
        purchaseOrderService.create(purchaseOrder);
        return "redirect:/purchase-orders";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("purchaseOrder") PurchaseOrder purchaseOrder,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("suppliers", supplierService.findAll());
            model.addAttribute("products", productService.findAll());
            return "purchase-orders/form";
        }
        purchaseOrderService.update(id, purchaseOrder);
        return "redirect:/purchase-orders";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        purchaseOrderService.delete(id);
        return "redirect:/purchase-orders";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        purchaseOrderService.updateStatus(id, status);
        return "redirect:/purchase-orders";
    }
}
