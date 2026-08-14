package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.TwelveLifeStage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 12차-4.
 *
 * 궁위 Context 결합 분석.
 */
public record PalaceContextAnalysis(

        HeavenlyStem dayMaster,

        PalacePositionAnalysis positionAnalysis,

        PalaceTenGodAnalysis tenGodAnalysis,

        TwelveLifeStageAnalysis twelveLifeStageAnalysis,

        PalaceCrossContext crossContext,

        List<PalaceContextEvidence> contexts,

        int knownPalaceCount,

        boolean completeChart

) {

    public PalaceContextAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "궁위 Context 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                positionAnalysis,
                "12-2 PalacePositionAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodAnalysis,
                "12-3 PalaceTenGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                twelveLifeStageAnalysis,
                "11차 TwelveLifeStageAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                crossContext,
                "PalaceCrossContext가 필요합니다."
        );

        contexts =
                contexts == null
                        ? List.of()
                        : List.copyOf(
                        contexts
                );

        /*
         * =================================================
         * 동일 원국 검증
         * =================================================
         */

        if (
                positionAnalysis.dayMaster()
                        != dayMaster

                        || tenGodAnalysis.dayMaster()
                        != dayMaster

                        || twelveLifeStageAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "12-4 입력 분석들의 일간이 일치하지 않습니다."
            );
        }

        if (
                tenGodAnalysis.positionAnalysis()
                        != positionAnalysis
        ) {

            throw new IllegalArgumentException(
                    "12-3이 현재 12-2 PalacePositionAnalysis를 참조하지 않습니다."
            );
        }

        if (
                positionAnalysis.completeChart()
                        != completeChart

                        || tenGodAnalysis.completeChart()
                        != completeChart

                        || twelveLifeStageAnalysis.completeChart()
                        != completeChart

                        || crossContext.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "12-4 입력 분석들의 completeChart 상태가 일치하지 않습니다."
            );
        }

        if (
                positionAnalysis.knownPalaceCount()
                        != knownPalaceCount

                        || tenGodAnalysis.knownPalaceCount()
                        != knownPalaceCount

                        || twelveLifeStageAnalysis.knownPillarCount()
                        != knownPalaceCount
        ) {

            throw new IllegalArgumentException(
                    "12-4 입력 분석들의 알려진 궁위 개수가 일치하지 않습니다."
            );
        }

        if (
                contexts.size()
                        != knownPalaceCount
        ) {

            throw new IllegalArgumentException(
                    "Palace Context 개수와 knownPalaceCount가 일치하지 않습니다."
            );
        }

        for (
                PalacePosition position
                : positionAnalysis.knownPositions()
        ) {

            boolean exists =
                    contexts
                            .stream()
                            .anyMatch(
                                    context ->
                                            context.position()
                                                    == position
                            );

            if (
                    !exists
            ) {

                throw new IllegalArgumentException(
                        "Palace Context가 누락되었습니다: "
                                + position
                );
            }
        }
    }

    public Optional<PalaceContextEvidence> contextOf(
            PalacePosition position
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

    public PalaceContextEvidence yearContext() {

        return contextOf(
                PalacePosition.YEAR
        )
                .orElseThrow();
    }

    public PalaceContextEvidence monthContext() {

        return contextOf(
                PalacePosition.MONTH
        )
                .orElseThrow();
    }

    public PalaceContextEvidence dayContext() {

        return contextOf(
                PalacePosition.DAY
        )
                .orElseThrow();
    }

    public Optional<PalaceContextEvidence> hourContext() {

        return contextOf(
                PalacePosition.HOUR
        );
    }

    public List<PalaceContextEvidence> contextsOf(
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
                                context.twelveLifeStage()
                                        == stage
                )
                .toList();
    }

    public List<PalaceRelationReference> relationsBetween(

            PalacePosition first,

            PalacePosition second

    ) {

        if (
                first == null
                        || second == null
        ) {

            return List.of();
        }

        return crossContext.relations()
                .stream()
                .filter(
                        relation ->
                                relation.connects(
                                        first,
                                        second
                                )
                )
                .toList();
    }

    public List<PalaceTransformationReference>
    transformationsOf(
            PalacePosition position
    ) {

        return crossContext
                .transformationsOf(
                        position
                );
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}