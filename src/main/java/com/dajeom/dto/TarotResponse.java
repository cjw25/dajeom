package com.dajeom.dto;

import java.time.LocalDateTime;
import java.util.List;

public final class TarotResponse {

    private TarotResponse() {
    }

    public record SpreadResponse(
            Integer spreadId,
            String name,
            int cardCount,
            String description,
            String layoutCode,
            List<PositionResponse> positions
    ) {
    }

    public record PositionResponse(
            Integer positionId,
            int positionOrder,
            String positionName,
            String description
    ) {
    }

    public record SessionStartResponse(
            String sessionKey,
            String status,
            Integer spreadId,
            String spreadName,
            String layoutCode,
            int requiredCardCount,
            int deckSize,
            String cardBackImageUrl,
            List<Integer> slotIndexes,
            List<PositionResponse> positions,
            LocalDateTime expiresAt
    ) {
    }

    public record DrawResponse(
            String sessionKey,
            String status,
            int drawOrder,
            Integer positionId,
            String positionName,
            int selectedSlotIndex,
            int selectedCount,
            int requiredCardCount,
            int remainingCardCount,
            List<Integer> remainingSlotIndexes,
            boolean readyToReveal
    ) {
    }

    public record RevealedCardResponse(
            int drawOrder,
            Integer positionId,
            String positionName,
            String positionDescription,
            Integer cardId,
            String cardCode,
            String nameKo,
            String nameEn,
            String majorMinor,
            String suit,
            String cardNumber,
            String imageUrl,
            boolean reversed,
            String direction,
            String meaning
    ) {
    }

    public record RevealResponse(
            String sessionKey,
            String status,
            Integer spreadId,
            String spreadName,
            String layoutCode,
            String question,
            List<RevealedCardResponse> cards,
            LocalDateTime completedAt
    ) {
    }
}