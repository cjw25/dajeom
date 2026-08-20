package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 20차 / 21차 Range
 * +
 * Personality Direct-Natal Prompt 확장.
 *
 * 하나의 ConsultationCategory를
 * 외부 LLM에 전달하기 위한
 * 최종 deterministic Prompt Section.
 *
 * =================================================
 *
 * 기존:
 *
 * - 상담 분야
 * - 제목
 * - 사용 가능 여부
 * - Category Constraint
 * - 원국 Evidence
 * - 현재 Timeline Scenario
 * - 과거/미래 Timeline Range
 *
 * =================================================
 *
 * PERSONALITY 추가:
 *
 * personalityStatementContract
 *
 * =================================================
 *
 * PERSONALITY에서는:
 *
 * personalityStatementContract
 * =
 * 공식 기본성향 의미 Source
 *
 * natalEvidences
 * =
 * legacy 보조 검증 Source
 *
 * =================================================
 *
 * 매우 중요한 원칙:
 *
 * Personality Contract와
 * legacy natalEvidences를
 * 합산하거나 다수결하지 않는다.
 *
 * 둘이 의미상 충돌할 경우
 * Personality Contract가
 * 기본성향 표현 범위를 결정한다.
 *
 * =================================================
 *
 * 여기서는:
 *
 * - 새로운 사주 계산
 * - Evidence 재판정
 * - Grounding 승격
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
        timelineYears,

        Optional<NatalPersonalityStatementContract>
        personalityStatementContract

) {

    /*
     * =================================================
     * 기존 20차 호출 호환 Constructor
     * =================================================
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
                List.of(),
                Optional.empty()
        );
    }

    /*
     * =================================================
     * 기존 21차 호출 호환 Constructor
     * =================================================
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
                    timelineScenarios,

            List<ConsultationGroundingTimelineYear>
                    timelineYears

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
                timelineYears,
                Optional.empty()
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

        personalityStatementContract =
                normalizePersonalityContract(
                        personalityStatementContract
                );

        /*
         * =================================================
         * Personality Category 정합성
         * =================================================
         */

        if (
                personalityStatementContract.isPresent()

                        && category
                        != ConsultationCategory.PERSONALITY
        ) {

            throw new IllegalArgumentException(
                    "Personality Statement Contract는 "
                            + "PERSONALITY Prompt Section에만 "
                            + "사용할 수 있습니다: "
                            + category
            );
        }

        boolean personalityOutputExists =
                personalityStatementContract
                        .map(
                                contract ->
                                        contract
                                                .bundleContracts()
                                                .stream()
                                                .anyMatch(
                                                        bundleContract ->
                                                                !bundleContract
                                                                        .mandatorySelections()
                                                                        .isEmpty()

                                                                        || !bundleContract
                                                                        .supportingSelections()
                                                                        .isEmpty()
                                                )
                        )
                        .orElse(
                                false
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
         * unavailable에는 기존 Evidence 금지.
         *
         * Personality Contract 자체는
         * 내부 Reference/Audit 보존을 위해
         * 존재할 수 있다.
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
                    "사용 불가 Prompt Section에는 "
                            + "기존 Evidence를 함께 넣을 수 없습니다: "
                            + category
            );
        }

        /*
         * =================================================
         * 출력 가능한 Personality Contract가 있는데
         * unavailable인 상태 금지.
         * =================================================
         */

        if (
                !available
                        && personalityOutputExists
        ) {

            throw new IllegalArgumentException(
                    "출력 가능한 Personality Statement Contract가 존재하는데 "
                            + "Prompt Section이 unavailable입니다."
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
     * Personality
     * =================================================
     */

    public boolean hasPersonalityStatementContract() {

        return personalityStatementContract
                .isPresent();
    }

    public NatalPersonalityStatementContract
    requirePersonalityStatementContract() {

        return personalityStatementContract
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Prompt Section에 Personality Statement Contract가 없습니다."
                                )
                );
    }

    /**
     * PERSONALITY에서
     * 새 direct-natal Contract가
     * 공식 의미 Source인지.
     */
    public boolean personalityDirectNatalPrimary() {

        return category
                == ConsultationCategory.PERSONALITY

                && personalityStatementContract
                .isPresent();
    }

    /**
     * Contract에서 실제 사용자 설명 가능한
     * Bundle이 존재하는지.
     */
    public boolean hasPersonalityOutputContract() {

        return personalityStatementContract
                .map(
                        contract ->
                                contract
                                        .bundleContracts()
                                        .stream()
                                        .anyMatch(
                                                bundleContract ->
                                                        !bundleContract
                                                                .mandatorySelections()
                                                                .isEmpty()

                                                                || !bundleContract
                                                                .supportingSelections()
                                                                .isEmpty()
                                        )
                )
                .orElse(
                        false
                );
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
     * 기존 Evidence Count
     * =================================================
     *
     * Personality Contract를
     * 가짜 Evidence 개수로 환산하지 않는다.
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

    /**
     * 기존 Grounding 또는
     * Personality Contract 중
     * 하나라도 존재하는가.
     */
    public boolean hasAnyGroundingInput() {

        return hasAnyEvidence()
                || hasPersonalityStatementContract();
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
     * Personality Contract Normalize
     * =================================================
     */

    private static Optional<NatalPersonalityStatementContract>
    normalizePersonalityContract(

            Optional<NatalPersonalityStatementContract> source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                Objects.requireNonNull(
                        source.orElseThrow(),
                        "Prompt Section의 Personality Statement Contract는 null일 수 없습니다."
                )
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