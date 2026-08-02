package com.dajeom.repository;

import com.dajeom.entity.TarotSessionDraw;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarotSessionDrawRepository
        extends JpaRepository<TarotSessionDraw, Long> {

    long countByTarotSessionId(Long tarotSessionId);

    boolean existsByTarotSessionIdAndSlotIndex(
            Long tarotSessionId,
            int slotIndex
    );

    @EntityGraph(
            attributePaths = {
                    "position",
                    "card"
            }
    )
    List<TarotSessionDraw>
    findByTarotSessionIdOrderByDrawOrderAsc(
            Long tarotSessionId
    );
}