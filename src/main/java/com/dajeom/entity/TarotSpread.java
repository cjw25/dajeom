package com.dajeom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tarot_spreads",
        indexes = {
                @Index(
                        name = "idx_tarot_spreads_active_sort",
                        columnList = "is_active, sort_order"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_tarot_spreads_name",
                        columnNames = "name"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TarotSpread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spread_id")
    private Integer id;

    @Column(
            name = "name",
            nullable = false,
            length = 100
    )
    private String name;

    @Column(
            name = "card_count",
            nullable = false
    )
    private int cardCount;

    @Lob
    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;

    /*
     * 프론트에서 사용할 전용 배치 코드.
     *
     * SINGLE
     * TIMELINE_3
     * RELATIONSHIP_5
     * CHOICE_6
     * CELTIC_10
     */
    @Column(
            name = "layout_code",
            length = 40
    )
    private String layoutCode;

    @Column(
            name = "is_active",
            nullable = false
    )
    private boolean active;

    @Column(
            name = "sort_order",
            nullable = false
    )
    private int sortOrder;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public static TarotSpread create(
            String name,
            int cardCount,
            String description,
            String layoutCode,
            int sortOrder
    ) {
        TarotSpread spread = new TarotSpread();

        spread.name = name;
        spread.cardCount = cardCount;
        spread.description = description;
        spread.layoutCode = layoutCode;
        spread.active = true;
        spread.sortOrder = sortOrder;

        return spread;
    }

    public void updateDefinition(
            int cardCount,
            String description,
            String layoutCode,
            int sortOrder
    ) {
        this.cardCount = cardCount;
        this.description = description;
        this.layoutCode = layoutCode;
        this.sortOrder = sortOrder;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    @PrePersist
    private void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}