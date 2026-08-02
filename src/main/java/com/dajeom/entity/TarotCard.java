package com.dajeom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tarot_cards",
        indexes = {
                @Index(
                        name = "idx_tarot_cards_sort_order",
                        columnList = "sort_order"
                ),
                @Index(
                        name = "idx_tarot_cards_active",
                        columnList = "is_active"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_tarot_cards_code",
                        columnNames = "code"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TarotCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Integer id;

    @Column(
            name = "code",
            nullable = false,
            length = 40
    )
    private String code;

    @Column(
            name = "name_ko",
            nullable = false,
            length = 100
    )
    private String nameKo;

    @Column(
            name = "name_en",
            nullable = false,
            length = 100
    )
    private String nameEn;

    @Column(
            name = "major_minor",
            nullable = false,
            length = 1
    )
    private String majorMinor;

    @Column(
            name = "suit",
            length = 20
    )
    private String suit;

    @Column(
            name = "card_number",
            length = 20
    )
    private String cardNumber;

    @Column(
            name = "image_url",
            length = 255
    )
    private String imageUrl;

    @Lob
    @Column(
            name = "upright_meaning",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String uprightMeaning;

    @Lob
    @Column(
            name = "reverse_meaning",
            columnDefinition = "TEXT"
    )
    private String reverseMeaning;

    @Column(
            name = "sort_order",
            nullable = false
    )
    private int sortOrder;

    @Column(
            name = "is_active",
            nullable = false
    )
    private boolean active;

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

    public static TarotCard create(
            String code,
            String nameKo,
            String nameEn,
            String majorMinor,
            String suit,
            String cardNumber,
            String imageUrl,
            String uprightMeaning,
            String reverseMeaning,
            int sortOrder
    ) {
        TarotCard card = new TarotCard();

        card.code = code;
        card.nameKo = nameKo;
        card.nameEn = nameEn;
        card.majorMinor = majorMinor;
        card.suit = suit;
        card.cardNumber = cardNumber;
        card.imageUrl = imageUrl;
        card.uprightMeaning = uprightMeaning;
        card.reverseMeaning = reverseMeaning;
        card.sortOrder = sortOrder;
        card.active = true;

        return card;
    }

    public void changeActive(boolean active) {
        this.active = active;
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