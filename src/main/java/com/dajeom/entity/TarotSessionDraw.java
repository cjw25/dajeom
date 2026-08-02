package com.dajeom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tarot_session_draws",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_tarot_session_draw_order",
                        columnNames = {
                                "tarot_session_id",
                                "draw_order"
                        }
                ),
                @UniqueConstraint(
                        name = "uq_tarot_session_slot",
                        columnNames = {
                                "tarot_session_id",
                                "slot_index"
                        }
                ),
                @UniqueConstraint(
                        name = "uq_tarot_session_card",
                        columnNames = {
                                "tarot_session_id",
                                "card_id"
                        }
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TarotSessionDraw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_draw_id")
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "tarot_session_id",
            nullable = false
    )
    private TarotSession tarotSession;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "position_id",
            nullable = false
    )
    private TarotSpreadPosition position;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "card_id",
            nullable = false
    )
    private TarotCard card;

    @Column(
            name = "slot_index",
            nullable = false
    )
    private int slotIndex;

    @Column(
            name = "is_reversed",
            nullable = false
    )
    private boolean reversed;

    @Column(
            name = "draw_order",
            nullable = false
    )
    private int drawOrder;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    public static TarotSessionDraw create(
            TarotSession session,
            TarotSpreadPosition position,
            TarotCard card,
            int slotIndex,
            boolean reversed,
            int drawOrder
    ) {
        TarotSessionDraw draw =
                new TarotSessionDraw();

        draw.tarotSession = session;
        draw.position = position;
        draw.card = card;
        draw.slotIndex = slotIndex;
        draw.reversed = reversed;
        draw.drawOrder = drawOrder;
        draw.createdAt = LocalDateTime.now();

        return draw;
    }
}