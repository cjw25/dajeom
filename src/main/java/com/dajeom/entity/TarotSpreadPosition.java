package com.dajeom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tarot_spread_positions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_tarot_spread_position_order",
                        columnNames = {
                                "spread_id",
                                "position_order"
                        }
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TarotSpreadPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Integer id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "spread_id",
            nullable = false
    )
    private TarotSpread spread;

    @Column(
            name = "position_order",
            nullable = false
    )
    private int positionOrder;

    @Column(
            name = "position_name",
            nullable = false,
            length = 100
    )
    private String positionName;

    @Column(
            name = "description",
            length = 255
    )
    private String description;

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

    public static TarotSpreadPosition create(
            TarotSpread spread,
            int positionOrder,
            String positionName,
            String description
    ) {
        TarotSpreadPosition position =
                new TarotSpreadPosition();

        position.spread = spread;
        position.positionOrder = positionOrder;
        position.positionName = positionName;
        position.description = description;

        return position;
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