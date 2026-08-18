package com.dajeom.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 21차-4.
 *
 * PAST_LUCK / FUTURE_LUCK용
 * LLM Grounding Timeline Year.
 *
 * =================================================
 *
 * 19차의
 *
 * ConsultationCategoryTimelineYearEvidence
 *
 * 를 20차 LLM Grounding 경계로 변환한 결과다.
 *
 * =================================================
 *
 * 이 객체에는 더 이상:
 *
 * - LuckIntegratedSegmentAnalysis
 * - AnnualLuckAnalysis
 * - MonthlyLuckAnalysis
 * - LifeDomain 원본 계산 객체
 *
 * 를 직접 노출하지 않는다.
 *
 * =================================================
 *
 * 포함:
 *
 * yearOrder
 * -> Range 내부 연도 순서
 *
 * sajuYear
 * -> 실제 명리연도
 *
 * monthIndices
 * -> 해당 연도 Evidence 생성에 실제 참여한
 *    기존 15/16차의 월 인덱스
 *
 * timelineUncertaintyExists
 * -> 해당 연도 Timeline 불확실성
 *
 * evidences
 * -> 20차 Grounding Evidence
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * monthIndex의 범위는 여기서 새로 정의하지 않는다.
 *
 * 0-based / 1-based 여부를
 * Grounding 계층이 임의로 재해석하지 않고
 * 기존 MonthlyLuckCandidate가 생성한 값을
 * 그대로 보존한다.
 *
 * =================================================
 *
 * - 월별 상태를 하나의 polarity로 합산하지 않는다.
 * - 대표 월을 선택하지 않는다.
 * - 성공/실패 점수를 계산하지 않는다.
 * - 연도 Evidence는 실제 19차 Match만 포함한다.
 */
public record ConsultationGroundingTimelineYear(

        int yearOrder,

        int sajuYear,

        List<Integer> monthIndices,

        boolean timelineUncertaintyExists,

        List<ConsultationGroundingEvidence> evidences

) {

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
     *
     * 중요:
     *
     * monthIndex는 기존 15/16차의 공식 값이다.
     *
     * Grounding 계층에서:
     *
     * - 0 ~ 11
     * - 1 ~ 12
     *
     * 같은 별도 범위를 가정하지 않는다.
     *
     * 실제 값은 그대로 유지하고
     * null / 중복만 정리한다.
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
             * 값의 범위를 여기서 검증하지 않는다.
             *
             * 기존 MonthlyLuckCandidate가 결정한
             * 공식 monthIndex를 그대로 보존한다.
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