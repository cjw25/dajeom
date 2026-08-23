package com.dajeom.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 21차-4
 * +
 * Category Deterministic Timeline Contract 확장.
 *
 * PAST_LUCK / FUTURE_LUCK용
 * LLM Grounding Timeline Year.
 *
 * =================================================
 * 전체 구조
 * =================================================
 *
 * yearOrder
 * sajuYear
 * monthIndices
 * timelineUncertaintyExists
 * evidences
 * deterministicContract
 *
 * =================================================
 * deterministicContract
 * =================================================
 *
 * 해당 명리연도의:
 *
 * BIG_FLOW
 * PRIMARY_ACTIVE_DOMAIN
 * PRIMARY_PRESSURE_DOMAIN
 * CROSS_DOMAIN_PATTERN
 * TURNING_POINT
 * OVERALL_NARRATIVE
 *
 * 를 LLM Grounding 경계로 변환한
 * 최종 deterministic 계약.
 *
 * =================================================
 * 공식 생성 계약
 * =================================================
 *
 * ConsultationGroundingTimelineYear는
 * PAST_LUCK / FUTURE_LUCK의
 * 실제 연도 Grounding 전용 모델이다.
 *
 * 따라서 deterministicContract는
 * 선택값이 아니다.
 *
 * 모든 Timeline Year는 생성 시점부터
 * 완성된 Deterministic Contract를
 * 반드시 가져야 한다.
 *
 * Contract가 없는 연도 객체 자체를
 * 타입 구조에서 허용하지 않는다.
 *
 * =================================================
 * 중요한 우선순위
 * =================================================
 *
 * deterministicContract
 *
 * =
 * 연도 해석의 공식 상위 Meaning Contract
 *
 * -------------------------------------------------
 *
 * evidences
 *
 * =
 * 그 판단의 세부 LifeDomain Grounding 근거
 *
 * =================================================
 * 연도 방향 재계산 금지
 * =================================================
 *
 * 외부 LLM은 raw evidences를 보고
 * 연도 전체 방향을 다시 계산해서는 안 된다.
 *
 * 예:
 *
 * deterministicContract
 * .overallNarrative
 * .status
 *
 * =
 * MIXED
 *
 * 인데
 *
 * raw Evidence에 PRESSURE가 많이 보인다고 해서
 *
 * "전체 PRESSURE"
 *
 * 로 바꿀 수 없다.
 *
 * =================================================
 * monthIndices
 * =================================================
 *
 * monthIndices는 upstream에서 계산된
 * 실제 명리 월 인덱스를 그대로 보존한다.
 *
 * 여기서는:
 *
 * - 일반 달력 월로 재해석하지 않는다.
 * - 새로운 월 범위를 가정하지 않는다.
 * - 대표 월을 선택하지 않는다.
 * - 여러 월을 하나의 월 상태로 합성하지 않는다.
 *
 * 실제 월 인덱스의 범위 계약은
 * upstream Monthly Timeline 계산에 맡긴다.
 *
 * =================================================
 * 구조 분리
 * =================================================
 *
 * 다음 내부 계산 객체는
 * 이 Grounding 모델에 직접 노출하지 않는다.
 *
 * - LuckIntegratedSegmentAnalysis
 * - AnnualLuckAnalysis
 * - MonthlyLuckAnalysis
 * - LifeDomainRuleMatch
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - Evidence 다수결
 * - Evidence 점수화
 * - 연도 Status 재계산
 * - deterministicContract 재계산
 * - Cross-Domain 자동 합성
 * - Cross-Temporal 자동 합성
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - deterministicContract 없는 연도 생성
 */
public record ConsultationGroundingTimelineYear(

        int yearOrder,

        int sajuYear,

        List<Integer> monthIndices,

        boolean timelineUncertaintyExists,

        List<ConsultationGroundingEvidence> evidences,

        ConsultationGroundingTimelineYearDeterministicContract
        deterministicContract

) {

    /*
     * =================================================
     * Canonical Constructor
     * =================================================
     */

    public ConsultationGroundingTimelineYear {

        if (
                yearOrder < 1
        ) {

            throw new IllegalArgumentException(
                    "Grounding Timeline Year 순서는 1 이상이어야 합니다."
            );
        }

        monthIndices =
                normalizeMonthIndices(
                        monthIndices
                );

        evidences =
                normalizeEvidences(
                        evidences
                );

        deterministicContract =
                Objects.requireNonNull(
                        deterministicContract,
                        "Grounding Timeline Year에는 Deterministic Contract가 반드시 필요합니다. sajuYear="
                                + sajuYear
                );
    }


    /*
     * =================================================
     * Month
     * =================================================
     */

    public boolean hasMonths() {

        return !monthIndices
                .isEmpty();
    }


    public int monthCount() {

        return monthIndices
                .size();
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
     * Month Normalize
     * =================================================
     */

    private static List<Integer>
    normalizeMonthIndices(

            List<Integer> source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<Integer> result =
                new ArrayList<>();

        for (
                Integer monthIndex
                : source
        ) {

            Objects.requireNonNull(
                    monthIndex,
                    "Grounding Timeline Year monthIndex는 null일 수 없습니다."
            );

            /*
             * monthIndex의 실제 값 범위는
             * upstream Monthly Timeline 계산의
             * 공식 계약을 그대로 따른다.
             *
             * 이 Grounding 모델에서는
             * 별도의 0~11 / 1~12 가정을
             * 새로 만들지 않는다.
             */

            if (
                    !result.contains(
                            monthIndex
                    )
            ) {

                result.add(
                        monthIndex
                );
            }
        }

        result.sort(
                Integer::compareTo
        );

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Evidence Normalize
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
                            "Grounding Timeline Year Evidence는 null일 수 없습니다."
                    )
            );
        }

        return List.copyOf(
                result
        );
    }
}