package com.dajeom.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 21차-3.
 *
 * PAST_LUCK / FUTURE_LUCK에서 사용하는
 * 하나의 명리연도 Timeline Range Evidence.
 *
 * =================================================
 *
 * 기존 ConsultationCategorySegmentEvidence는
 * 하나의 실제 Timeline Segment를 표현한다.
 *
 * 따라서 여러 월의 Match를 하나의 Segment에
 * 억지로 합치는 것은 허용하지 않는다.
 *
 * 이 객체는:
 *
 * - 하나의 명리연도
 * - 실제로 분석된 월 인덱스
 * - 해당 연도에서 발생한 Timeline 불확실성
 * - 해당 연도에서 확인된 LifeDomainRuleMatch
 *
 * 를 별도로 보존한다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * - 점수를 계산하지 않는다.
 * - 대표 polarity를 선택하지 않는다.
 * - 길흉을 합산하지 않는다.
 * - 월별 계산 결과를 새로운 명리 사실로 재해석하지 않는다.
 * - 동일 Match는 exact equality 기준으로만 중복 제거한다.
 *
 * =================================================
 *
 * monthIndices:
 *
 * 16차 MonthlyLuckCandidate.monthIndex()의 실제 값.
 *
 * 일반 달력의 월 숫자가 아니라
 * 기존 15~16차가 사용하는 명리 월 인덱스를 그대로 보존한다.
 *
 * 공식 범위는 upstream Monthly Timeline과 동일한
 * 0~11이다.
 *
 * 여기서는 새로운 월 체계를 만들거나
 * 일반 달력의 1~12로 재해석하지 않고,
 * upstream의 공식 monthIndex 범위만 검증한다.
 */
public record ConsultationCategoryTimelineYearEvidence(

        int sajuYear,

        List<Integer> monthIndices,

        boolean timelineUncertaintyExists,

        List<LifeDomainRuleMatch> matches

) {

    public ConsultationCategoryTimelineYearEvidence {

        monthIndices =
                normalizeMonthIndices(
                        monthIndices
                );

        matches =
                normalizeMatches(
                        matches
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
     * Match
     * =================================================
     */

    public boolean hasMatches() {

        return !matches
                .isEmpty();
    }

    public int matchCount() {

        return matches
                .size();
    }

    /*
     * =================================================
     * Normalize Month
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

        List<Integer> normalized =
                new ArrayList<>();

        for (
                Integer monthIndex
                : source
        ) {

            Objects.requireNonNull(
                    monthIndex,
                    "Timeline Year Evidence의 monthIndex는 null일 수 없습니다."
            );

            if (
                    monthIndex < 0
                            || monthIndex >= 12
            ) {

                throw new IllegalArgumentException(
                        "Timeline Year Evidence의 monthIndex는 0~11이어야 합니다: "
                                + monthIndex
                );
            }

            if (
                    !normalized.contains(
                            monthIndex
                    )
            ) {

                normalized.add(
                        monthIndex
                );
            }
        }

        normalized.sort(
                Integer::compareTo
        );

        return List.copyOf(
                normalized
        );
    }

    /*
     * =================================================
     * Normalize Match
     * =================================================
     */

    private static List<LifeDomainRuleMatch>
    normalizeMatches(
            List<LifeDomainRuleMatch> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<LifeDomainRuleMatch> normalized =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : source
        ) {

            Objects.requireNonNull(
                    match,
                    "Timeline Year Evidence의 LifeDomainRuleMatch는 null일 수 없습니다."
            );

            /*
             * equals()가 동일하다고 판단하는
             * 완전히 같은 Match만 제거한다.
             *
             * ruleCode가 같다는 이유만으로
             * 서로 다른 Evidence Context를 합치지 않는다.
             */

            if (
                    !normalized.contains(
                            match
                    )
            ) {

                normalized.add(
                        match
                );
            }
        }

        return List.copyOf(
                normalized
        );
    }
}