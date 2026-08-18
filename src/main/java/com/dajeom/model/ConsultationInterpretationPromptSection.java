package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 20차 / 21차 Range 확장.
 *
 * 하나의 ConsultationCategory를
 * LLM에 전달하기 위한 최종 Prompt Section.
 *
 * =================================================
 *
 * 포함:
 *
 * - 상담 분야
 * - 사용자용 제목
 * - 사용 가능 여부
 * - 사용 불가 사유
 * - Category별 Constraint
 * - 원국 Evidence
 * - 현재 Timeline Scenario
 * - 과거/미래 Timeline Year Range
 *
 * =================================================
 *
 * 여기에는 3~17차 원본 계산 객체를
 * 직접 포함하지 않는다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 이 객체는 이미 deterministic 계산이 끝난
 * Grounding만 전달한다.
 *
 * 여기서:
 *
 * - 새로운 사주 계산
 * - Evidence 재판정
 * - 대표 polarity 선택
 * - 점수 계산
 * - 사건 예측
 *
 * 을 하지 않는다.
 */
public record ConsultationInterpretationPromptSection(

        int order,

        ConsultationCategory category,

        String title,

        boolean available,

        Optional<String> unavailableReason,

        List<String> constraints,

        List<ConsultationGroundingEvidence>
        natalEvidences,

        List<ConsultationGroundingTimelineScenario>
        timelineScenarios,

        List<ConsultationGroundingTimelineYear>
        timelineYears

) {

    /*
     * =================================================
     * 기존 20차 호출 호환 Constructor
     * =================================================
     *
     * 기존 코드 중 아직 timelineYears를 전달하지 않는
     * 호출부가 있어도 컴파일이 깨지지 않게 유지한다.
     */

    public ConsultationInterpretationPromptSection(

            int order,

            ConsultationCategory category,

            String title,

            boolean available,

            Optional<String> unavailableReason,

            List<String> constraints,

            List<ConsultationGroundingEvidence>
                    natalEvidences,

            List<ConsultationGroundingTimelineScenario>
                    timelineScenarios

    ) {

        this(
                order,
                category,
                title,
                available,
                unavailableReason,
                constraints,
                natalEvidences,
                timelineScenarios,
                List.of()
        );
    }

    /*
     * =================================================
     * Canonical Constructor
     * =================================================
     */

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
                normalizeNatalEvidences(
                        natalEvidences
                );

        timelineScenarios =
                normalizeTimelineScenarios(
                        timelineScenarios
                );

        timelineYears =
                normalizeTimelineYears(
                        timelineYears
                );

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
         * =================================================
         * unavailable에는 Evidence 금지
         * =================================================
         */

        if (
                !available
                        && (
                        !natalEvidences.isEmpty()
                                || !timelineScenarios.isEmpty()
                                || !timelineYears.isEmpty()
                )
        ) {

            throw new IllegalArgumentException(
                    "사용 불가 Prompt Section에는 Evidence를 함께 넣을 수 없습니다: "
                            + category
            );
        }

        /*
         * =================================================
         * Constraint
         * =================================================
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

    /*
     * =================================================
     * Availability
     * =================================================
     */

    public boolean unavailable() {

        return !available;
    }

    /*
     * =================================================
     * Natal
     * =================================================
     */

    public boolean hasNatalEvidence() {

        return !natalEvidences
                .isEmpty();
    }

    /*
     * =================================================
     * Current Timeline
     * =================================================
     */

    public boolean hasTimelineScenarios() {

        return !timelineScenarios
                .isEmpty();
    }

    /*
     * =================================================
     * Timeline Range
     * =================================================
     */

    public boolean hasTimelineYears() {

        return !timelineYears
                .isEmpty();
    }

    public int timelineYearCount() {

        return timelineYears
                .size();
    }

    public boolean hasAnyTimeline() {

        return hasTimelineScenarios()
                || hasTimelineYears();
    }

    /*
     * =================================================
     * Timeline Uncertainty
     * =================================================
     */

    public boolean timelineUncertaintyExists() {

        boolean scenarioUncertainty =
                timelineScenarios
                        .stream()
                        .anyMatch(
                                ConsultationGroundingTimelineScenario
                                        ::timelineUncertaintyExists
                        );

        if (
                scenarioUncertainty
        ) {

            return true;
        }

        return timelineYears
                .stream()
                .anyMatch(
                        ConsultationGroundingTimelineYear
                                ::timelineUncertaintyExists
                );
    }

    /*
     * =================================================
     * Evidence Count
     * =================================================
     */

    public int evidenceCount() {

        int result =
                natalEvidences
                        .size();

        for (
                ConsultationGroundingTimelineScenario scenario
                : timelineScenarios
        ) {

            result +=
                    scenario
                            .evidenceCount();
        }

        for (
                ConsultationGroundingTimelineYear timelineYear
                : timelineYears
        ) {

            result +=
                    timelineYear
                            .evidenceCount();
        }

        return result;
    }

    public boolean hasAnyEvidence() {

        return evidenceCount() > 0;
    }

    /*
     * =================================================
     * Natal Normalize
     * =================================================
     */

    private static List<ConsultationGroundingEvidence>
    normalizeNatalEvidences(

            List<ConsultationGroundingEvidence> source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<ConsultationGroundingEvidence> result =
                new ArrayList<>(
                        source.size()
                );

        for (
                ConsultationGroundingEvidence evidence
                : source
        ) {

            result.add(
                    Objects.requireNonNull(
                            evidence,
                            "Prompt Section의 Natal Evidence는 null일 수 없습니다."
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Current Timeline Normalize
     * =================================================
     */

    private static List<ConsultationGroundingTimelineScenario>
    normalizeTimelineScenarios(

            List<ConsultationGroundingTimelineScenario>
                    source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<ConsultationGroundingTimelineScenario> result =
                new ArrayList<>(
                        source.size()
                );

        for (
                ConsultationGroundingTimelineScenario scenario
                : source
        ) {

            result.add(
                    Objects.requireNonNull(
                            scenario,
                            "Prompt Section의 Timeline Scenario는 null일 수 없습니다."
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Timeline Range Normalize
     * =================================================
     */

    private static List<ConsultationGroundingTimelineYear>
    normalizeTimelineYears(

            List<ConsultationGroundingTimelineYear> source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<ConsultationGroundingTimelineYear> result =
                new ArrayList<>(
                        source.size()
                );

        for (
                ConsultationGroundingTimelineYear timelineYear
                : source
        ) {

            result.add(
                    Objects.requireNonNull(
                            timelineYear,
                            "Prompt Section의 Timeline Year는 null일 수 없습니다."
                    )
            );
        }

        result.sort(
                java.util.Comparator
                        .comparingInt(
                                ConsultationGroundingTimelineYear
                                        ::yearOrder
                        )
        );

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Text
     * =================================================
     */

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

    /*
     * =================================================
     * Optional Text
     * =================================================
     */

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

            throw new IllegalArgumentException(
                    "Prompt Section unavailableReason은 빈 문자열일 수 없습니다."
            );
        }

        return Optional.of(
                value
        );
    }

    /*
     * =================================================
     * Constraint Text
     * =================================================
     */

    private static List<String>
    normalizeTexts(

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