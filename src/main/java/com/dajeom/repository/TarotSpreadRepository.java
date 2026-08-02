package com.dajeom.repository;

import com.dajeom.entity.TarotSpread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarotSpreadRepository
        extends JpaRepository<TarotSpread, Integer> {

    List<TarotSpread> findByActiveTrueOrderBySortOrderAsc();

    Optional<TarotSpread> findByName(String name);
}