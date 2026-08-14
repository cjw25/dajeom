package com.dajeom.analyzer;

import com.dajeom.engine.ShinsalRuleEngine;
import com.dajeom.model.ShinsalPoint;
import com.dajeom.model.ShinsalRuleContext;
import com.dajeom.model.ShinsalRuleContextResolution;
import com.dajeom.model.ShinsalRuleEvaluation;
import com.dajeom.model.ShinsalRuleMatch;
import com.dajeom.model.ShinsalSegmentAnalysis;
import com.dajeom.model.ShinsalSegmentRuleContext;
import com.dajeom.model.ShinsalTimelineAnalysis;
import com.dajeom.type.ShinsalLayer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 17차-5.
 *
 * 17-4에서 생성된 ShinsalRuleContext들을
 * 실제 ShinsalRuleEngine으로 평가한다.
 *
 * =================================================
 *
 * 순서:
 *
 * 1.
 * natalContext를 딱 한 번 평가.
 *
 * 2.
 * 각 Timeline Segment Context 평가.
 *
 * 3.
 * Segment Evaluation에서:
 *
 * NATAL-only Match 제거.
 *
 * 4.
 * DECADE / ANNUAL / MONTHLY
 * matchedPoint를 가진 Match만 luckMatches로 보존.
 *
 * =================================================
 *
 * 원국 신살은 Segment마다 중복 저장하지 않는다.
 */
@Component
public class ShinsalTimelineAnalyzer {

    private final ShinsalRuleEngine
            shinsalRuleEngine;

