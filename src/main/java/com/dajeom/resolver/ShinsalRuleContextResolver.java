package com.dajeom.resolver;

import com.dajeom.model.AnnualLuckCandidate;
import com.dajeom.model.DecadeLuckCandidate;
import com.dajeom.model.LuckIntegratedAnalysis;
import com.dajeom.model.LuckIntegratedSegmentAnalysis;
import com.dajeom.model.MonthlyLuckCandidate;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.ShinsalPoint;
import com.dajeom.model.ShinsalRuleContext;
import com.dajeom.model.ShinsalRuleContextResolution;
import com.dajeom.model.ShinsalSegmentRuleContext;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.ShinsalLayer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 17차-4.
 *
 * LuckIntegratedAnalysis
 * ->
 * ShinsalRuleContextResolution
 *
 * 변환 Resolver.
 *
 * =================================================
 *
 * 이 Resolver는 신살 Rule을 실행하지 않는다.
 *
 * 역할:
 *
 * 16차 통합 운 데이터를
 * 17차 신살 Rule이 읽을 수 있는
 * Point 구조로 정규화하는 것.
 *
 * =================================================
 *
 * 원국:
 *
 * ShinsalNatalPointSourceResolver 사용.
 *
 * =================================================
 *
 * 대운:
 *
 * ACTIVE Segment에서만 생성.
 *
 * BEFORE_FIRST
 * AFTER_GENERATED_RANGE
 * UNAVAILABLE
 *
 * 에서는 생성하지 않는다.
 *
 * =================================================
 *
 * 세운:
 *
 * Timeline Segment의 실제
 * AnnualLuckCandidate 사용.
 *
 * =================================================
 *
 * 월운:
 *
 * Timeline Segment의 실제
 * MonthlyLuckCandidate 사용.
 */
@Component
public class ShinsalRuleContextResolver {

    /**
     * =================================================
     * 공식 17-4 API
     * =================================================
     */
    public ShinsalRuleContextResolution resolve(

            LuckIntegratedAnalysis luckIntegratedAnalysis,

            ShinsalNatalPointSourceResolver
                    natalPointSourceResolver

    ) {

        Objects.requireNonNull(
                luckIntegratedAnalysis,
                "17-4에는 LuckIntegratedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalPointSourceResolver,
                "17-4에는 ShinsalNatalPointSourceResolver가 필요합니다."
        );

        /*
         * =================================================
         * 원국 Context는 단 한 번 생성한다.
         * =================================================
         */

        ShinsalRuleContext natalContext =
                resolveNatal(
                        luckIntegratedAnalysis.natalAnalysis(),
                        natalPointSourceResolver
                );

        List<ShinsalSegmentRuleContext> segmentContexts =
                new ArrayList<>(
                        luckIntegratedAnalysis
                                .segmentAnalyses()
                                .size()
                );

        /*
         * =================================================
         * 16차 Timeline 순서를 그대로 유지.
         * =================================================
         */

        for (
                LuckIntegratedSegmentAnalysis segmentAnalysis
                : luckIntegratedAnalysis.segmentAnalyses()
        ) {

            segmentContexts.add(
                    resolveSegment(
                            segmentAnalysis,
                            natalContext
                    )
            );
        }

        return new ShinsalRuleContextResolution(
                luckIntegratedAnalysis,
                natalContext,
                segmentContexts
        );
    }

    /*
     * =================================================
     * 원국 Context
     * =================================================
     */

