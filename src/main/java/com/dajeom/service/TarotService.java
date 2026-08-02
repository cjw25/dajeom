package com.dajeom.service;

import com.dajeom.dto.TarotDrawRequest;
import com.dajeom.dto.TarotResponse;
import com.dajeom.dto.TarotSessionCreateRequest;
import com.dajeom.entity.*;
import com.dajeom.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TarotService {

    private static final int REQUIRED_DECK_SIZE = 78;

    private static final String CARD_BACK_IMAGE_URL =
            "/img/tarot/card-back.webp";

    private final TarotCardRepository tarotCardRepository;
    private final TarotSpreadRepository tarotSpreadRepository;
    private final TarotSpreadPositionRepository tarotSpreadPositionRepository;
    private final TarotSessionRepository tarotSessionRepository;
    private final TarotSessionDrawRepository tarotSessionDrawRepository;

    private final SecureRandom secureRandom =
            new SecureRandom();

    @Transactional(readOnly = true)
    public List<TarotResponse.SpreadResponse> getSpreads() {

        return tarotSpreadRepository
                .findByActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(this::toSpreadResponse)
                .toList();
    }

    @Transactional
    public TarotResponse.SessionStartResponse createSession(
            TarotSessionCreateRequest request
    ) {
        TarotSpread spread =
                tarotSpreadRepository
                        .findById(request.spreadId())
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "선택한 타로 스프레드를 찾을 수 없습니다."
                                )
                        );

        if (!spread.isActive()) {
            throw new IllegalStateException(
                    "현재 사용할 수 없는 타로 스프레드입니다."
            );
        }

        List<TarotSpreadPosition> positions =
                tarotSpreadPositionRepository
                        .findBySpreadIdOrderByPositionOrderAsc(
                                spread.getId()
                        );

        if (positions.size() != spread.getCardCount()) {
            throw new IllegalStateException(
                    "스프레드 카드 수와 위치 설정이 일치하지 않습니다."
            );
        }

        List<TarotCard> cards =
                new ArrayList<>(
                        tarotCardRepository
                                .findByActiveTrueOrderBySortOrderAsc()
                );

        if (cards.size() != REQUIRED_DECK_SIZE) {
            throw new IllegalStateException(
                    "활성화된 타로 카드는 정확히 78장이어야 합니다. 현재 "
                            + cards.size()
                            + "장입니다."
            );
        }

        Collections.shuffle(
                cards,
                secureRandom
        );

        String encodedDeck =
                encodeDeckOrder(cards);

        TarotSession session =
                TarotSession.create(
                        spread,
                        request.question(),
                        request.reversedEnabled(),
                        encodedDeck,
                        cards.size()
                );

        TarotSession savedSession =
                tarotSessionRepository.save(session);

        return new TarotResponse.SessionStartResponse(
                savedSession.getSessionKey(),
                savedSession.getStatus().name(),
                spread.getId(),
                spread.getName(),
                resolveLayoutCode(spread),
                spread.getCardCount(),
                savedSession.getDeckSize(),
                CARD_BACK_IMAGE_URL,
                createAllSlotIndexes(
                        savedSession.getDeckSize()
                ),
                positions.stream()
                        .map(this::toPositionResponse)
                        .toList(),
                savedSession.getExpiresAt()
        );
    }

    @Transactional
    public TarotResponse.DrawResponse draw(
            String sessionKey,
            TarotDrawRequest request
    ) {
        TarotSession session =
                getLockedSession(sessionKey);

        validateSessionForDraw(session);

        int slotIndex =
                request.slotIndex();

        if (
                slotIndex < 0
                        || slotIndex >= session.getDeckSize()
        ) {
            throw new IllegalArgumentException(
                    "선택한 카드 위치가 덱 범위를 벗어났습니다."
            );
        }

        if (
                tarotSessionDrawRepository
                        .existsByTarotSessionIdAndSlotIndex(
                                session.getId(),
                                slotIndex
                        )
        ) {
            throw new IllegalStateException(
                    "이미 선택한 카드입니다."
            );
        }

        long selectedCount =
                tarotSessionDrawRepository
                        .countByTarotSessionId(
                                session.getId()
                        );

        int requiredCount =
                session.getSpread().getCardCount();

        if (selectedCount >= requiredCount) {
            throw new IllegalStateException(
                    "필요한 카드를 모두 선택했습니다."
            );
        }

        List<TarotSpreadPosition> positions =
                tarotSpreadPositionRepository
                        .findBySpreadIdOrderByPositionOrderAsc(
                                session.getSpread().getId()
                        );

        int drawOrder =
                Math.toIntExact(selectedCount) + 1;

        TarotSpreadPosition position =
                positions.get(drawOrder - 1);

        List<Integer> deckOrder =
                decodeDeckOrder(
                        session.getDeckOrder()
                );

        Integer selectedCardId =
                deckOrder.get(slotIndex);

        TarotCard selectedCard =
                tarotCardRepository
                        .findById(selectedCardId)
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "셔플된 덱의 카드 정보를 찾을 수 없습니다."
                                )
                        );

        boolean reversed =
                session.isAllowReversed()
                        && secureRandom.nextBoolean();

        TarotSessionDraw draw =
                TarotSessionDraw.create(
                        session,
                        position,
                        selectedCard,
                        slotIndex,
                        reversed,
                        drawOrder
                );

        tarotSessionDrawRepository.saveAndFlush(draw);

        session.startDrawing();

        List<TarotSessionDraw> savedDraws =
                tarotSessionDrawRepository
                        .findByTarotSessionIdOrderByDrawOrderAsc(
                                session.getId()
                        );

        Set<Integer> selectedSlots =
                savedDraws.stream()
                        .map(TarotSessionDraw::getSlotIndex)
                        .collect(Collectors.toSet());

        List<Integer> remainingSlots =
                createAllSlotIndexes(session.getDeckSize())
                        .stream()
                        .filter(
                                index -> !selectedSlots.contains(index)
                        )
                        .toList();

        boolean readyToReveal =
                savedDraws.size() == requiredCount;

        return new TarotResponse.DrawResponse(
                session.getSessionKey(),
                session.getStatus().name(),
                drawOrder,
                position.getId(),
                position.getPositionName(),
                slotIndex,
                savedDraws.size(),
                requiredCount,
                remainingSlots.size(),
                remainingSlots,
                readyToReveal
        );
    }

    @Transactional
    public TarotResponse.RevealResponse reveal(
            String sessionKey
    ) {
        TarotSession session =
                getLockedSession(sessionKey);

        if (
                session.isExpired()
                        && !session.isCompleted()
        ) {
            session.expire();

            throw new IllegalStateException(
                    "타로 선택 시간이 만료되었습니다. 카드를 다시 섞어주세요."
            );
        }

        List<TarotSessionDraw> draws =
                tarotSessionDrawRepository
                        .findByTarotSessionIdOrderByDrawOrderAsc(
                                session.getId()
                        );

        int requiredCount =
                session.getSpread().getCardCount();

        if (draws.size() != requiredCount) {
            throw new IllegalStateException(
                    "카드를 "
                            + requiredCount
                            + "장 모두 선택한 뒤 공개할 수 있습니다."
            );
        }

        if (!session.isCompleted()) {
            session.complete();
        }

        List<TarotResponse.RevealedCardResponse> cards =
                draws.stream()
                        .map(this::toRevealedCardResponse)
                        .toList();

        return new TarotResponse.RevealResponse(
                session.getSessionKey(),
                session.getStatus().name(),
                session.getSpread().getId(),
                session.getSpread().getName(),
                resolveLayoutCode(
                        session.getSpread()
                ),
                session.getQuestion(),
                cards,
                session.getCompletedAt()
        );
    }

    private TarotSession getLockedSession(
            String sessionKey
    ) {
        if (
                sessionKey == null
                        || sessionKey.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "타로 세션 번호가 없습니다."
            );
        }

        return tarotSessionRepository
                .findLockedBySessionKey(
                        sessionKey
                )
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "타로 선택 세션을 찾을 수 없습니다."
                        )
                );
    }

    private void validateSessionForDraw(
            TarotSession session
    ) {
        if (session.isExpired()) {
            session.expire();

            throw new IllegalStateException(
                    "타로 선택 시간이 만료되었습니다. 카드를 다시 섞어주세요."
            );
        }

        if (session.isCompleted()) {
            throw new IllegalStateException(
                    "이미 완료된 타로 선택입니다."
            );
        }

        if (
                session.getStatus()
                        == TarotSessionStatus.EXPIRED
        ) {
            throw new IllegalStateException(
                    "만료된 타로 선택입니다."
            );
        }
    }

    private TarotResponse.SpreadResponse toSpreadResponse(
            TarotSpread spread
    ) {
        List<TarotResponse.PositionResponse> positions =
                tarotSpreadPositionRepository
                        .findBySpreadIdOrderByPositionOrderAsc(
                                spread.getId()
                        )
                        .stream()
                        .map(this::toPositionResponse)
                        .toList();

        return new TarotResponse.SpreadResponse(
                spread.getId(),
                spread.getName(),
                spread.getCardCount(),
                spread.getDescription(),
                resolveLayoutCode(spread),
                positions
        );
    }

    private TarotResponse.PositionResponse toPositionResponse(
            TarotSpreadPosition position
    ) {
        return new TarotResponse.PositionResponse(
                position.getId(),
                position.getPositionOrder(),
                position.getPositionName(),
                position.getDescription()
        );
    }

    private TarotResponse.RevealedCardResponse
    toRevealedCardResponse(
            TarotSessionDraw draw
    ) {
        TarotCard card =
                draw.getCard();

        TarotSpreadPosition position =
                draw.getPosition();

        String direction =
                draw.isReversed()
                        ? "REVERSED"
                        : "UPRIGHT";

        String meaning =
                draw.isReversed()
                        ? card.getReverseMeaning()
                        : card.getUprightMeaning();

        return new TarotResponse.RevealedCardResponse(
                draw.getDrawOrder(),
                position.getId(),
                position.getPositionName(),
                position.getDescription(),
                card.getId(),
                card.getCode(),
                card.getNameKo(),
                card.getNameEn(),
                card.getMajorMinor(),
                card.getSuit(),
                card.getCardNumber(),
                card.getImageUrl(),
                draw.isReversed(),
                direction,
                meaning
        );
    }

    private String resolveLayoutCode(
            TarotSpread spread
    ) {
        if (
                spread.getLayoutCode() != null
                        && !spread.getLayoutCode().isBlank()
        ) {
            return spread.getLayoutCode();
        }

        return switch (spread.getCardCount()) {
            case 1 -> "SINGLE";
            case 3 -> "TIMELINE_3";
            case 5 -> "RELATIONSHIP_5";
            case 6 -> "CHOICE_6";
            case 10 -> "CELTIC_10";
            default -> "AUTO";
        };
    }

    private String encodeDeckOrder(
            List<TarotCard> cards
    ) {
        return cards.stream()
                .map(TarotCard::getId)
                .map(String::valueOf)
                .collect(
                        Collectors.joining(",")
                );
    }

    private List<Integer> decodeDeckOrder(
            String deckOrder
    ) {
        if (
                deckOrder == null
                        || deckOrder.isBlank()
        ) {
            throw new IllegalStateException(
                    "셔플된 타로 덱 정보가 없습니다."
            );
        }

        try {
            return Arrays.stream(
                            deckOrder.split(",")
                    )
                    .map(String::trim)
                    .map(Integer::valueOf)
                    .toList();

        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    "셔플된 타로 덱 정보가 올바르지 않습니다.",
                    exception
            );
        }
    }

    private List<Integer> createAllSlotIndexes(
            int deckSize
    ) {
        return IntStream
                .range(0, deckSize)
                .boxed()
                .toList();
    }
}