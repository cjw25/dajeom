package com.dajeom.repository;

import com.dajeom.entity.TarotSpreadPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarotSpreadPositionRepository
        extends JpaRepository<TarotSpreadPosition, Integer> {

    List<TarotSpreadPosition>
    findBySpreadIdOrderByPositionOrderAsc(
            Integer spreadId
    );

    long countBySpreadId(Integer spreadId);
}