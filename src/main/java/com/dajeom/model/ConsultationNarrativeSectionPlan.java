package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 하나의 ConsultationCategory를
 * 사용자에게 어떤 순서와 깊이로 설명할지
 * 결정한 Narrative Section 설계도.
 *
 * =================================================
 *
 * category
 *
 * 어떤 상담 Category인가.
 *
 * =================================================
 *
 * title
 *
 * 일반 사용자에게 보여줄 Section 제목.
 *
 * =================================================
 *
 * categoryOrder
 *
 * ConsultationCategory 자체의
 * 공식 기본 순서.
 *
 * =================================================
 *
 * focusRank
 *
 * User Context를 반영한
 * 실제 표시 우선순위.
 *
 * 명리 결과 자체를 변경하지 않는다.
 *
 * =================================================
 *
 * questionFocused
 *
 * 현재 사용자 질문 또는 Context와
 * 직접 관련되어 먼저 보여줄 Section인지.
 *
 * =================================================
 *
 * packets
 *
 * 이 Category 안에서 AI에게 전달할
 * 독립 Meaning Packet 목록.
 *
 * 서로 다른 Packet을
 * AI가 임의로 인과관계로 연결해서는 안 된다.
 */
public record ConsultationNarrativeSectionPlan(

        ConsultationCategory category,

        String title,

        int categoryOrder,

        int focusRank,

        boolean questionFocused,

        List<ConsultationMeaningPacket> packets

) {

    public ConsultationNarrativeSectionPlan {

        Objects.requireNonNull(
                category,
                "Narrative Section Category는 필수입니다."
        );

        title =
                requireText(
                        title,
                        "Narrative Section title은 필수입니다."
                );

        if (
                categoryOrder
                        != category.getOrder()
        ) {

            throw new IllegalArgumentException(
                    "Narrative Section categoryOrder가 "
                            + "ConsultationCategory order와 일치하지 않습니다. "
                            + "category="
                            + category
                            + ", expected="
                            + category.getOrder()
                            + ", actual="
                            + categoryOrder
            );
        }

        if (
                focusRank < 1
        ) {

            throw new IllegalArgumentException(
                    "Narrative Section focusRank는 1 이상이어야 합니다."
            );
        }

        packets =
                packets == null
                        ? List.of()
                        : packets
                        .stream()
                        .map(
                                packet ->
                                        Objects.requireNonNull(
                                                packet,
                                                "Narrative Packet은 null일 수 없습니다."
                                        )
                        )
                        .peek(
                                packet -> {

                                    if (
                                            packet.category()
                                                    != category
                                    ) {

                                        throw new IllegalArgumentException(
                                                "Narrative Packet Category가 "
                                                        + "Section Category와 일치하지 않습니다. "
                                                        + "section="
                                                        + category
                                                        + ", packet="
                                                        + packet.category()
                                        );
                                    }
                                }
                        )
                        .sorted(
                                Comparator
                                        .comparingInt(
                                                (
                                                        ConsultationMeaningPacket
                                                                packet
                                                ) ->
                                                        packet
                                                                .priority()
                                                                .getOrder()
                                        )
                                        .thenComparing(
                                                ConsultationMeaningPacket
                                                        ::packetId
                                        )
                        )
                        .toList();
    }

    /**
     * AI에 전달할 Packet이
     * 하나 이상 존재하는가.
     */
    public boolean hasPackets() {

        return !packets
                .isEmpty();
    }

    /**
     * Section 안의 전체 Claim 수.
     */
    public int claimCount() {

        return packets
                .stream()
                .mapToInt(
                        packet ->
                                packet
                                        .claims()
                                        .size()
                )
                .sum();
    }

    private static String requireText(
            String value,
            String message
    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }
}