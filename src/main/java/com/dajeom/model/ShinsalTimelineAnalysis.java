package com.dajeom.model;

import com.dajeom.type.ShinsalLayer;
import com.dajeom.type.ShinsalType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 정밀화 17차-5.
 *
 * 원국 신살 1회
 * +
 * Timeline Segment별 운 신살
 *
 * 분석 결과.
 *
 * =================================================
 *
 * natalEvaluation
 *
 * 원국 NATAL Context만 평가.
 *
 * 따라서 여기의 Match는 모두
 * NATAL matchedPoint만 가져야 한다.
 *
 * =================================================
 *
 * segmentAnalyses
 *
 * 각 Timeline Segment에서 검출된:
 *
 * DECADE
 * ANNUAL
 * MONTHLY
 *
 * 신살.
 *
 * =================================================
 *
 * 같은 원국 천을귀인/문창/화개 등을
 * Segment마다 중복 저장하지 않는다.
 */
public record ShinsalTimelineAnalysis(

        ShinsalRuleContextResolution contextResolution,

        ShinsalRuleEvaluation natalEvaluation,

        List<ShinsalSegmentAnalysis> segmentAnalyses

) {

    public ShinsalTimelineAnalysis {

        Objects.requireNonNull(
                contextResolution,
                "Timeline 신살 분석에는 ShinsalRuleContextResolution이 필요합니다."
        );

        Objects.requireNonNull(
                natalEvaluation,
                "Timeline 신살 분석에는 natalEvaluation이 필요합니다."
        );

        /*
         * =================================================
         * NATAL Context Identity
         * =================================================
         */

        if (
                natalEvaluation.context()
                        != contextResolution.natalContext()
        ) {

            throw new IllegalArgumentException(
                    "natalEvaluation이 17-4 natalContext를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * natalEvaluation에는 NATAL matchedPoint만 존재.
         * =================================================
         */

        for (
                ShinsalRuleMatch match
                : natalEvaluation.matches()
        ) {

            boolean allNatal =
                    match
                            .matchedPoints()
                            .stream()
                            .allMatch(
                                    point ->
                                            point.layer()
                                                    == ShinsalLayer.NATAL
                            );

            if (
                    !allNatal
            ) {

                throw new IllegalArgumentException(
                        "natalEvaluation에 운 Layer 신살 Match가 포함되었습니다: "
                                + match.type()
                );
            }
        }

        segmentAnalyses =
                segmentAnalyses == null
                        ? List.of()
                        : List.copyOf(
                        segmentAnalyses
                );

        /*
         * =================================================
         * Segment 수
         * =================================================
         */

        if (
                segmentAnalyses.size()
                        != contextResolution
                        .segmentContexts()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "ShinsalSegmentAnalysis 수와 17-4 Segment Context 수가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Timeline 순서 + Identity
         * =================================================
         */

        for (
                int index = 0;
                index < segmentAnalyses.size();
                index++
        ) {

            ShinsalSegmentAnalysis analysis =
                    Objects.requireNonNull(
                            segmentAnalyses.get(index),
                            "ShinsalSegmentAnalysis는 null일 수 없습니다."
                    );

            ShinsalSegmentRuleContext expectedContext =
                    contextResolution
                            .segmentContexts()
                            .get(index);

            if (
                    analysis.segmentRuleContext()
                            != expectedContext
            ) {

                throw new IllegalArgumentException(
                        "Segment 신살 분석 순서가 17-4 Context 순서와 일치하지 않습니다: "
                                + index
                );
            }

            if (
                    analysis.segmentAnalysis()
                            != expectedContext
                            .segmentAnalysis()
            ) {

                throw new IllegalArgumentException(
                        "Segment 신살 분석이 다른 LuckIntegratedSegmentAnalysis를 참조합니다."
                );
            }
        }
    }

    /*
     * =================================================
     * 16차
     * =================================================
     */

    public LuckIntegratedAnalysis luckIntegratedAnalysis() {

        return contextResolution
                .luckIntegratedAnalysis();
    }

    public boolean completeChart() {

        return luckIntegratedAnalysis()
                .completeChart();
    }

    /*
     * =================================================
     * 원국 신살
     * =================================================
     */

    public List<ShinsalRuleMatch>
    natalMatches() {

        return natalEvaluation
                .matches();
    }

    public boolean hasNatalMatches() {

        return !natalMatches()
                .isEmpty();
    }

    public List<ShinsalRuleMatch>
    natalMatchesOf(
            ShinsalType type
    ) {

        if (
                type == null
        ) {

            return List.of();
        }

        return natalMatches()
                .stream()
                .filter(
                        match ->
                                match.type()
                                        == type
                )
                .toList();
    }

    public boolean hasNatal(
            ShinsalType type
    ) {

        return !natalMatchesOf(
                type
        )
                .isEmpty();
    }

    public Set<ShinsalType>
    detectedNatalTypes() {

        return natalMatches()
                .stream()
                .map(
                        ShinsalRuleMatch::type
                )
                .collect(
                        Collectors.toUnmodifiableSet()
                );
    }

    /*
     * =================================================
     * Segment
     * =================================================
     */

    public int segmentCount() {

        return segmentAnalyses
                .size();
    }

    public ShinsalSegmentAnalysis segmentAt(
            int index
    ) {

        if (
                index < 0
                        || index >= segmentAnalyses.size()
        ) {

            throw new IndexOutOfBoundsException(
                    "ShinsalSegmentAnalysis index가 범위를 벗어났습니다: "
                            + index
            );
        }

        return segmentAnalyses
                .get(index);
    }

    public Optional<ShinsalSegmentAnalysis>
    segmentOf(
            LuckIntegratedSegmentAnalysis segmentAnalysis
    ) {

        if (
                segmentAnalysis == null
        ) {

            return Optional.empty();
        }

        return segmentAnalyses
                .stream()
                .filter(
                        analysis ->
                                analysis.segmentAnalysis()
                                        == segmentAnalysis
                )
                .findFirst();
    }

    /*
     * =================================================
     * Layer별 Segment Match
     * =================================================
     *
     * 주의:
     *
     * 같은 세운/월운이 복수 Timeline Segment에
     * 걸쳐 있다면 동일한 위치의 신살이
     * 여러 Segment에서 반복되어 나타날 수 있다.
     *
     * 여기서는 Timeline 의미를 보존하기 위해
     * 임의 Dedup 하지 않는다.
     *
     * 최종 unique Evidence 정규화는
     * 17-6에서 수행한다.
     */

    public List<ShinsalRuleMatch>
    segmentMatchesOf(
            ShinsalLayer layer
    ) {

        if (
                layer == null
                        || layer == ShinsalLayer.NATAL
        ) {

            return List.of();
        }

        return segmentAnalyses
                .stream()
                .flatMap(
                        analysis ->
                                analysis
                                        .matchesOf(
                                                layer
                                        )
                                        .stream()
                )
                .toList();
    }

    public List<ShinsalRuleMatch>
    decadeSegmentMatches() {

        return segmentMatchesOf(
                ShinsalLayer.DECADE
        );
    }

    public List<ShinsalRuleMatch>
    annualSegmentMatches() {

        return segmentMatchesOf(
                ShinsalLayer.ANNUAL
        );
    }

    public List<ShinsalRuleMatch>
    monthlySegmentMatches() {

        return segmentMatchesOf(
                ShinsalLayer.MONTHLY
        );
    }

    /*
     * =================================================
     * 신살 종류별 Segment
     * =================================================
     */

    public List<ShinsalSegmentAnalysis>
    segmentsHaving(
            ShinsalType type
    ) {

        if (
                type == null
        ) {

            return List.of();
        }

        return segmentAnalyses
                .stream()
                .filter(
                        analysis ->
                                analysis.has(
                                        type
                                )
                )
                .toList();
    }

    public List<ShinsalSegmentAnalysis>
    segmentsHaving(

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

        return segmentAnalyses
                .stream()
                .filter(
                        analysis ->
                                analysis.has(
                                        type,
                                        layer
                                )
                )
                .toList();
    }

    /*
     * =================================================
     * 전체 검출 Type
     * =================================================
     */

    public Set<ShinsalType>
    detectedTypes() {

        Set<ShinsalType> result =
                new java.util.HashSet<>(
                        detectedNatalTypes()
                );

        for (
                ShinsalSegmentAnalysis segmentAnalysis
                : segmentAnalyses
        ) {

            result.addAll(
                    segmentAnalysis
                            .detectedLuckTypes()
            );
        }

        return Set.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Segment 운 Match 전체
     * =================================================
     */

    public List<ShinsalRuleMatch>
    allSegmentLuckMatches() {

        List<ShinsalRuleMatch> result =
                new ArrayList<>();

        for (
                ShinsalSegmentAnalysis analysis
                : segmentAnalyses
        ) {

            result.addAll(
                    analysis.luckMatches()
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 운 신살 존재 여부
     * =================================================
     */

    public boolean hasLuckMatches() {

        return segmentAnalyses
                .stream()
                .anyMatch(
                        ShinsalSegmentAnalysis
                                ::hasLuckMatches
                );
    }

    public boolean hasLuck(
            ShinsalType type
    ) {

        if (
                type == null
        ) {

            return false;
        }

        return segmentAnalyses
                .stream()
                .anyMatch(
                        analysis ->
                                analysis.has(
                                        type
                                )
                );
    }
}