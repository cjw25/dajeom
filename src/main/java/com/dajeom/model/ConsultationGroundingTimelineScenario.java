package com.dajeom.model;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 20차.
 * +
 * Category Deterministic Grounding 확장.
 *
 * 하나의 가능한 Timeline Segment를
 * LLM Grounding용 Scenario로 변환한 결과.
 *
 * =================================================
 * Scenario
 * =================================================
 *
 * 출생시간 미상이나
 * 대운 교체 시점 불확실성 때문에
 * 동일 조회 시점에 여러 Segment가 가능하면:
 *
 * Scenario 1
 * Scenario 2
 * ...
 *
 * 로 각각 유지한다.
 *
 * 하나의 대표 Scenario를 고르지 않는다.
 *
 * POSSIBLE을 DEFINITE로 바꾸지 않는다.
 *
 * =================================================
 * evidences
 * =================================================
 *
 * LifeDomainRuleMatch를 변환한
 * raw Grounding Evidence.
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
 * deterministicConclusions
 * =================================================
 *
 * ConsultationCategoryDeterministicRuleEngine이
 * 현재 Timeline Segment에 대해
 * Java에서 확정한:
 *
 * ConsultationCategoryDeterministicMatch
 *
 * 를 LLM 경계용:
 *
 * ConsultationGroundingDeterministicConclusion
 *
 * 으로 변환한 결과.
 *
 * =================================================
 * 공식 생성 계약
 * =================================================
 *
 * 모든 Timeline Scenario는
 * canonical constructor로
 * 전체 구조를 명시해서 생성한다.
 *
 * 특히:
 *
 * deterministicConclusions
 *
 * 를 생략하는 호환 생성 경로는
 * 제공하지 않는다.
 *
 * 호출부는:
 *
 * - raw evidences
 * - deterministicConclusions
 *
 * 를 각각 명시적으로 전달해야 한다.
 *
 * =================================================
 * 중요한 우선순위
 * =================================================
 *
 * deterministicConclusions
 *
 * =
 * 해당 ConsultationCategory에서
 * Java가 확정한 공식 의미 Contract.
 *
 * -------------------------------------------------
 *
 * evidences
 *
 * =
 * 그 판단의 세부 LifeDomain Grounding.
 *
 * =================================================
 * 관계형 풀이 차단
 * =================================================
 *
 * raw evidences가 여러 개 존재한다고 해서:
 *
 * A 때문에 B
 *
 * A가 B로 이어진다
 *
 * A와 B가 함께 있으므로 C다
 *
 * 같은 의미를 LLM이 새로 만들 수 있다는
 * 뜻이 아니다.
 *
 * 관계·인과·결합 의미가 필요하면
 * 그것은 반드시 upstream Java의
 * Category Deterministic Rule이
 * 명시적으로 계산한
 * deterministicConclusion으로 존재해야 한다.
 *
 * =================================================
 * Status 합성 금지
 * =================================================
 *
 * 예:
 *
 * WEALTH Evidence = SUPPORTIVE
 *
 * CAREER Evidence = PRESSURE
 *
 * 라고 해서:
 *
 * Scenario = MIXED
 *
 * 로 계산하지 않는다.
 *
 * -------------------------------------------------
 *
 * 같은 LifeDomain에서도:
 *
 * Annual = SUPPORTIVE
 *
 * Monthly = PRESSURE
 *
 * 라고 해서:
 *
 * 해당 운 = MIXED
 *
 * 로 새로 계산하지 않는다.
 *
 * 별도 Java Rule이 MIXED를 계산한 경우에만
 * deterministicConclusions에 존재할 수 있다.
 *
 * =================================================
 * Current Timeline
 * =================================================
 *
 * deterministicConclusions에는
 * 이 Scenario와 실제로 연결된
 * CURRENT_TIMELINE Layer 결과만
 * GroundingAnalyzer에서 넣는다.
 *
 * NATAL 결과는 여기에 넣지 않는다.
 *
 * TIMELINE_YEAR 결과도 여기에 넣지 않는다.
 *
 * =================================================
 * PAST / FUTURE
 * =================================================
 *
 * PAST_LUCK / FUTURE_LUCK의
 * TIMELINE_YEAR Deterministic Contract는
 *
 * ConsultationGroundingTimelineYear
 *
 * 내부의:
 *
 * ConsultationGroundingTimelineYearDeterministicContract
 *
 * 가 담당한다.
 *
 * 이 Scenario 구조와 섞지 않는다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Scenario가 둘 이상이면
 * 각 Scenario는:
 *
 * - 자기 raw evidences
 * - 자기 deterministic conclusions
 *
 * 를 따로 가져야 한다.
 *
 * 다른 Scenario의 Conclusion을
 * 현재 Scenario에 합치지 않는다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * - raw Evidence와 Category Conclusion 분리
 * - Category Conclusion 우선 의미 경계
 * - Evidence 다수결 금지
 * - Scenario 다수결 금지
 * - 대표 Scenario 선택 금지
 * - Cross-Domain 자동 합성 금지
 * - Cross-Temporal 자동 합성 금지
 * - 새로운 관계 생성 금지
 * - 새로운 인과 생성 금지
 * - 새로운 순서 생성 금지
 */
