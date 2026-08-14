package com.dajeom.model;

import com.dajeom.type.ShinsalLayer;
import com.dajeom.type.ShinsalPointKind;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 17차-4.
 *
 * 하나의 16차 통합 Timeline Segment와
 * 해당 Segment에서 신살 Rule이 사용할
 * ShinsalRuleContext를 연결한다.
 *
 * =================================================
 *
 * ACTIVE:
 *
 * NATAL
 * +
 * DECADE
 * +
 * ANNUAL
 * +
 * MONTHLY
 *
 * =================================================
 *
 * BEFORE_FIRST:
 *
 * NATAL
 * +
 * ANNUAL
 * +
 * MONTHLY
 *
 * 대운 Point 없음.
 *
 * =================================================
 *
 * AFTER_GENERATED_RANGE:
 *
 * NATAL
 * +
 * ANNUAL
 * +
 * MONTHLY
 *
 * 생성된 대운 데이터 밖이므로
 * 가상의 DECADE Point를 생성하지 않는다.
 *
 * =================================================
 *
 * UNAVAILABLE:
 *
 * NATAL
 * +
 * ANNUAL
 * +
 * MONTHLY
 *
 * 대운 분석이 불가능하므로
 * DECADE Point를 생성하지 않는다.
 */
public record ShinsalSegmentRuleContext(

        LuckIntegratedSegmentAnalysis segmentAnalysis,

        ShinsalRuleContext ruleContext

) {

    public ShinsalSegmentRuleContext {

        Objects.requireNonNull(
                segmentAnalysis,
                "Segment 신살 Context에는 LuckIntegratedSegmentAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                ruleContext,
                "Segment 신살 Context에는 ShinsalRuleContext가 필요합니다."
        );

        /*
         * =================================================
         * completeChart
         * =================================================
         */

        if (
                ruleContext.completeChart()
                        != segmentAnalysis
                        .natalAnalysis()
                        .completeChart()
        ) {

            throw new IllegalArgumentException(
                    "ShinsalRuleContext의 completeChart와 원국 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * NATAL
         * =================================================
         */

        int expectedNatalPointCount =
                ruleContext.completeChart()
                        ? 8
                        : 6;

        if (
                ruleContext
                        .pointsOf(
                                ShinsalLayer.NATAL
                        )
                        .size()
                        != expectedNatalPointCount
        ) {

            throw new IllegalArgumentException(
                    "원국 ShinsalPoint 수가 올바르지 않습니다."
            );
        }

        /*
         * =================================================
         * DECADE
         * =================================================
         */

        List<ShinsalPoint> decadePoints =
                ruleContext
                        .pointsOf(
                                ShinsalLayer.DECADE
                        );

        if (
                segmentAnalysis
                        .hasActiveDecade()
        ) {

            if (
                    decadePoints.size() != 2
            ) {

                throw new IllegalArgumentException(
                        "ACTIVE Segment에는 DECADE Stem/Branch Point가 정확히 2개 필요합니다."
                );
            }

            validateDecadePoints(
                    segmentAnalysis,
                    decadePoints
            );

        } else {

            if (
                    !decadePoints.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "ACTIVE가 아닌 Segment에는 DECADE ShinsalPoint가 존재하면 안 됩니다."
                );
            }
        }

        /*
         * =================================================
         * ANNUAL
         * =================================================
         */

        List<ShinsalPoint> annualPoints =
                ruleContext
                        .pointsOf(
                                ShinsalLayer.ANNUAL
                        );

        if (
                annualPoints.size() != 2
        ) {

            throw new IllegalArgumentException(
                    "Segment에는 ANNUAL Stem/Branch Point가 정확히 2개 필요합니다."
            );
        }

        validateAnnualPoints(
                segmentAnalysis,
                annualPoints
        );

        /*
         * =================================================
         * MONTHLY
         * =================================================
         */

        List<ShinsalPoint> monthlyPoints =
                ruleContext
                        .pointsOf(
                                ShinsalLayer.MONTHLY
                        );

        if (
                monthlyPoints.size() != 2
        ) {

            throw new IllegalArgumentException(
                    "Segment에는 MONTHLY Stem/Branch Point가 정확히 2개 필요합니다."
            );
        }

        validateMonthlyPoints(
                segmentAnalysis,
                monthlyPoints
        );

        /*
         * =================================================
         * 허용 Layer 외에는 없음.
         *
         * 현재 ShinsalLayer가
         * NATAL / DECADE / ANNUAL / MONTHLY
         * 네 개이므로 전체 수로 한번 더 방어한다.
         * =================================================
         */

        int expectedTotal =
                expectedNatalPointCount
                        + 2
                        + 2
                        + (
                        segmentAnalysis.hasActiveDecade()
                                ? 2
                                : 0
                );

        if (
                ruleContext.points().size()
                        != expectedTotal
        ) {

            throw new IllegalArgumentException(
                    "Segment ShinsalRuleContext의 전체 Point 수가 예상값과 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * DECADE
     * =================================================
     */

    private static void validateDecadePoints(

            LuckIntegratedSegmentAnalysis segmentAnalysis,

            List<ShinsalPoint> points

    ) {

        DecadeLuckCandidate candidate =
                segmentAnalysis
                        .decadeCandidate()
                        .orElseThrow();

        ShinsalPoint stemPoint =
                findPoint(
                        points,
                        ShinsalPointKind.HEAVENLY_STEM
                )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "DECADE HeavenlyStem Point가 없습니다."
                                        )
                        );

        ShinsalPoint branchPoint =
                findPoint(
                        points,
                        ShinsalPointKind.EARTHLY_BRANCH
                )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "DECADE EarthlyBranch Point가 없습니다."
                                        )
                        );

        if (
                stemPoint
                        .decadeSequenceNumber()
                        .orElseThrow()
                        != candidate.sequenceNumber()

                        || branchPoint
                        .decadeSequenceNumber()
                        .orElseThrow()
                        != candidate.sequenceNumber()
        ) {

            throw new IllegalArgumentException(
                    "DECADE ShinsalPoint의 대운 순번이 Timeline Candidate와 일치하지 않습니다."
            );
        }

        if (
                stemPoint.requireStem()
                        != candidate
                        .pillar()
                        .stem()
        ) {

            throw new IllegalArgumentException(
                    "DECADE Stem Point가 현재 대운 천간과 일치하지 않습니다."
            );
        }

        if (
                branchPoint.requireBranch()
                        != candidate
                        .pillar()
                        .branch()
        ) {

            throw new IllegalArgumentException(
                    "DECADE Branch Point가 현재 대운 지지와 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * ANNUAL
     * =================================================
     */

    private static void validateAnnualPoints(

            LuckIntegratedSegmentAnalysis segmentAnalysis,

            List<ShinsalPoint> points

    ) {

        AnnualLuckCandidate candidate =
                segmentAnalysis
                        .annualCandidate();

        ShinsalPoint stemPoint =
                findPoint(
                        points,
                        ShinsalPointKind.HEAVENLY_STEM
                )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "ANNUAL HeavenlyStem Point가 없습니다."
                                        )
                        );

        ShinsalPoint branchPoint =
                findPoint(
                        points,
                        ShinsalPointKind.EARTHLY_BRANCH
                )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "ANNUAL EarthlyBranch Point가 없습니다."
                                        )
                        );

        int sajuYear =
                candidate.targetYear();

        if (
                stemPoint
                        .sajuYear()
                        .orElseThrow()
                        != sajuYear

                        || branchPoint
                        .sajuYear()
                        .orElseThrow()
                        != sajuYear
        ) {

            throw new IllegalArgumentException(
                    "ANNUAL ShinsalPoint의 명리연도가 Timeline Candidate와 일치하지 않습니다."
            );
        }

        if (
                stemPoint.requireStem()
                        != candidate.stem()
        ) {

            throw new IllegalArgumentException(
                    "ANNUAL Stem Point가 현재 세운 천간과 일치하지 않습니다."
            );
        }

        if (
                branchPoint.requireBranch()
                        != candidate.branch()
        ) {

            throw new IllegalArgumentException(
                    "ANNUAL Branch Point가 현재 세운 지지와 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * MONTHLY
     * =================================================
     */

    private static void validateMonthlyPoints(

            LuckIntegratedSegmentAnalysis segmentAnalysis,

            List<ShinsalPoint> points

    ) {

        MonthlyLuckCandidate candidate =
                segmentAnalysis
                        .monthlyCandidate();

        ShinsalPoint stemPoint =
                findPoint(
                        points,
                        ShinsalPointKind.HEAVENLY_STEM
                )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "MONTHLY HeavenlyStem Point가 없습니다."
                                        )
                        );

        ShinsalPoint branchPoint =
                findPoint(
                        points,
                        ShinsalPointKind.EARTHLY_BRANCH
                )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "MONTHLY EarthlyBranch Point가 없습니다."
                                        )
                        );

        if (
                stemPoint
                        .sajuYear()
                        .orElseThrow()
                        != candidate.sajuYear()

                        || branchPoint
                        .sajuYear()
                        .orElseThrow()
                        != candidate.sajuYear()
        ) {

            throw new IllegalArgumentException(
                    "MONTHLY ShinsalPoint의 명리연도가 현재 월운과 일치하지 않습니다."
            );
        }

        if (
                stemPoint
                        .monthIndex()
                        .orElseThrow()
                        != candidate.monthIndex()

                        || branchPoint
                        .monthIndex()
                        .orElseThrow()
                        != candidate.monthIndex()
        ) {

            throw new IllegalArgumentException(
                    "MONTHLY ShinsalPoint의 monthIndex가 현재 월운과 일치하지 않습니다."
            );
        }

        if (
                stemPoint.requireStem()
                        != candidate.monthlyStem()
        ) {

            throw new IllegalArgumentException(
                    "MONTHLY Stem Point가 현재 월운 천간과 일치하지 않습니다."
            );
        }

        if (
                branchPoint.requireBranch()
                        != candidate.monthlyBranch()
        ) {

            throw new IllegalArgumentException(
                    "MONTHLY Branch Point가 현재 월운 지지와 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * Helper
     * =================================================
     */

    private static Optional<ShinsalPoint> findPoint(

            List<ShinsalPoint> points,

            ShinsalPointKind kind

    ) {

        return points
                .stream()
                .filter(
                        point ->
                                point.kind()
                                        == kind
                )
                .findFirst();
    }
}