package com.dajeom.model;

import com.dajeom.type.ShinsalLayer;
import com.dajeom.type.ShinsalType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 정밀화 17차-5.
 *
 * 하나의 LuckIntegratedSegmentAnalysis에서
 * 실제로 검출된 운 신살 분석.
 *
 * =================================================
 *
 * rawEvaluation
 *
 * 해당 Segment Context 전체를
 * ShinsalRuleEngine으로 평가한 원본 결과.
 *
 * 여기에는:
 *
 * NATAL
 * DECADE
 * ANNUAL
 * MONTHLY
 *
 * Match가 모두 포함될 수 있다.
 *
 * =================================================
 *
 * luckMatches
 *
 * rawEvaluation 중 실제 matchedPoint가:
 *
 * DECADE
 * ANNUAL
 * MONTHLY
 *
 * 에 존재하는 Match만 추출한 결과.
 *
 * =================================================
 *
 * 원국 신살은 이 객체의 luckMatches에
 * 다시 저장하지 않는다.
 *
 * 원국 신살은 ShinsalTimelineAnalysis의
 * natalEvaluation에서 단 한 번 보존한다.
 */
public record ShinsalSegmentAnalysis(

        LuckIntegratedSegmentAnalysis segmentAnalysis,

        ShinsalSegmentRuleContext segmentRuleContext,

        ShinsalRuleEvaluation rawEvaluation,

        List<ShinsalRuleMatch> luckMatches

) {

    public ShinsalSegmentAnalysis {

        Objects.requireNonNull(
                segmentAnalysis,
                "Segment 신살 분석에는 LuckIntegratedSegmentAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                segmentRuleContext,
                "Segment 신살 분석에는 ShinsalSegmentRuleContext가 필요합니다."
        );

        Objects.requireNonNull(
                rawEvaluation,
                "Segment 신살 분석에는 ShinsalRuleEvaluation이 필요합니다."
        );

        /*
         * =================================================
         * Segment Identity
         * =================================================
         */

        if (
                segmentRuleContext.segmentAnalysis()
                        != segmentAnalysis
        ) {

            throw new IllegalArgumentException(
                    "ShinsalSegmentRuleContext가 현재 LuckIntegratedSegmentAnalysis를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * RuleContext Identity
         * =================================================
         */

        if (
                rawEvaluation.context()
                        != segmentRuleContext.ruleContext()
        ) {

            throw new IllegalArgumentException(
                    "ShinsalRuleEvaluation이 현재 Segment의 ShinsalRuleContext를 참조하지 않습니다."
            );
        }

        luckMatches =
                luckMatches == null
                        ? List.of()
                        : List.copyOf(
                        luckMatches
                );

        /*
         * =================================================
         * luckMatch는 반드시 rawEvaluation에 존재해야 한다.
         *
         * record equals가 아니라 실제 Match 객체 Identity를
         * 기준으로 검증한다.
         * =================================================
         */

        for (
                ShinsalRuleMatch luckMatch
                : luckMatches
        ) {

            Objects.requireNonNull(
                    luckMatch,
                    "운 신살 Match는 null일 수 없습니다."
            );

            boolean contained =
                    rawEvaluation
                            .matches()
                            .stream()
                            .anyMatch(
                                    rawMatch ->
                                            rawMatch
                                                    == luckMatch
                            );

            if (
                    !contained
            ) {

                throw new IllegalArgumentException(
                        "rawEvaluation에 존재하지 않는 ShinsalRuleMatch가 luckMatches에 포함되었습니다."
                );
            }

            /*
             * =================================================
             * 운 Match에는 실제 운 Layer matchedPoint가
             * 최소 하나 존재해야 한다.
             * =================================================
             */

            boolean hasLuckMatchedPoint =
                    luckMatch
                            .matchedPoints()
                            .stream()
                            .anyMatch(
                                    point ->
                                            point.layer()
                                                    != ShinsalLayer.NATAL
                            );

            if (
                    !hasLuckMatchedPoint
            ) {

                throw new IllegalArgumentException(
                        "원국에만 존재하는 신살 Match를 Segment luckMatches에 넣을 수 없습니다: "
                                + luckMatch.type()
                );
            }
        }

        /*
         * =================================================
         * 동일 Match 중복 금지.
         * =================================================
         */

        for (
                int first = 0;
                first < luckMatches.size();
                first++
        ) {

            for (
                    int second = first + 1;
                    second < luckMatches.size();
                    second++
            ) {

                if (
                        luckMatches.get(first)
                                == luckMatches.get(second)
                ) {

                    throw new IllegalArgumentException(
                            "동일 ShinsalRuleMatch 객체가 Segment luckMatches에 중복되었습니다."
                    );
                }
            }
        }

        /*
         * =================================================
         * ACTIVE가 아닌 Segment에서
         * DECADE matchedPoint가 존재하면 안 된다.
         * =================================================
         */

        if (
                !segmentAnalysis.hasActiveDecade()
        ) {

            boolean decadeMatchExists =
                    luckMatches
                            .stream()
                            .anyMatch(
                                    match ->
                                            containsMatchedLayer(
                                                    match,
                                                    ShinsalLayer.DECADE
                                            )
                            );

            if (
                    decadeMatchExists
            ) {

                throw new IllegalArgumentException(
                        "ACTIVE가 아닌 Timeline Segment에서 DECADE 신살 Match가 발견되었습니다."
                );
            }
        }
    }

    /*
     * =================================================
     * 기본
     * =================================================
     */

    public boolean hasLuckMatches() {

        return !luckMatches
                .isEmpty();
    }

    public int luckMatchCount() {

        return luckMatches
                .size();
    }

    /*
     * =================================================
     * Layer별
     * =================================================
     */

    public List<ShinsalRuleMatch>
    decadeMatches() {

        return matchesOf(
                ShinsalLayer.DECADE
        );
    }

    public List<ShinsalRuleMatch>
    annualMatches() {

        return matchesOf(
                ShinsalLayer.ANNUAL
        );
    }

    public List<ShinsalRuleMatch>
    monthlyMatches() {

        return matchesOf(
                ShinsalLayer.MONTHLY
        );
    }

    public List<ShinsalRuleMatch>
    matchesOf(
            ShinsalLayer layer
    ) {

        if (
                layer == null
                        || layer == ShinsalLayer.NATAL
        ) {

            return List.of();
        }

        return luckMatches
                .stream()
                .filter(
                        match ->
                                containsMatchedLayer(
                                        match,
                                        layer
                                )
                )
                .toList();
    }

    /*
     * =================================================
     * 신살 종류별
     * =================================================
     */

    public List<ShinsalRuleMatch>
    matchesOf(
            ShinsalType type
    ) {

        if (
                type == null
        ) {

            return List.of();
        }

        return luckMatches
                .stream()
                .filter(
                        match ->
                                match.type()
                                        == type
                )
                .toList();
    }

    public List<ShinsalRuleMatch>
    matchesOf(

            ShinsalType type,

            ShinsalLayer layer

    ) {

        if (
                type == null
                        || layer == null
                        || layer == ShinsalLayer.NATAL
        ) {

            return List.of();
        }

        return luckMatches
                .stream()
                .filter(
                        match ->
                                match.type()
                                        == type
                )
                .filter(
                        match ->
                                containsMatchedLayer(
                                        match,
                                        layer
                                )
                )
                .toList();
    }

    public boolean has(
            ShinsalType type
    ) {

        return !matchesOf(
                type
        )
                .isEmpty();
    }

    public boolean has(

            ShinsalType type,

            ShinsalLayer layer

    ) {

        return !matchesOf(
                type,
                layer
        )
                .isEmpty();
    }

    public Set<ShinsalType>
    detectedLuckTypes() {

        return luckMatches
                .stream()
                .map(
                        ShinsalRuleMatch
                                ::type
                )
                .collect(
                        Collectors.toUnmodifiableSet()
                );
    }

    /*
     * =================================================
     * Timeline
     * =================================================
     */

    public LuckTimelineSegment timelineSegment() {

        return segmentAnalysis
                .timelineSegment();
    }

    public boolean fullyDefinite() {

        return segmentAnalysis
                .fullyDefinite();
    }

    public boolean timelineUncertaintyExists() {

        return segmentAnalysis
                .timelineUncertaintyExists();
    }

    /*
     * =================================================
     * 현재 운
     * =================================================
     */

    public Optional<DecadeLuckCandidate>
    decadeCandidate() {

        return segmentAnalysis
                .decadeCandidate();
    }

    public AnnualLuckCandidate annualCandidate() {

        return segmentAnalysis
                .annualCandidate();
    }

    public MonthlyLuckCandidate monthlyCandidate() {

        return segmentAnalysis
                .monthlyCandidate();
    }

    /*
     * =================================================
     * Helper
     * =================================================
     */

    private static boolean containsMatchedLayer(

            ShinsalRuleMatch match,

            ShinsalLayer layer

    ) {

        return match
                .matchedPoints()
                .stream()
                .anyMatch(
                        point ->
                                point.layer()
                                        == layer
                );
    }
}