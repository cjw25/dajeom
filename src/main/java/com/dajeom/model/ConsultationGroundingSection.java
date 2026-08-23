package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 20차 / 21차 Range 확장
 * +
 * Personality Direct-Natal Grounding 확장
 * +
 * Category NATAL Deterministic Grounding 확장.
 *
 * 하나의 ConsultationCategory에 대한
 * LLM Grounding Section.
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
 * Java로 확정한:
 *
 * ConsultationCategoryDeterministicMatch
 *
 * 를 LLM Grounding 경계용:
 *
 * ConsultationGroundingDeterministicConclusion
 *
 * 으로 변환한 결과.
 *
 * =================================================
 * 중요한 우선순위
 * =================================================
 *
 * natalDeterministicConclusions
 *
 * =
 * 해당 Category의 공식 NATAL Meaning Contract.
 *
 * -------------------------------------------------
 *
 * natalEvidences
 *
 * =
 * 그 판단의 세부 LifeDomain Grounding.
 *
 * =================================================
 * timelineScenarios
 * =================================================
 *
 * 현재 조회시점의 가능한 Timeline Segment.
 *
 * 각 Scenario는:
 *
 * - raw evidences
 * - CURRENT_TIMELINE deterministicConclusions
 *
 * 를 서로 분리해서 보존한다.
 *
 * =================================================
 * timelineYears
 * =================================================
 *
 * PAST_LUCK / FUTURE_LUCK 전용
 * 실제 Timeline Range의 연도별 Grounding.
 *
 * PAST_LUCK / FUTURE_LUCK의
 * 연도별 Meaning Contract는
 * ConsultationGroundingTimelineYear 내부의
 * deterministicContract가 담당한다.
 *
 * =================================================
 * personalityStatementContract
 * =================================================
 *
 * PERSONALITY 전용.
 *
 * LifeDomain을 우회하여:
 *
 * 원국
 * ↓
 * Functional
 * ↓
 * CrossFunctional
 * ↓
 * ConditionalResponse
 * ↓
 * Personality
 *
 * 로 계산된 직접 원국 Contract.
 *
 * =================================================
 * unavailableReason
 * =================================================
 *
 * deterministic 계산 범위에서
 * 아직 해당 Category를 만들 수 없는 경우.
 *
 * =================================================
 * 공식 생성 계약
 * =================================================
 *
 * 모든 Grounding Section은 canonical constructor로
 * 전체 구조를 명시해서 생성한다.
 *
 * 다음을 생략하는 호환 생성 경로는 제공하지 않는다.
 *
 * - natalDeterministicConclusions
 * - timelineYears
 * - personalityStatementContract
 *
 * 따라서 호출부는 항상 현재 Grounding 구조 전체를
 * 명시적으로 전달해야 한다.
 *
 * =================================================
 * 관계형 풀이 차단
 * =================================================
 *
 * raw Evidence가 여러 개 존재한다는 이유로:
 *
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 있으므로 C다
 * - 여러 Evidence가 같은 방향이므로 더 강하다
 * - 여러 Evidence가 반대 방향이므로 MIXED다
 *
 * 같은 의미를 LLM이 새로 만들 수 있다는
 * 뜻이 아니다.
 *
 * 관계·인과·결합·종합 의미가 필요하면
 * 반드시 upstream Java의
 * Category Deterministic Rule이
 * 명시적으로 계산한 Conclusion으로
 * 존재해야 한다.
 *
 * =================================================
 * NATAL Status 합성 금지
 * =================================================
 *
 * 예:
 *
 * NATAL Evidence A = SUPPORTIVE
 *
 * NATAL Evidence B = PRESSURE
 *
 * 라고 해서:
 *
 * Category NATAL = MIXED
 *
 * 로 만들지 않는다.
 *
 * 별도 Java Rule이 실제로 MIXED를
 * 계산한 경우에만
 * natalDeterministicConclusions에
 * MIXED Conclusion이 존재한다.
 *
 * =================================================
 * Current Timeline Status 합성 금지
 * =================================================
 *
 * Annual = SUPPORTIVE
 *
 * Monthly = PRESSURE
 *
 * 라고 해서:
 *
 * 현재 운 = MIXED
 *
 * 로 이 Section에서 계산하지 않는다.
 *
 * 각 Timeline Scenario의
 * deterministicConclusions를 그대로 보존한다.
 *
 * =================================================
 * Cross-Domain 합성 금지
 * =================================================
 *
 * WEALTH = SUPPORTIVE
 *
 * CAREER = PRESSURE
 *
 * RELATIONSHIP = CONTEXTUAL
 *
 * 라고 해서:
 *
 * 전체적으로 MIXED
 *
 * 같은 결론을 이 모델에서 만들지 않는다.
 *
 * =================================================
 * PERSONALITY 경계
 * =================================================
 *
 * PERSONALITY의 공식 Direct-Natal 의미는
 * NatalPersonalityStatementContract가
 * 담당한다.
 *
 * Personality Contract를
 * 일반 Category Deterministic Conclusion으로
 * 위장하지 않는다.
 *
 * =================================================
 * PAST / FUTURE 경계
 * =================================================
 *
 * PAST_LUCK / FUTURE_LUCK의
 * 연도별 공식 Meaning Contract는
 * ConsultationGroundingTimelineYear의
 * deterministicContract가 담당한다.
 *
 * NATAL Conclusion 또는
 * CURRENT_TIMELINE Scenario Conclusion과
 * 같은 구조로 합치지 않는다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 1.
 * 현재 Segment Scenario와
 * 과거/미래 Range Year를
 * 같은 개념으로 합치지 않는다.
 *
 * 2.
 * PERSONALITY Contract를
 * LifeDomainRuleMatch로 위장하지 않는다.
 *
 * 3.
 * LifeDomain Grounding Evidence는
 * 세부 계산 근거로 보존한다.
 *
 * 4.
 * NATAL Evidence와
 * NATAL Deterministic Conclusion을
 * 별도 구조로 유지한다.
 *
 * 5.
 * raw Evidence 다수결 금지.
 *
 * 6.
 * Category Status 재계산 금지.
 *
 * 7.
 * Cross-Domain 자동 합성 금지.
 *
 * 8.
 * Cross-Temporal 자동 합성 금지.
 *
 * 9.
 * 새로운 관계 생성 금지.
 *
 * 10.
 * 새로운 인과 생성 금지.
 *
 * 11.
 * 새로운 순서 생성 금지.
 */
