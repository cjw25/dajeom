package com.dajeom.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 21차-4
 * +
 * Category Deterministic Timeline Contract 확장.
 *
 * PAST_LUCK / FUTURE_LUCK용
 * LLM Grounding Timeline Year.
 *
 * =================================================
 * 기존 구성
 * =================================================
 *
 * yearOrder
 * sajuYear
 * monthIndices
 * timelineUncertaintyExists
 * evidences
 *
 * =================================================
 * 추가
 * =================================================
 *
 * deterministicContract
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
 * 중요한 우선순위
 * =================================================
 *
 * deterministicContract
 *
 * =
 * 연도 해석의 공식 상위 Meaning Contract
 *
 * =================================================
 *
 * evidences
 *
 * =
 * 그 판단의 세부 LifeDomain Grounding 근거
 *
 * =================================================
 *
 * 따라서 외부 LLM은:
 *
 * raw evidences를 보고
 * 연도 전체 방향을 다시 계산해서는 안 된다.
 *
 * =================================================
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
 *
 * 내부 계산 객체는 노출하지 않는다.
 *
 * - LuckIntegratedSegmentAnalysis
 * - AnnualLuckAnalysis
 * - MonthlyLuckAnalysis
 * - LifeDomainRuleMatch
 *
 * 등을 직접 담지 않는다.
 */
public record ConsultationGroundingTimelineYear(

        int yearOrder,

        int sajuYear,

        List<Integer> monthIndices,

        boolean timelineUncertaintyExists,

        List<ConsultationGroundingEvidence> evidences,

        Optional<ConsultationGroundingTimelineYearDeterministicContract>
        deterministicContract

) {

    /*
     * =================================================
     * 기존 21차 호출 호환
     * =================================================
     */

    public ConsultationGroundingTimelineYear(

            int yearOrder,

            int sajuYear,

            List<Integer> monthIndices,

            boolean timelineUncertaintyExists,

            List<ConsultationGroundingEvidence> evidences

    ) {

        this(
                yearOrder,
                sajuYear,
                monthIndices,
                timelineUncertaintyExists,
                evidences,
                Optional.empty()
        );
    }


    /*
     * =================================================
     * Canonical
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
                normalizeDeterministicContract(
                        deterministicContract
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
     * Deterministic Contract
     * =================================================
     */

    public boolean hasDeterministicContract() {

        return deterministicContract
                .isPresent();
    }


    public ConsultationGroundingTimelineYearDeterministicContract
    requireDeterministicContract() {

        return deterministicContract
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Grounding Timeline Year에 Deterministic Contract가 없습니다. sajuYear="
                                                + sajuYear
                                )
                );
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


    /*
     * =================================================
     * Contract Normalize
     * =================================================
     */

    private static Optional<
            ConsultationGroundingTimelineYearDeterministicContract
            > normalizeDeterministicContract(

            Optional<
                    ConsultationGroundingTimelineYearDeterministicContract
                    > source

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
                        "Grounding Timeline Year Deterministic Contract는 null일 수 없습니다."
                )
        );
    }
}