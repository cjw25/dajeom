package com.dajeom.model;

import com.dajeom.type.DecadeLuckContextAxis;
import com.dajeom.type.DecadeLuckContextStatus;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 13차-6.
 *
 * 전체 대운 Sequence의 교차 Context 분석.
 */
public record DecadeLuckContextAnalysis(

        HeavenlyStem dayMaster,

        DecadeLuckTenGodAnalysis tenGodAnalysis,

        DecadeLuckNatalRelationAnalysis natalRelationAnalysis,

        List<DecadeLuckPeriodContextAnalysis> periodAnalyses,

        boolean analysisAvailable,

        String unavailableReason

) {

    public DecadeLuckContextAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "대운 Context 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                tenGodAnalysis,
                "13-4 DecadeLuckTenGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalRelationAnalysis,
                "13-5 DecadeLuckNatalRelationAnalysis가 필요합니다."
        );

        periodAnalyses =
                periodAnalyses == null
                        ? List.of()
                        : List.copyOf(
                        periodAnalyses
                );

        unavailableReason =
                unavailableReason == null
                        ? ""
                        : unavailableReason.trim();

        /*
         * =================================================
         * Compact constructor 입력 참조
         * =================================================
         *
         * record compact constructor body가 끝나기 전에는
         * this.tenGodAnalysis 등의 component field가
         * 아직 자동 대입되지 않는다.
         *
         * 따라서 constructor 내부에서는
         * sequenceAnalysis() 같은 instance convenience를
         * 호출하지 않는다.
         *
         * constructor parameter에서 직접 Sequence를 확보한다.
         */
        DecadeLuckSequenceAnalysis sequenceAnalysis =
                tenGodAnalysis
                        .sequenceAnalysis();

        if (
                tenGodAnalysis.dayMaster()
                        != dayMaster

                        || natalRelationAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "13-4 / 13-5 / 13-6의 일간이 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 같은 13-3 Sequence를 참조해야 한다.
         * =================================================
         */
        if (
                sequenceAnalysis
                        != natalRelationAnalysis
                        .sequenceAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "13-4와 13-5가 서로 다른 대운 Sequence를 참조하고 있습니다."
            );
        }

        if (
                tenGodAnalysis.analysisAvailable()
                        != natalRelationAnalysis.analysisAvailable()
        ) {

            throw new IllegalArgumentException(
                    "13-4와 13-5의 분석 가능 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 분석 가능
         * =================================================
         */
        if (
                analysisAvailable
        ) {

            if (
                    !tenGodAnalysis.analysisAvailable()
                            || !natalRelationAnalysis.analysisAvailable()
            ) {

                throw new IllegalArgumentException(
                        "선행 분석이 불가능한 상태에서 13-6을 활성화할 수 없습니다."
                );
            }

            if (
                    periodAnalyses.size()
                            != tenGodAnalysis
                            .periodAnalyses()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "대운 Period 수와 Context 분석 수가 일치하지 않습니다."
                );
            }

            if (
                    !unavailableReason
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "분석 가능한 상태에는 unavailableReason이 없어야 합니다."
                );
            }

            /*
             * compact constructor 내부이므로
             * sequenceAnalysis() instance method를 호출하지 않고
             * 위에서 확보한 constructor-local Sequence를 사용한다.
             */
            for (
                    DecadeLuckPeriod period
                    : sequenceAnalysis.periods()
            ) {

                boolean found =
                        periodAnalyses
                                .stream()
                                .anyMatch(
                                        analysis ->
                                                analysis.period()
                                                        == period
                                );

                if (
                        !found
                ) {

                    throw new IllegalArgumentException(
                            "대운 Context 분석이 누락되었습니다: "
                                    + period.sequenceNumber()
                    );
                }
            }
        }

        /*
         * =================================================
         * 분석 불가
         * =================================================
         */
        if (
                !analysisAvailable
        ) {

            if (
                    !periodAnalyses
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "분석 불가 상태에서는 대운 Context를 생성할 수 없습니다."
                );
            }

            if (
                    unavailableReason
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "대운 Context 분석 불가 사유가 필요합니다."
                );
            }
        }
    }

    public DecadeLuckSequenceAnalysis sequenceAnalysis() {

        return tenGodAnalysis
                .sequenceAnalysis();
    }

    public PalaceAnalysis natalAnalysis() {

        return natalRelationAnalysis
                .natalAnalysis();
    }

    public Optional<DecadeLuckPeriodContextAnalysis>
    periodAnalysis(
            int sequenceNumber
    ) {

        if (
                sequenceNumber < 1
        ) {

            return Optional.empty();
        }

        return periodAnalyses
                .stream()
                .filter(
                        analysis ->
                                analysis.sequenceNumber()
                                        == sequenceNumber
                )
                .findFirst();
    }

    /*
     * =================================================
     * 전체 Signal
     * =================================================
     */

    public List<DecadeLuckContextSignal> signals() {

        return periodAnalyses
                .stream()
                .flatMap(
                        analysis ->
                                analysis
                                        .signals()
                                        .stream()
                )
                .toList();
    }

    public List<DecadeLuckContextSignal> signalsOf(
            DecadeLuckContextAxis axis
    ) {

        if (
                axis == null
        ) {

            return List.of();
        }

        return signals()
                .stream()
                .filter(
                        signal ->
                                signal.axis()
                                        == axis
                )
                .toList();
    }

    public List<DecadeLuckContextSignal> signalsOf(
            DecadeLuckContextStatus status
    ) {

        if (
                status == null
        ) {

            return List.of();
        }

        return signals()
                .stream()
                .filter(
                        signal ->
                                signal.status()
                                        == status
                )
                .toList();
    }

    public List<DecadeLuckPeriodContextAnalysis>
    periodsInvolving(
            PalacePosition position
    ) {

        if (
                position == null
        ) {

            return List.of();
        }

        return periodAnalyses
                .stream()
                .filter(
                        analysis ->
                                !analysis
                                        .signalsOf(
                                                position
                                        )
                                        .isEmpty()
                )
                .toList();
    }

    public List<DecadeLuckPeriodContextAnalysis>
    periodsInvolving(
            StructureTenGodType tenGodType
    ) {

        if (
                tenGodType == null
        ) {

            return List.of();
        }

        return periodAnalyses
                .stream()
                .filter(
                        analysis ->
                                !analysis
                                        .signalsOf(
                                                tenGodType
                                        )
                                        .isEmpty()
                )
                .toList();
    }

    public List<DecadeLuckPeriodContextAnalysis>
    mixedPeriods() {

        return periodAnalyses
                .stream()
                .filter(
                        DecadeLuckPeriodContextAnalysis
                                ::mixedContext
                )
                .toList();
    }
}