public record ConsultationGroundingSection(

        ConsultationCategory category,

        List<ConsultationGroundingEvidence>
        natalEvidences,

        List<ConsultationGroundingDeterministicConclusion>
        natalDeterministicConclusions,

        List<ConsultationGroundingTimelineScenario>
        timelineScenarios,

        List<ConsultationGroundingTimelineYear>
        timelineYears,

        Optional<NatalPersonalityStatementContract>
        personalityStatementContract,

        Optional<String> unavailableReason

) {

    /*
     * =================================================
     * Canonical Constructor
     * =================================================
     */

    public ConsultationGroundingSection {

        Objects.requireNonNull(
                category,
                "Grounding Section의 ConsultationCategory는 필수입니다."
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
                normalizePersonalityStatementContract(
                        personalityStatementContract
                );

        unavailableReason =
                normalizeUnavailableReason(
                        unavailableReason
                );

        /*
         * =================================================
         * Personality Contract Category 검증
         * =================================================
         */

        if (
                personalityStatementContract.isPresent()

                        && category
                        != ConsultationCategory.PERSONALITY
        ) {

            throw new IllegalArgumentException(
                    "NatalPersonalityStatementContract는 "
                            + "PERSONALITY Grounding Section에만 "
                            + "연결할 수 있습니다: "
                            + category
            );
        }

        /*
         * =================================================
         * unavailable Section 검증
         * =================================================
         *
         * Personality Contract 자체는
         * 내부 Reference까지 보존할 수 있으므로
         * unavailable Section에도 존재할 수 있다.
         *
         * 단:
         *
         * - Natal raw Evidence
         * - Natal Deterministic Conclusion
         * - Timeline Scenario
         * - Timeline Year
         *
         * 와 unavailableReason을
         * 동시에 두지는 않는다.
         */

        if (
                unavailableReason.isPresent()

                        && (
                        !natalEvidences.isEmpty()
                                || !natalDeterministicConclusions.isEmpty()
                                || !timelineScenarios.isEmpty()
                                || !timelineYears.isEmpty()
                )
        ) {

            throw new IllegalArgumentException(
                    "사용 불가 Grounding Section에는 "
                            + "Evidence 또는 Category Deterministic Conclusion을 "
                            + "함께 넣을 수 없습니다: "
                            + category
            );
        }
    }


    /*
     * =================================================
     * 기본 정보
     * =================================================
     */

    public String title() {

        return category
                .getKoreanName();
    }


    /*
     * =================================================
     * Availability
     * =================================================
     */

    public boolean available() {

        return unavailableReason
                .isEmpty();
    }


    public boolean unavailable() {

        return !available();
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
     * Natal Category Deterministic Conclusion
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
     * Personality Direct-Natal Contract
     * =================================================
     */

    public boolean hasPersonalityStatementContract() {

        return personalityStatementContract
                .isPresent();
    }


    /**
     * 실제 사용자 설명 가능한
     * Personality Bundle Contract가
     * 하나라도 존재하는가.
     *
     * Contract가 존재하더라도
     * weak/internal-only 구조만 있다면
     * false일 수 있다.
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


    public NatalPersonalityStatementContract
    requirePersonalityStatementContract() {

        return personalityStatementContract
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "PERSONALITY Statement Contract가 없습니다."
                                )
                );
    }


    /*
     * =================================================
     * Current Timeline Scenario
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
     * Scenario별 Conclusion을 단순 조회한다.
     *
     * 여기서:
     *
     * - 합산
     * - 다수결
     * - Status 비교
     * - Cross-Scenario 합성
     *
     * 을 하지 않는다.
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
     * Category Deterministic Conclusion
     * =================================================
     *
     * NATAL + CURRENT_TIMELINE에
     * 공식 Category Deterministic Conclusion이
     * 하나라도 존재하는가.
     *
     * 이것은 존재 여부 조회일 뿐
     * 두 Layer를 결합한다는 뜻이 아니다.
     *
     * PAST_LUCK / FUTURE_LUCK의
     * TimelineYear deterministicContract는
     * 별도 구조이므로 여기 포함하지 않는다.
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
     * Timeline Range Year
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


    /*
     * =================================================
     * Timeline
     * =================================================
     */

    public boolean hasAnyTimeline() {

        return hasTimelineScenarios()
                || hasTimelineYears();
    }


    /**
     * 현재 Segment 또는
     * Range Year 중 하나라도
     * Timeline 불확실성을 가지면 true.
     */
    public boolean timelineUncertaintyExists() {

        boolean currentScenarioUncertainty =
                timelineScenarios
                        .stream()
                        .anyMatch(
                                ConsultationGroundingTimelineScenario
                                        ::timelineUncertaintyExists
                        );

        if (
                currentScenarioUncertainty
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
     * 전체 Raw Evidence
     * =================================================
     *
     * 단순 조회용 연결이다.
     *
     * 여기서:
     *
     * - 합산
     * - 대표 Status
     * - 점수화
     *
     * 하지 않는다.
     *
     * -------------------------------------------------
     *
     * 다음은 이 List에 넣지 않는다.
     *
     * - Natal Deterministic Conclusion
     * - Timeline Deterministic Conclusion
     * - PersonalityStatementContract
     *
     * 이들은 raw LifeDomain GroundingEvidence가
     * 아니기 때문이다.
     */

    public List<ConsultationGroundingEvidence>
    allEvidences() {

        List<ConsultationGroundingEvidence> result =
                new ArrayList<>();

        result.addAll(
                natalEvidences
        );

        for (
                ConsultationGroundingTimelineScenario scenario
                : timelineScenarios
        ) {

            result.addAll(
                    scenario.evidences()
            );
        }

        for (
                ConsultationGroundingTimelineYear timelineYear
                : timelineYears
        ) {

            result.addAll(
                    timelineYear.evidences()
            );
        }

        return List.copyOf(
                result
        );
    }


    public int evidenceCount() {

        return allEvidences()
                .size();
    }


    /**
     * LifeDomain Grounding Evidence 존재 여부.
     *
     * Deterministic Conclusion과
     * Personality Contract 존재 여부는
     * 별도 helper로 조회한다.
     */
    public boolean hasAnyEvidence() {

        return evidenceCount() > 0;
    }


    /**
     * raw Evidence,
     *
     * Category Deterministic Conclusion,
     *
     * 또는 Personality Direct-Natal Contract 중
     * 하나라도 존재하는가.
     *
     * =================================================
     * 중요
     * =================================================
     *
     * 존재 여부를 하나로 조회하는 Helper일 뿐:
     *
     * - 서로 합산하지 않는다.
     * - Status를 합치지 않는다.
     * - 의미를 합성하지 않는다.
     */
    public boolean hasAnyGroundingInput() {

        return hasAnyEvidence()
                || hasAnyCategoryDeterministicConclusion()
                || hasPersonalityStatementContract();
    }


    /*
     * =================================================
     * Normalize Natal Raw Evidence
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
                            "Grounding Natal Evidence는 null일 수 없습니다."
                    )
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Normalize Natal Deterministic Conclusion
     * =================================================
     *
     * 여기서는 이미 Java Rule Engine에서
     * 계산된 Conclusion을 그대로 보존한다.
     *
     * 하지 않는 것:
     *
     * - Conclusion Status 비교
     * - Conclusion 병합
     * - 동일 방향 합산
     * - 반대 방향 MIXED 생성
     * - 대표 Conclusion 선택
     * - 관계 생성
     * - 인과 생성
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
                            "Grounding Natal Deterministic Conclusion은 null일 수 없습니다."
                    )
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Normalize Current Timeline
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
                            "Grounding Timeline Scenario는 null일 수 없습니다."
                    )
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Normalize Timeline Year
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
                            "Grounding Timeline Year는 null일 수 없습니다."
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
     * Normalize Personality Contract
     * =================================================
     */

    private static Optional<NatalPersonalityStatementContract>
    normalizePersonalityStatementContract(

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
                        "Personality Statement Contract는 null일 수 없습니다."
                )
        );
    }


    /*
     * =================================================
     * unavailableReason
     * =================================================
     */

    private static Optional<String>
    normalizeUnavailableReason(
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
                    "Grounding unavailableReason은 빈 문자열일 수 없습니다."
            );
        }

        return Optional.of(
                value
        );
    }
}