    public ShinsalTimelineAnalyzer(
            ShinsalRuleEngine shinsalRuleEngine
    ) {

        this.shinsalRuleEngine =
                Objects.requireNonNull(
                        shinsalRuleEngine,
                        "ShinsalRuleEngine이 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 17-5 API
     * =================================================
     */
    public ShinsalTimelineAnalysis analyze(
            ShinsalRuleContextResolution contextResolution
    ) {

        Objects.requireNonNull(
                contextResolution,
                "17-5에는 ShinsalRuleContextResolution이 필요합니다."
        );

        /*
         * =================================================
         * 원국 신살 1회
         * =================================================
         */

        ShinsalRuleEvaluation natalEvaluation =
                shinsalRuleEngine
                        .evaluate(
                                contextResolution
                                        .natalContext()
                        );

        validateEvaluationPointIdentity(
                contextResolution.natalContext(),
                natalEvaluation
        );

        validateNatalEvaluation(
                natalEvaluation
        );

        /*
         * =================================================
         * Segment별
         * =================================================
         */

        List<ShinsalSegmentAnalysis> segmentAnalyses =
                new ArrayList<>(
                        contextResolution
                                .segmentContexts()
                                .size()
                );

        for (
                ShinsalSegmentRuleContext segmentContext
                : contextResolution.segmentContexts()
        ) {

            ShinsalRuleContext ruleContext =
                    segmentContext
                            .ruleContext();

            ShinsalRuleEvaluation rawEvaluation =
                    shinsalRuleEngine
                            .evaluate(
                                    ruleContext
                            );

            /*
             * Rule이 Context에 존재하지 않는 Point를
             * 임의 생성해서 Evidence로 반환하는 것을 금지한다.
             */
            validateEvaluationPointIdentity(
                    ruleContext,
                    rawEvaluation
            );

            /*
             * 같은 원국 Point를 사용한 동일 Rule 결과가
             * Segment Context에서도 그대로 유지되는지 검증.
             */
            validateNatalMatchStability(
                    natalEvaluation,
                    rawEvaluation
            );

            List<ShinsalRuleMatch> luckMatches =
                    rawEvaluation
                            .matches()
                            .stream()
                            .filter(
                                    this
                                            ::hasLuckMatchedPoint
                            )
                            .toList();

            segmentAnalyses.add(
                    new ShinsalSegmentAnalysis(
                            segmentContext
                                    .segmentAnalysis(),
                            segmentContext,
                            rawEvaluation,
                            luckMatches
                    )
            );
        }

        return new ShinsalTimelineAnalysis(
                contextResolution,
                natalEvaluation,
                segmentAnalyses
        );
    }

    /*
     * =================================================
     * NATAL Evaluation 검증
     * =================================================
     */

    private void validateNatalEvaluation(
            ShinsalRuleEvaluation evaluation
    ) {

        for (
                ShinsalRuleMatch match
                : evaluation.matches()
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

                throw new IllegalStateException(
                        "원국 전용 ShinsalRuleContext 평가에서 운 Layer Match가 반환되었습니다: "
                                + match.ruleCode()
                );
            }
        }
    }

    /*
     * =================================================
     * Point Identity 검증
     * =================================================
     *
     * Rule은 반드시 전달받은 Context의
     * ShinsalPoint를 Evidence Source로 사용해야 한다.
     *
     * 이런 식의 새 Point 생성은 금지:
     *
     * new ShinsalPoint(...)
     *
     * Rule은 Context에서 받은 실제 Point 객체를
     * referencePoints / matchedPoints에 넣어야 한다.
     */

    private void validateEvaluationPointIdentity(

            ShinsalRuleContext context,

            ShinsalRuleEvaluation evaluation

    ) {

        if (
                evaluation.context()
                        != context
        ) {

            throw new IllegalStateException(
                    "ShinsalRuleEvaluation이 다른 ShinsalRuleContext를 참조합니다."
            );
        }

        for (
                ShinsalRuleMatch match
                : evaluation.matches()
        ) {

            for (
                    ShinsalPoint referencePoint
                    : match.referencePoints()
            ) {

                if (
                        !containsByIdentity(
                                context.points(),
                                referencePoint
                        )
                ) {

                    throw new IllegalStateException(
                            "ShinsalRule이 Context에 존재하지 않는 referencePoint를 반환했습니다: "
                                    + match.ruleCode()
                                    + " / "
                                    + referencePoint.locationKey()
                    );
                }
            }

            for (
                    ShinsalPoint matchedPoint
                    : match.matchedPoints()
            ) {

                if (
                        !containsByIdentity(
                                context.points(),
                                matchedPoint
                        )
                ) {

                    throw new IllegalStateException(
                            "ShinsalRule이 Context에 존재하지 않는 matchedPoint를 반환했습니다: "
                                    + match.ruleCode()
                                    + " / "
                                    + matchedPoint.locationKey()
                    );
                }
            }
        }
    }

    /*
     * =================================================
     * 원국 Match 안정성
     * =================================================
     *
     * Segment에 대운·세운·월운 Point를 추가했다고
     * 기존 원국 신살 Match 자체가 바뀌면 안 된다.
     *
     * 현재 17차 신살 Rule은:
     *
     * 원국 기준
     * ->
     * 각 Point 독립 Match
     *
     * 구조이므로 원국 결과는 Segment 여부와 무관해야 한다.
     */

    private void validateNatalMatchStability(

            ShinsalRuleEvaluation natalEvaluation,

            ShinsalRuleEvaluation segmentEvaluation

    ) {

        List<ShinsalRuleMatch> segmentNatalMatches =
                segmentEvaluation
                        .matches()
                        .stream()
                        .filter(
                                this
                                        ::isNatalOnlyMatch
                        )
                        .toList();

        List<ShinsalRuleMatch> natalMatches =
                natalEvaluation
                        .matches();

        if (
                natalMatches.size()
                        != segmentNatalMatches.size()
        ) {

            throw new IllegalStateException(
                    "운 Point 추가 후 원국 신살 Match 수가 변경되었습니다."
            );
        }

        for (
                ShinsalRuleMatch natalMatch
                : natalMatches
        ) {

            boolean found =
                    segmentNatalMatches
                            .stream()
                            .anyMatch(
                                    segmentMatch ->
                                            sameMatchIdentity(
                                                    natalMatch,
                                                    segmentMatch
                                            )
                            );

            if (
                    !found
            ) {

                throw new IllegalStateException(
                        "Segment 평가에서 원국 신살 Match가 변경되거나 누락되었습니다: "
                                + natalMatch.ruleCode()
                );
            }
        }
    }

    /*
     * =================================================
     * 운 Match
     * =================================================
     */

    private boolean hasLuckMatchedPoint(
            ShinsalRuleMatch match
    ) {

        return match
                .matchedPoints()
                .stream()
                .anyMatch(
                        point ->
                                point.layer()
                                        == ShinsalLayer.DECADE

                                        || point.layer()
                                        == ShinsalLayer.ANNUAL

                                        || point.layer()
                                        == ShinsalLayer.MONTHLY
                );
    }

    private boolean isNatalOnlyMatch(
            ShinsalRuleMatch match
    ) {

        return !match.matchedPoints().isEmpty()
                && match
                .matchedPoints()
                .stream()
                .allMatch(
                        point ->
                                point.layer()
                                        == ShinsalLayer.NATAL
                );
    }

    /*
     * =================================================
     * Match Identity
     * =================================================
     */

    private boolean sameMatchIdentity(

            ShinsalRuleMatch first,

            ShinsalRuleMatch second

    ) {

        if (
                first.type()
                        != second.type()
        ) {

            return false;
        }

        if (
                first.basis()
                        != second.basis()
        ) {

            return false;
        }

        if (
                !first.ruleCode()
                        .equals(
                                second.ruleCode()
                        )
        ) {

            return false;
        }

        if (
                !first.conventionId()
                        .equals(
                                second.conventionId()
                        )
        ) {

            return false;
        }

        if (
                !samePointIdentityList(
                        first.referencePoints(),
                        second.referencePoints()
                )
        ) {

            return false;
        }

        return samePointIdentityList(
                first.matchedPoints(),
                second.matchedPoints()
        );
    }

    /*
     * =================================================
     * Point List Identity
     * =================================================
     */

    private boolean samePointIdentityList(

            List<ShinsalPoint> first,

            List<ShinsalPoint> second

    ) {

        if (
                first.size()
                        != second.size()
        ) {

            return false;
        }

        for (
                int index = 0;
                index < first.size();
                index++
        ) {

            if (
                    first.get(index)
                            != second.get(index)
            ) {

                return false;
            }
        }

        return true;
    }

    private boolean containsByIdentity(

            List<ShinsalPoint> points,

            ShinsalPoint target

    ) {

        return points
                .stream()
                .anyMatch(
                        point ->
                                point
                                        == target
                );
    }
}