    public ShinsalRuleContext resolveNatal(

            PalaceAnalysis natalAnalysis,

            ShinsalNatalPointSourceResolver
                    natalPointSourceResolver

    ) {

        Objects.requireNonNull(
                natalAnalysis,
                "원국 신살 Context에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalPointSourceResolver,
                "ShinsalNatalPointSourceResolver가 필요합니다."
        );

        List<ShinsalPoint> sourcePoints =
                natalPointSourceResolver
                        .resolveNatalPoints(
                                natalAnalysis
                        );

        if (
                sourcePoints == null
        ) {

            throw new IllegalStateException(
                    "ShinsalNatalPointSourceResolver는 null을 반환할 수 없습니다."
            );
        }

        List<ShinsalPoint> points =
                List.copyOf(
                        sourcePoints
                );

        /*
         * =================================================
         * 원국 Layer만 허용
         * =================================================
         */

        for (
                ShinsalPoint point
                : points
        ) {

            Objects.requireNonNull(
                    point,
                    "원국 ShinsalPoint는 null일 수 없습니다."
            );

            if (
                    point.layer()
                            != ShinsalLayer.NATAL
            ) {

                throw new IllegalArgumentException(
                        "ShinsalNatalPointSourceResolver는 NATAL Point만 반환해야 합니다: "
                                + point.locationKey()
                );
            }
        }

        int expectedPointCount =
                natalAnalysis.completeChart()
                        ? 8
                        : 6;

        if (
                points.size()
                        != expectedPointCount
        ) {

            throw new IllegalArgumentException(
                    "원국 ShinsalPoint 수가 올바르지 않습니다. expected="
                            + expectedPointCount
                            + ", actual="
                            + points.size()
            );
        }

        ShinsalRuleContext context =
                new ShinsalRuleContext(
                        natalAnalysis.completeChart(),
                        points
                );

        /*
         * =================================================
         * 실제 일간과 Point 일간 일치
         * =================================================
         */

        if (
                context.dayMaster()
                        != natalAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "원국 DAY STEM ShinsalPoint가 PalaceAnalysis의 일간과 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * completeChart=false이면
         * HOUR Point가 정말 존재하지 않는지 한번 더 확인.
         * =================================================
         */

        if (
                !natalAnalysis.completeChart()
        ) {

            boolean hourExists =
                    points
                            .stream()
                            .anyMatch(
                                    point ->
                                            point
                                                    .palacePosition()
                                                    .orElse(null)
                                                    == PalacePosition.HOUR
                            );

            if (
                    hourExists
            ) {

                throw new IllegalArgumentException(
                        "출생시간 미상 원국에 HOUR ShinsalPoint를 생성할 수 없습니다."
                );
            }
        }

        return context;
    }

    /*
     * =================================================
     * Segment Context
     * =================================================
     */

    public ShinsalSegmentRuleContext resolveSegment(

            LuckIntegratedSegmentAnalysis segmentAnalysis,

            ShinsalRuleContext natalContext

    ) {

        Objects.requireNonNull(
                segmentAnalysis,
                "Segment 신살 변환에는 LuckIntegratedSegmentAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalContext,
                "Segment 신살 변환에는 natalContext가 필요합니다."
        );

        if (
                natalContext.completeChart()
                        != segmentAnalysis
                        .natalAnalysis()
                        .completeChart()
        ) {

            throw new IllegalArgumentException(
                    "Segment 원국과 natalContext의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                natalContext.dayMaster()
                        != segmentAnalysis
                        .natalAnalysis()
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "Segment 원국과 natalContext의 일간이 일치하지 않습니다."
            );
        }

        List<ShinsalPoint> points =
                new ArrayList<>();

        /*
         * =================================================
         * NATAL
         *
         * 새 Point를 만들지 않고
         * natalContext Point 객체 그대로 재사용.
         * =================================================
         */

        points.addAll(
                natalContext
                        .pointsOf(
                                ShinsalLayer.NATAL
                        )
        );

        /*
         * =================================================
         * DECADE
         *
         * ACTIVE에서만 생성.
         * =================================================
         */

        if (
                segmentAnalysis
                        .hasActiveDecade()
        ) {

            DecadeLuckCandidate decadeCandidate =
                    segmentAnalysis
                            .decadeCandidate()
                            .orElseThrow();

            points.add(
                    ShinsalPoint.decadeStem(
                            decadeCandidate.sequenceNumber(),
                            decadeCandidate
                                    .pillar()
                                    .stem()
                    )
            );

            points.add(
                    ShinsalPoint.decadeBranch(
                            decadeCandidate.sequenceNumber(),
                            decadeCandidate
                                    .pillar()
                                    .branch()
                    )
            );
        }

        /*
         * BEFORE_FIRST:
         *
         * 대운 없음.
         *
         * AFTER_GENERATED_RANGE:
         *
         * 다음 실제 대운을 추측하지 않는다.
         *
         * UNAVAILABLE:
         *
         * 대운을 임의 생성하지 않는다.
         */

        /*
         * =================================================
         * ANNUAL
         * =================================================
         */

        AnnualLuckCandidate annualCandidate =
                segmentAnalysis
                        .annualCandidate();

        points.add(
                ShinsalPoint.annualStem(
                        annualCandidate.targetYear(),
                        annualCandidate.stem()
                )
        );

        points.add(
                ShinsalPoint.annualBranch(
                        annualCandidate.targetYear(),
                        annualCandidate.branch()
                )
        );

        /*
         * =================================================
         * MONTHLY
         * =================================================
         */

        MonthlyLuckCandidate monthlyCandidate =
                segmentAnalysis
                        .monthlyCandidate();

        points.add(
                ShinsalPoint.monthlyStem(
                        monthlyCandidate.sajuYear(),
                        monthlyCandidate.monthIndex(),
                        monthlyCandidate.monthlyStem()
                )
        );

        points.add(
                ShinsalPoint.monthlyBranch(
                        monthlyCandidate.sajuYear(),
                        monthlyCandidate.monthIndex(),
                        monthlyCandidate.monthlyBranch()
                )
        );

        /*
         * =================================================
         * 최종 Rule Context
         * =================================================
         */

        ShinsalRuleContext ruleContext =
                new ShinsalRuleContext(
                        natalContext.completeChart(),
                        points
                );

        return new ShinsalSegmentRuleContext(
                segmentAnalysis,
                ruleContext
        );
    }
}