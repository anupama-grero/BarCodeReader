package com.bci.productcrud.repository;

import com.bci.productcrud.model.GoodsReceivedNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoodsReceivedNoteRepository extends JpaRepository<GoodsReceivedNote, Long> {
    Optional<GoodsReceivedNote> findByGrnNumber(String grnNumber);
    boolean existsByGrnNumber(String grnNumber);
}
