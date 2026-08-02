package com.dajeom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "tarot_sessions",
        indexes = {
                @Index(
                        name = "idx_tarot_sessions_key",
                        columnList = "session_key",
                        unique = true
                ),
                @Index(
                        name = "idx_tarot_sessions_status_created",
                        columnList = "status, created_at"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TarotSession {

    private static final int SESSION_MINUTES = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tarot_session_id")
    private Long id;

    @Column(
            name = "session_key",
            nullable = false,
            unique = true,
            length = 36
    )
    private String sessionKey;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "spread_id",
            nullable = false
    )
    private TarotSpread spread;

    @Lob
    @Column(
            name = "question",
            columnDefinition = "TEXT"
    )
    private String question;

    @Column(
            name = "allow_reversed",
            nullable = false
    )
    private boolean allowReversed;

    /*
     * 서버가 섞은 카드 ID 순서.
     * 예: 15,3,72,1,44...
     *
     * 프론트에는 이 값을 보내지 않는다.
     */
    @Lob
    @Column(
            name = "deck_order",
            nullable = false,
            columnDefinition = "LONGTEXT"
    )
    private String deckOrder;

    @Column(
            name = "deck_size",
            nullable = false
    )
    private int deckSize;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private TarotSessionStatus status;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private LocalDateTime expiresAt;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public static TarotSession create(
            TarotSpread spread,
            String question,
            boolean allowReversed,
            String deckOrder,
            int deckSize
    ) {
        LocalDateTime now = LocalDateTime.now();

        TarotSession session = new TarotSession();

        session.sessionKey =
                UUID.randomUUID().toString();

        session.spread = spread;
        session.question = normalizeQuestion(question);
        session.allowReversed = allowReversed;
        session.deckOrder = deckOrder;
        session.deckSize = deckSize;
        session.status = TarotSessionStatus.SHUFFLED;
        session.createdAt = now;
        session.expiresAt =
                now.plusMinutes(SESSION_MINUTES);

        return session;
    }

    public void startDrawing() {
        if (status == TarotSessionStatus.SHUFFLED) {
            status = TarotSessionStatus.DRAWING;
        }
    }

    public void complete() {
        status = TarotSessionStatus.COMPLETED;
        completedAt = LocalDateTime.now();
    }

    public void expire() {
        status = TarotSessionStatus.EXPIRED;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isCompleted() {
        return status == TarotSessionStatus.COMPLETED;
    }

    private static String normalizeQuestion(
            String question
    ) {
        if (question == null) {
            return null;
        }

        String normalized = question.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }
}