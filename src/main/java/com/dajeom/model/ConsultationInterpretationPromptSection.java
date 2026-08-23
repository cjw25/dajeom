package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 20차 / 21차 Range
 * +
 * Personality Direct-Natal Prompt 확장
 * +
 * Category NATAL Deterministic Prompt 확장.
 *
 * 하나의 ConsultationCategory를
 * 외부 LLM에 전달하기 위한
 * 최종 deterministic Prompt Section.
 *
 * =================================================
 * 공식 생성 계약
 * =================================================
 *
 * Prompt Section은 canonical constructor로
 * 현재 구조 전체를 명시해서 생성한다.
 *
 * 다음 필드를 생략하는
 * 과거 호환 생성 경로는 제공하지 않는다.
 *
 * - natalDeterministicConclusions
 * - timelineYears
 * - personalityStatementContract
 *
 * =================================================
 * natalEvidences
 * =================================================
 *
 * LifeDomain 기반
 * 원국 Grounding Evidence.
 *
 * 이것은:
 *
 * - 계산 근거
 * - provenance
 * - LifeDomain 상태
 *
 * 를 보존한다.
 *
 * =================================================
 * natalDeterministicConclusions
 * =================================================
 *
 * ConsultationCategoryDeterministicRuleEngine이
 * 해당 Category의 NATAL Layer에서
 * Java로 확정한 공식 Meaning Contract.
 *
 * raw natalEvidences와
 * 서로 다른 구조로 유지한다.
 *
 * =================================================
 * Current Timeline
 * =================================================
 *
 * timelineScenarios 안에는:
 *
 * - raw evidences
 * - CURRENT_TIMELINE deterministicConclusions
 *
 * 가 이미 함께 전달된다.
 *
 * 단 두 List는 Scenario 내부에서도
 * 서로 독립적으로 유지된다.
 *
 * =================================================
 * PAST_LUCK / FUTURE_LUCK
 * =================================================
 *
 * timelineYears 안에는:
 *
 * ConsultationGroundingTimelineYear
 * .deterministicContract
 *
 * 가 이미 포함되어 있다.
 *
 * 연도별 raw Evidence와
 * deterministicContract를 합치지 않는다.
 *
 * =================================================
 * PERSONALITY
 * =================================================
 *
 * personalityStatementContract
 *
 * =
 *
 * PERSONALITY 전용 공식
 * Direct-Natal 의미 Source.
 *
 * =================================================
 * PERSONALITY에서는
 * =================================================
 *
 * personalityStatementContract
 *
 * =
 * 공식 기본성향 의미 Source
 *
 * natalEvidences
 *
 * =
 * 세부 계산 근거와 provenance 설명 자료
 *
 * =================================================
 * 중요한 우선순위
 * =================================================
 *
 * 일반 Category:
 *
 * natalDeterministicConclusions
 *
 * =
 * Java 확정 NATAL Meaning Boundary
 *
 * natalEvidences
 *
 * =
 * 세부 계산 근거
 *
 * -------------------------------------------------
 *
 * PERSONALITY:
 *
 * personalityStatementContract
 *
 * =
 * 공식 Direct-Natal Meaning Boundary
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * natalEvidences가 여러 개 있다는 이유로:
 *
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 같이 있으므로 C다
 * - A 이후 B가 나타난다
 * - 여러 Evidence가 같은 방향이라 더 강하다
 *
 * 같은 의미를 Prompt 모델에서
 * 새로 만들지 않는다.
 *
 * 관계·인과·결합·순서 의미가 필요하면
 * 반드시 upstream Java 계산 결과로
 * 이미 존재해야 한다.
 *
 * =================================================
 * Status 합성 금지
 * =================================================
 *
 * 예:
 *
 * natalEvidence A = SUPPORTIVE
 *
 * natalEvidence B = PRESSURE
 *
 * 라고 해서:
 *
 * Category = MIXED
 *
 * 로 계산하지 않는다.
 *
 * Java Category Rule이 실제로
 * MIXED Conclusion을 계산한 경우에만
 * natalDeterministicConclusions에 존재한다.
 *
 * =================================================
 * CURRENT_YEAR_LUCK
 * =================================================
 *
 * CURRENT_YEAR_LUCK에
 * 여러 Domain의 시간운 결과가 존재해도:
 *
 * - 전체 SUPPORTIVE
 * - 전체 PRESSURE
 * - 전체 MIXED
 *
 * 같은 새로운 종합 Status를
 * 이 Prompt 모델에서 계산하지 않는다.
 *
 * 각 시간운 Category Deterministic Conclusion은
 * Timeline Scenario 내부에
 * 독립적으로 보존된다.
 *
 * =================================================
 * 매우 중요한 원칙
 * =================================================
 *
 * Personality Contract와
 * natalEvidences를
 * 합산하거나 다수결하지 않는다.
 *
 * 둘이 의미상 충돌할 경우
 * Personality Contract가
 * 기본성향 표현 범위를 결정한다.
 *
 * 일반 Category에서도:
 *
 * natalDeterministicConclusions와
 * natalEvidences를 합산하지 않는다.
 *
 * =================================================
 * 여기서는 하지 않는 것
 * =================================================
 *
 * - 새로운 사주 계산
 * - Evidence 재판정
 * - Grounding 승격
 * - Category Status 재계산
 * - Evidence 다수결
 * - Conclusion 다수결
 * - NATAL + Timeline 합성
 * - Cross-Domain 자동 합성
 * - Cross-Temporal 자동 합성
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 사건 예측
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

        List<ConsultationGroundingDeterministicConclusion>
        natalDeterministicConclusions,

        List<ConsultationGroundingTimelineScenario>
        timelineScenarios,

        List<ConsultationGroundingTimelineYear>
        timelineYears,

        Optional<NatalPersonalityStatementContract>
        personalityStatementContract

) {

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

        natalDeterministicConclusions =
                normalizeNatalDeterministicConclusions(
                        natalDeterministicConclusions
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
         * unavailable에는 Grounding 입력 금지
         * =================================================
         *
         * 다음은 unavailable Section에
         * 함께 둘 수 없다.
         *
         * - natalEvidences
         * - natalDeterministicConclusions
         * - timelineScenarios
         * - timelineYears
         *
         * Personality Contract 자체는
         * 내부 Reference/Audit 보존을 위해
         * 존재할 수 있다.
         */

        if (
                !available
                        && (
                        !natalEvidences.isEmpty()
                                || !natalDeterministicConclusions.isEmpty()
                                || !timelineScenarios.isEmpty()
                                || !timelineYears.isEmpty()
                )
        ) {

            throw new IllegalArgumentException(
                    "사용 불가 Prompt Section에는 "
                            + "Evidence 또는 Category Deterministic Conclusion을 "
                            + "함께 넣을 수 없습니다: "
                            + category
            );
        }

        /*
         * =================================================
         * 출력 가능한 Personality Contract가 있는데
         * unavailable인 상태 금지
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
     * Natal Raw Evidence
     * =================================================
     */

    public boolean hasNatalEvidence() {

        return !natalEvidences
                .isEmpty();
    }


    public int natalEvidenceCount() {

        return natalEvidences
                .size();
    }


    /*
     * =================================================
     * Natal Deterministic Conclusion
     * =================================================
     */

    public boolean hasNatalDeterministicConclusions() {

        return !natalDeterministicConclusions
                .isEmpty();
    }


    public int natalDeterministicConclusionCount() {

        return natalDeterministicConclusions
                .size();
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
     * direct-natal Contract가
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


    public int timelineScenarioCount() {

        return timelineScenarios
                .size();
    }


    /*
     * =================================================
     * Current Timeline Deterministic Conclusion
     * =================================================
     *
     * Scenario별 Conclusion 존재 여부만 조회한다.
     *
     * 여기서 서로 합치지 않는다.
     */

    public boolean hasTimelineDeterministicConclusions() {

        return timelineScenarios
                .stream()
                .anyMatch(
                        ConsultationGroundingTimelineScenario
                                ::hasDeterministicConclusions
                );
    }


    public int timelineDeterministicConclusionCount() {

        return timelineScenarios
                .stream()
                .mapToInt(
                        ConsultationGroundingTimelineScenario
                                ::deterministicConclusionCount
                )
                .sum();
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
     * Raw Evidence Count
     * =================================================
     *
     * 다음을 가짜 Evidence 개수로
     * 환산하지 않는다.
     *
     * - natalDeterministicConclusions
     * - Scenario deterministicConclusions
     * - Timeline Year deterministicContract
     * - Personality Contract
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
     * Category Deterministic Grounding
     * =================================================
     *
     * NATAL과 CURRENT_TIMELINE에
     * Category Deterministic Conclusion이
     * 존재하는지 확인한다.
     *
     * 두 Layer를 하나의 의미로
     * 합성하지 않는다.
     *
     * PAST/FUTURE Timeline Year Contract는
     * 별도 구조이므로 이 Helper에 포함하지 않는다.
     */

    public boolean hasAnyCategoryDeterministicConclusion() {

        return hasNatalDeterministicConclusions()
                || hasTimelineDeterministicConclusions();
    }


    public int categoryDeterministicConclusionCount() {

        return natalDeterministicConclusionCount()
                + timelineDeterministicConclusionCount();
    }


    /*
     * =================================================
     * 전체 Grounding Input
     * =================================================
     *
     * 존재 여부 확인용이다.
     *
     * 서로 다른 Grounding Source를
     * 의미적으로 합친다는 뜻이 아니다.
     */

    public boolean hasAnyGroundingInput() {

        return hasAnyEvidence()
                || hasAnyCategoryDeterministicConclusion()
                || hasPersonalityStatementContract();
    }


    /*
     * =================================================
     * Natal Raw Evidence Normalize
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
     * Natal Deterministic Conclusion Normalize
     * =================================================
     *
     * 이미 Java Category Rule이 계산한
     * Conclusion을 그대로 보존한다.
     *
     * 여기서는:
     *
     * - Status 비교
     * - Conclusion 병합
     * - 대표 Conclusion 선택
     * - 동일 방향 강화
     * - 반대 방향 MIXED 생성
     * - 관계 생성
     * - 인과 생성
     *
     * 을 하지 않는다.
     */

    private static List<
            ConsultationGroundingDeterministicConclusion
            >
    normalizeNatalDeterministicConclusions(

            List<
                    ConsultationGroundingDeterministicConclusion
                    > source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<ConsultationGroundingDeterministicConclusion>
                result =
                new ArrayList<>(
                        source.size()
                );

        for (
                ConsultationGroundingDeterministicConclusion
                        conclusion
                : source
        ) {

            result.add(
                    Objects.requireNonNull(
                            conclusion,
                            "Prompt Section의 Natal Deterministic Conclusion은 null일 수 없습니다."
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