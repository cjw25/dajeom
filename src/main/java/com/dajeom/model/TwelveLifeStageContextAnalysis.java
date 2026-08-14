package com.dajeom.model;

import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TwelveLifeStage;
import com.dajeom.type.TwelveLifeStageContextScope;
import com.dajeom.type.TwelveLifeStagePositionContextType;
import com.dajeom.type.TwelveLifeStageStateGroup;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 11차-4.
 *
 * 위치와 원국 범위를 반영한
 * 12운성 Context 분석.
 *
 * =================================================
 *
 * 이 분석은:
 *
 * - 상태 그룹
 * - 위치 Context
 * - 시간 미상 Scope
 *
 * 를 제공한다.
 *
 * =================================================
 *
 * 아직 최종 12운성 Aggregate는 아니다.
 *
 * 11-5에서:
 *
 * TwelveLifeStageEvidenceAnalysis
 * +
 * TwelveLifeStageContextAnalysis
 *
 * 을 TwelveLifeStageAnalysis로 통합한다.
 */
public record TwelveLifeStageContextAnalysis(

        HeavenlyStem dayMaster,

        TwelveLifeStageEvidenceAnalysis evidenceAnalysis,

        List<TwelveLifeStageContextEvidence> contexts,

        TwelveLifeStageContextScope scope,

        int knownPillarCount,

        boolean completeChart

) {

    public TwelveLifeStageContextAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                evidenceAnalysis,
                "11-3 12운성 Evidence 분석이 필요합니다."
        );

        Objects.requireNonNull(
                scope,
                "12운성 Context Scope가 필요합니다."
        );

        contexts =
                contexts == null
                        ? List.of()
                        : List.copyOf(
                        contexts
                );

        if (
                evidenceAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "11-3과 11-4의 일간이 일치하지 않습니다."
            );
        }

        if (
                knownPillarCount
                        != evidenceAnalysis.knownPillarCount()
        ) {

            throw new IllegalArgumentException(
                    "11-3과 11-4의 knownPillarCount가 일치하지 않습니다."
            );
        }

        if (
                completeChart
                        != evidenceAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "11-3과 11-4의 completeChart 상태가 일치하지 않습니다."
            );
        }

        if (
                contexts.size()
                        != evidenceAnalysis.evidences().size()
        ) {

            throw new IllegalArgumentException(
                    "11-3 Evidence 개수와 11-4 Context 개수가 일치하지 않습니다."
            );
        }

        if (
                completeChart
                        && scope
                        != TwelveLifeStageContextScope.FULL_CHART
        ) {

            throw new IllegalArgumentException(
                    "4주 완전 원국은 FULL_CHART Scope여야 합니다."
            );
        }

        if (
                !completeChart
                        && scope
                        != TwelveLifeStageContextScope.THREE_PILLAR_ONLY
        ) {

            throw new IllegalArgumentException(
                    "시간 미상 원국은 THREE_PILLAR_ONLY Scope여야 합니다."
            );
        }

        /*
         * =================================================
         * sourceEvidence 정합성
         * =================================================
         */

        for (
                TwelveLifeStageEvidence evidence
                : evidenceAnalysis.evidences()
        ) {

            boolean found =
                    contexts
                            .stream()
                            .anyMatch(
                                    context ->
                                            context.sourceEvidence()
                                                    == evidence
                            );

            if (
                    !found
            ) {

                throw new IllegalArgumentException(
                        "11-3 Evidence가 11-4 Context에서 누락되었습니다: "
                                + evidence.position()
                );
            }
        }

        /*
         * =================================================
         * 위치 중복 방지
         * =================================================
         */

        for (
                int first = 0;
                first < contexts.size();
                first++
        ) {

            for (
                    int second = first + 1;
                    second < contexts.size();
                    second++
            ) {

                if (
                        contexts
                                .get(first)
                                .position()

                                == contexts
                                .get(second)
                                .position()
                ) {

                    throw new IllegalArgumentException(
                            "동일 위치의 12운성 Context가 중복되었습니다: "
                                    + contexts
                                    .get(first)
                                    .position()
                    );
                }
            }
        }
    }

    /*
     * =================================================
     * 위치 조회
     * =================================================
     */

    public Optional<TwelveLifeStageContextEvidence> contextOf(
            EarthlyBranchPosition position
    ) {

        if (
                position == null
        ) {

            return Optional.empty();
        }

        return contexts
                .stream()
                .filter(
                        context ->
                                context.position()
                                        == position
                )
                .findFirst();
    }

    public Optional<TwelveLifeStageContextEvidence> yearContext() {

        return contextOf(
                EarthlyBranchPosition.YEAR
        );
    }

    public Optional<TwelveLifeStageContextEvidence> monthContext() {

        return contextOf(
                EarthlyBranchPosition.MONTH
        );
    }

    public Optional<TwelveLifeStageContextEvidence> dayContext() {

        return contextOf(
                EarthlyBranchPosition.DAY
        );
    }

    public Optional<TwelveLifeStageContextEvidence> hourContext() {

        return contextOf(
                EarthlyBranchPosition.HOUR
        );
    }

    /*
     * =================================================
     * 12운성 조회
     * =================================================
     */

    public List<TwelveLifeStageContextEvidence> contextsOf(
            TwelveLifeStage stage
    ) {

        if (
                stage == null
        ) {

            return List.of();
        }

        return contexts
                .stream()
                .filter(
                        context ->
                                context.stage()
                                        == stage
                )
                .toList();
    }

    public boolean containsStage(
            TwelveLifeStage stage
    ) {

        return !contextsOf(
                stage
        )
                .isEmpty();
    }

    public long countStage(
            TwelveLifeStage stage
    ) {

        return contextsOf(
                stage
        )
                .size();
    }

    /*
     * =================================================
     * 상태 그룹 조회
     * =================================================
     */

    public List<TwelveLifeStageContextEvidence> contextsOf(
            TwelveLifeStageStateGroup stateGroup
    ) {

        if (
                stateGroup == null
        ) {

            return List.of();
        }

        return contexts
                .stream()
                .filter(
                        context ->
                                context.stateGroup()
                                        == stateGroup
                )
                .toList();
    }

    public boolean containsStateGroup(
            TwelveLifeStageStateGroup stateGroup
    ) {

        return !contextsOf(
                stateGroup
        )
                .isEmpty();
    }

    public long countStateGroup(
            TwelveLifeStageStateGroup stateGroup
    ) {

        return contextsOf(
                stateGroup
        )
                .size();
    }

    /*
     * =================================================
     * 위치 Context 조회
     * =================================================
     */

    public Optional<TwelveLifeStageContextEvidence>
    contextOfPositionType(
            TwelveLifeStagePositionContextType positionContext
    ) {

        if (
                positionContext == null
        ) {

            return Optional.empty();
        }

        return contexts
                .stream()
                .filter(
                        context ->
                                context.positionContext()
                                        == positionContext
                )
                .findFirst();
    }

    /*
     * =================================================
     * 반복 Pattern
     * =================================================
     *
     * 같은 12운성이나 같은 상태 그룹이
     * 여러 지지에 나타나는지를 확인한다.
     *
     * 반복된다고 점수를 더하지 않는다.
     */

    public boolean repeatedStage(
            TwelveLifeStage stage
    ) {

        return countStage(
                stage
        ) >= 2;
    }

    public boolean repeatedStateGroup(
            TwelveLifeStageStateGroup stateGroup
    ) {

        return countStateGroup(
                stateGroup
        ) >= 2;
    }

    /*
     * =================================================
     * 범위
     * =================================================
     */

    public boolean fullChartScope() {

        return scope
                .fullChart();
    }

    public boolean threePillarOnlyScope() {

        return scope
                .threePillarOnly();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}