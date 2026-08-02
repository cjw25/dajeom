package com.dajeom.repository;

import com.dajeom.entity.TarotCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarotCardRepository
        extends JpaRepository<TarotCard, Integer> {

    List<TarotCard> findByActiveTrueOrderBySortOrderAsc();

    boolean existsByCode(String code);
}