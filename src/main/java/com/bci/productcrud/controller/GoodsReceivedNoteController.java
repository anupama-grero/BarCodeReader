package com.bci.productcrud.controller;

import com.bci.productcrud.model.*;
import com.bci.productcrud.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/grn")
public class GoodsReceivedNoteController {

    private final GoodsReceivedNoteService goodsReceivedNoteService;
    private final PurchaseOrderService purchaseOrderService;

    public GoodsReceivedNoteController(GoodsReceivedNoteService goodsReceivedNoteService,
                                      PurchaseOrderService purchaseOrderService) {
        this.goodsReceivedNoteService = goodsReceivedNoteService;
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("grns", goodsReceivedNoteService.findAll());
        return "grn/list";
    }

    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("goodsReceivedNote", new GoodsReceivedNote());
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        return "grn/form";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("goodsReceivedNote", goodsReceivedNoteService.findById(id));
        return "grn/view";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("goodsReceivedNote", goodsReceivedNoteService.findById(id));
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        return "grn/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("goodsReceivedNote") GoodsReceivedNote goodsReceivedNote,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
            return "grn/form";
        }
        goodsReceivedNoteService.create(goodsReceivedNote);
        return "redirect:/grn";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("goodsReceivedNote") GoodsReceivedNote goodsReceivedNote,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
            return "grn/form";
        }
        goodsReceivedNoteService.update(id, goodsReceivedNote);
        return "redirect:/grn";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        goodsReceivedNoteService.delete(id);
        return "redirect:/grn";
    }
}
