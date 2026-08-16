package com.bci.productcrud.service;

import com.bci.productcrud.model.GoodsReceivedNote;

import java.util.List;

public interface GoodsReceivedNoteService {
    GoodsReceivedNote create(GoodsReceivedNote goodsReceivedNote);
    List<GoodsReceivedNote> findAll();
    GoodsReceivedNote findById(Long id);
    GoodsReceivedNote findByGrnNumber(String grnNumber);
    GoodsReceivedNote update(Long id, GoodsReceivedNote goodsReceivedNote);
    void delete(Long id);
    void updateReceivingStatus(Long purchaseOrderId);
}