public record ConsultationGroundingTimelineScenario(

        int scenarioOrder,

        int sajuYear,

        int monthIndex,

        boolean fullyDefinite,

        boolean timelineUncertaintyExists,

        boolean activeDecade,

        boolean beforeFirstDecadeLuck,

        boolean afterGeneratedDecadeRange,

        boolean decadeUnavailable,

        List<ConsultationGroundingEvidence>
        evidences,

        List<ConsultationGroundingDeterministicConclusion>
        deterministicConclusions

) {

    /*
     * =================================================
     * Canonical Constructor
     * =================================================
     */

    public ConsultationGroundingTimelineScenario {

        if (
                scenarioOrder < 1
        ) {

            throw new IllegalArgumentException(
                    "Grounding Timeline Scenario 순서는 1 이상이어야 합니다."
            );
        }

        if (
                monthIndex < 0
                        || monthIndex >= 12
        ) {

            throw new IllegalArgumentException(
                    "Grounding Timeline monthIndex는 0~11이어야 합니다: "
                            + monthIndex
            );
        }

        evidences =
                normalizeEvidences(
                        evidences
                );

        deterministicConclusions =
                normalizeDeterministicConclusions(
                        deterministicConclusions
                );
    }


    /*
     * =================================================
     * Evidence
     * =================================================
     */

    public boolean hasEvidence() {

        return !evidences
                .isEmpty();
    }


    public int evidenceCount() {

        return evidences
                .size();
    }


    /*
     * =================================================
     * Category Deterministic Conclusion
     * =================================================
     */

    public boolean hasDeterministicConclusions() {

        return !deterministicConclusions
                .isEmpty();
    }


    public int deterministicConclusionCount() {

        return deterministicConclusions
                .size();
    }


    /*
     * =================================================
     * Grounding Availability
     * =================================================
     *
     * raw Evidence 또는
     * 공식 Category Deterministic Conclusion 중
     * 하나라도 존재하면
     * 이 Scenario에는 설명 가능한 Grounding이 있다.
     *
     * 존재 여부를 확인하는 Helper일 뿐
     * 두 Grounding Source의 의미를 합성하지 않는다.
     */

    public boolean hasGrounding() {

        return hasEvidence()
                || hasDeterministicConclusions();
    }


    /*
     * =================================================
     * Scenario Certainty
     * =================================================
     */

    /**
     * 이 Scenario를 확정 Scenario라고
     * 사용자에게 설명해도 되는지.
     */
    public boolean definiteScenario() {

        return fullyDefinite
                && !timelineUncertaintyExists;
    }


    public boolean possibleScenario() {

        return !definiteScenario();
    }


    /*
     * =================================================
     * Normalize Evidence
     * =================================================
     */

    private static List<ConsultationGroundingEvidence>
    normalizeEvidences(
            List<ConsultationGroundingEvidence> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<ConsultationGroundingEvidence> normalized =
                source
                        .stream()
                        .map(
                                evidence ->
                                        Objects.requireNonNull(
                                                evidence,
                                                "Grounding Timeline Evidence는 null일 수 없습니다."
                                        )
                        )
                        .toList();

        return List.copyOf(
                normalized
        );
    }


    /*
     * =================================================
     * Normalize Deterministic Conclusion
     * =================================================
     *
     * 여기서는:
     *
     * - Status 비교
     * - 동일 방향 합산
     * - 다수결
     * - 중요도 선정
     * - 관계 생성
     * - 인과 생성
     * - 순서 생성
     *
     * 을 하지 않는다.
     *
     * GroundingAnalyzer가 Scenario에
     * 실제로 배정한 Conclusion 순서를
     * 그대로 보존한다.
     */

    private static List<
            ConsultationGroundingDeterministicConclusion
            >
    normalizeDeterministicConclusions(

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
                normalized =
                source
                        .stream()
                        .map(
                                conclusion ->
                                        Objects.requireNonNull(
                                                conclusion,
                                                "Grounding Timeline Deterministic Conclusion은 null일 수 없습니다."
                                        )
                        )
                        .toList();

        return List.copyOf(
                normalized
        );
    }
}