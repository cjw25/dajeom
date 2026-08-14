package com.dajeom.model;

import com.dajeom.type.ShinsalLayer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 17차-4.
 *
 * 하나의 LuckIntegratedAnalysis를
 * 신살 Rule 실행 Context들로 변환한 최종 결과.
 *
 * =================================================
 *
 * natalContext
 *
 * 원국만 존재.
 *
 * NATAL Point 6개 또는 8개.
 *
 * =================================================
 *
 * segmentContexts
 *
 * 각 16차 Timeline Segment별:
 *
 * NATAL
 * +
 * DECADE 가능
 * +
 * ANNUAL
 * +
 * MONTHLY
 *
 * Context.
 *
 * =================================================
 *
 * natalContext를 따로 보존하는 이유:
 *
 * 원국 신살은 Timeline Segment마다
 * 반복 계산할 필요가 없기 때문이다.
 *
 * 17-5에서는:
 *
 * 원국 신살 = natalContext를 1회 평가
 *
 * 운 신살 = segmentContexts 각각 평가
 *
 * 구조로 사용한다.
 */
public record ShinsalRuleContextResolution(

        LuckIntegratedAnalysis luckIntegratedAnalysis,

        ShinsalRuleContext natalContext,

        List<ShinsalSegmentRuleContext> segmentContexts

) {

    public ShinsalRuleContextResolution {

        Objects.requireNonNull(
                luckIntegratedAnalysis,
                "신살 Context Resolution에는 LuckIntegratedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalContext,
                "신살 Context Resolution에는 natalContext가 필요합니다."
        );

        /*
         * =================================================
         * natalContext는 NATAL만 가져야 한다.
         * =================================================
         */

        if (
                !natalContext
                        .pointsOf(
                                ShinsalLayer.DECADE
                        )
                        .isEmpty()

                        || !natalContext
                        .pointsOf(
                                ShinsalLayer.ANNUAL
                        )
                        .isEmpty()

                        || !natalContext
                        .pointsOf(
                                ShinsalLayer.MONTHLY
                        )
                        .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "natalContext에는 운 ShinsalPoint가 존재하면 안 됩니다."
            );
        }

        if (
                natalContext.completeChart()
                        != luckIntegratedAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "natalContext의 completeChart와 LuckIntegratedAnalysis가 일치하지 않습니다."
            );
        }

        if (
                natalContext.dayMaster()
                        != luckIntegratedAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "natalContext의 일간과 LuckIntegratedAnalysis의 일간이 일치하지 않습니다."
            );
        }

        segmentContexts =
                segmentContexts == null
                        ? List.of()
                        : List.copyOf(
                        segmentContexts
                );

        /*
         * =================================================
         * Timeline Segment 수와 정확히 일치
         * =================================================
         */

        if (
                segmentContexts.size()
                        != luckIntegratedAnalysis
                        .segmentAnalyses()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "LuckIntegratedSegmentAnalysis 수와 ShinsalSegmentRuleContext 수가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 순서 + Identity
         * =================================================
         */

        for (
                int index = 0;
                index < segmentContexts.size();
                index++
        ) {

            ShinsalSegmentRuleContext segmentContext =
                    Objects.requireNonNull(
                            segmentContexts.get(index),
                            "ShinsalSegmentRuleContext는 null일 수 없습니다."
                    );

            LuckIntegratedSegmentAnalysis expectedSegment =
                    luckIntegratedAnalysis
                            .segmentAnalyses()
                            .get(index);

            if (
                    segmentContext.segmentAnalysis()
                            != expectedSegment
            ) {

                throw new IllegalArgumentException(
                        "신살 Segment Context 순서가 LuckIntegratedAnalysis와 일치하지 않습니다: "
                                + index
                );
            }

            /*
             * =================================================
             * 모든 Segment가 동일한 원국 Point 객체를
             * 재사용하는지 확인.
             *
             * 같은 원국을 Segment마다 새로 만들어
             * 다른 Evidence Source처럼 보이지 않게 한다.
             * =================================================
             */

            validateNatalPointIdentity(
                    natalContext,
                    segmentContext.ruleContext()
            );
        }
    }

    /*
     * =================================================
     * 조회
     * =================================================
     */

    public Optional<ShinsalSegmentRuleContext>
    contextOf(
            LuckIntegratedSegmentAnalysis segmentAnalysis
    ) {

        if (
                segmentAnalysis == null
        ) {

            return Optional.empty();
        }

        return segmentContexts
                .stream()
                .filter(
                        context ->
                                context.segmentAnalysis()
                                        == segmentAnalysis
                )
                .findFirst();
    }

    public ShinsalSegmentRuleContext contextAt(
            int index
    ) {

        if (
                index < 0
                        || index >= segmentContexts.size()
        ) {

            throw new IndexOutOfBoundsException(
                    "신살 Segment Context index가 범위를 벗어났습니다: "
                            + index
            );
        }

        return segmentContexts
                .get(index);
    }

    public int segmentCount() {

        return segmentContexts
                .size();
    }

    public boolean hasActiveDecadeContext() {

        return segmentContexts
                .stream()
                .anyMatch(
                        context ->
                                context
                                        .segmentAnalysis()
                                        .hasActiveDecade()
                );
    }

    /*
     * =================================================
     * 원국 Identity
     * =================================================
     */

    private static void validateNatalPointIdentity(

            ShinsalRuleContext natalContext,

            ShinsalRuleContext segmentContext

    ) {

        List<ShinsalPoint> natalPoints =
                natalContext
                        .pointsOf(
                                ShinsalLayer.NATAL
                        );

        List<ShinsalPoint> segmentNatalPoints =
                segmentContext
                        .pointsOf(
                                ShinsalLayer.NATAL
                        );

        if (
                natalPoints.size()
                        != segmentNatalPoints.size()
        ) {

            throw new IllegalArgumentException(
                    "Segment Context의 원국 Point 수가 natalContext와 일치하지 않습니다."
            );
        }

        for (
                ShinsalPoint natalPoint
                : natalPoints
        ) {

            ShinsalPoint matched =
                    segmentNatalPoints
                            .stream()
                            .filter(
                                    point ->
                                            point.locationKey()
                                                    .equals(
                                                            natalPoint.locationKey()
                                                    )
                            )
                            .findFirst()
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "Segment Context에 원국 Point가 누락되었습니다: "
                                                            + natalPoint.locationKey()
                                            )
                            );

            if (
                    matched != natalPoint
            ) {

                throw new IllegalArgumentException(
                        "Segment Context가 natalContext의 원국 ShinsalPoint 객체를 재사용하지 않았습니다: "
                                + natalPoint.locationKey()
                );
            }
        }
    }
}