package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 20차.
 *
 * 하나의 ConsultationCategory를
 * LLM에 전달하기 위한 최종 Prompt Section.
 *
 * =================================================
 *
 * 이 객체는 이미 Grounding 단계에서
 * 정리된 Evidence만 포함한다.
 *
 * 3~17차의 원본 계산 객체는 포함하지 않는다.
 *
 * =================================================
 *
 * 포함:
 *
 * - 상담 분야
 * - 사용자용 제목
 * - 사용 가능 여부
 * - 사용 불가 사유
 * - Category별 Grounding 제약
 * - 원국 Evidence
 * - Timeline Scenario
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 사주 계산
 * - Evidence 재판정
 * - 대표 polarity 결정
 * - 점수화
 * - 사건 예측
 * - 사용자 상담문 생성
 */
public record ConsultationInterpretationPromptSection(

        int order,

        ConsultationCategory category,

        String title,

        boolean available,

        Optional<String> unavailableReason,

        List<String> constraints,

        List<ConsultationGroundingEvidence> natalEvidences,

        List<ConsultationGroundingTimelineScenario>
        timelineScenarios

) {

    public ConsultationInterpretationPromptSection {

        if (
                order < 1
        ) {

            throw new IllegalArgumentException(
                    "Prompt Section 순서는 1 이상이어야 합니다."
            );
        }

        Objects.requireNonNull(
                category,
                "Prompt Section의 ConsultationCategory는 필수입니다."
        );

        title =
                requireText(
                        title,
                        "Prompt Section 제목은 필수입니다."
                );

        unavailableReason =
                normalizeOptionalText(
                        unavailableReason
                );

        constraints =
                normalizeTexts(
                        constraints,
                        "Prompt Section Constraint는 비어 있을 수 없습니다."
                );

        natalEvidences =
                natalEvidences == null
                        ? List.of()
                        : List.copyOf(
                        natalEvidences
                );

        for (
                ConsultationGroundingEvidence evidence
                : natalEvidences
        ) {

            Objects.requireNonNull(
                    evidence,
                    "Prompt Section의 Natal Evidence는 null일 수 없습니다."
            );
        }

        timelineScenarios =
                timelineScenarios == null
                        ? List.of()
                        : List.copyOf(
                        timelineScenarios
                );

        for (
                ConsultationGroundingTimelineScenario scenario
                : timelineScenarios
        ) {

            Objects.requireNonNull(
                    scenario,
                    "Prompt Section의 Timeline Scenario는 null일 수 없습니다."
            );
        }

        /*
         * =================================================
         * Availability 정합성
         * =================================================
         */

        if (
                available
                        && unavailableReason.isPresent()
        ) {

            throw new IllegalArgumentException(
                    "사용 가능한 Prompt Section에는 unavailableReason을 넣을 수 없습니다: "
                            + category
            );
        }

        if (
                !available
                        && unavailableReason.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "사용 불가 Prompt Section에는 unavailableReason이 필요합니다: "
                            + category
            );
        }

        /*
         * 사용 불가 View를
         * 가짜 Evidence로 채우지 않는다.
         */

        if (
                !available
                        && (
                        !natalEvidences.isEmpty()
                                || !timelineScenarios.isEmpty()
                )
        ) {

            throw new IllegalArgumentException(
                    "사용 불가 Prompt Section에는 Evidence를 함께 넣을 수 없습니다: "
                            + category
            );
        }

        /*
         * 모든 실제 Section에는
         * 명시적인 Grounding 제약이 있어야 한다.
         */

        if (
                constraints.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Prompt Section에는 최소 하나 이상의 Grounding Constraint가 필요합니다: "
                            + category
            );
        }
    }

    public boolean unavailable() {

        return !available;
    }

    public boolean hasNatalEvidence() {

        return !natalEvidences
                .isEmpty();
    }

    public boolean hasTimelineScenarios() {

        return !timelineScenarios
                .isEmpty();
    }

    public boolean timelineUncertaintyExists() {

        return timelineScenarios
                .stream()
                .anyMatch(
                        ConsultationGroundingTimelineScenario
                                ::timelineUncertaintyExists
                );
    }

    public int evidenceCount() {

        int result =
                natalEvidences.size();

        for (
                ConsultationGroundingTimelineScenario scenario
                : timelineScenarios
        ) {

            result +=
                    scenario
                            .evidenceCount();
        }

        return result;
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

    private static Optional<String>
    normalizeOptionalText(
            Optional<String> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Optional.empty();
        }

        String value =
                source
                        .orElseThrow()
                        .trim();

        if (
                value.isBlank()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                value
        );
    }

    private static List<String> normalizeTexts(

            List<String> source,

            String emptyMessage

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        return source
                .stream()
                .map(
                        value ->
                                requireText(
                                        value,
                                        emptyMessage
                                )
                )
                .distinct()
                .toList();
    }
}