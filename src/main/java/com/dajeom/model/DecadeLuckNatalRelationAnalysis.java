package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 13차-5.
 *
 * 전체 대운 Sequence와
 * 원국 사이의 관계 분석.
 *
 * =================================================
 *
 * analysisAvailable=false
 *
 * 13-3에서 대운 Sequence 자체가
 * 불확실한 경우.
 *
 * 이 경우 관계 분석 역시
 * 하나로 확정하지 않는다.
 */
public record DecadeLuckNatalRelationAnalysis(

        HeavenlyStem dayMaster,

        PalaceAnalysis natalAnalysis,

        DecadeLuckSequenceAnalysis sequenceAnalysis,

        List<DecadeLuckPeriodNatalRelationAnalysis> periodAnalyses,

        boolean analysisAvailable,

        String unavailableReason

) {

    public DecadeLuckNatalRelationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "대운-원국 관계 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                natalAnalysis,
                "원국 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                sequenceAnalysis,
                "13-3 DecadeLuckSequenceAnalysis가 필요합니다."
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

        if (
                natalAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "원국과 대운 관계 분석의 일간이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 기준 월주 정합성
         * =================================================
         */

        if (
                !sequenceAnalysis
                        .referenceMonthPillar()
                        .samePillar(
                                natalAnalysis
                                        .monthStemEvidence()
                                        .stem(),
                                natalAnalysis
                                        .monthBranchEvidence()
                                        .branch()
                        )
        ) {

            throw new IllegalArgumentException(
                    "13-3 대운 Sequence의 기준 월주와 원국 월주가 일치하지 않습니다."
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
                    !sequenceAnalysis
                            .sequenceAvailable()
            ) {

                throw new IllegalArgumentException(
                        "대운 Sequence가 불확실한데 원국 관계 분석을 활성화할 수 없습니다."
                );
            }

            if (
                    periodAnalyses.size()
                            != sequenceAnalysis
                            .periods()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "대운 Period 수와 원국 관계 분석 수가 일치하지 않습니다."
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
             * 동일 Period 객체 검증.
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
                            "대운 Period에 대응하는 원국 관계 분석이 없습니다: "
                                    + period.sequenceNumber()
                    );
                }
            }

            for (
                    DecadeLuckPeriodNatalRelationAnalysis analysis
                    : periodAnalyses
            ) {

                if (
                        analysis.dayMaster()
                                != dayMaster
                ) {

                    throw new IllegalArgumentException(
                            "각 대운 관계 분석의 일간이 일치하지 않습니다."
                    );
                }

                if (
                        analysis.natalAnalysis()
                                != natalAnalysis
                ) {

                    throw new IllegalArgumentException(
                            "각 대운 관계 분석이 동일한 원국 PalaceAnalysis를 참조하지 않습니다."
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
                        "분석 불가 상태에서는 대운 관계 결과를 생성할 수 없습니다."
                );
            }

            if (
                    unavailableReason
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "대운-원국 관계 분석 불가 사유가 필요합니다."
                );
            }
        }
    }

    /*
     * =================================================
     * Period 조회
     * =================================================
     */

    public Optional<DecadeLuckPeriodNatalRelationAnalysis>
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
     * 전체 관계
     * =================================================
     */

    public List<DecadeLuckNatalRelationReference>
    relations() {

        return periodAnalyses
                .stream()
                .flatMap(
                        analysis ->
                                analysis
                                        .relations()
                                        .stream()
                )
                .toList();
    }

    public List<DecadeLuckNatalTransformationReference>
    transformations() {

        return periodAnalyses
                .stream()
                .flatMap(
                        analysis ->
                                analysis
                                        .transformations()
                                        .stream()
                )
                .toList();
    }

    /*
     * =================================================
     * 특정 Palace가 영향을 받는 모든 대운
     * =================================================
     */

    public List<DecadeLuckPeriodNatalRelationAnalysis>
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
                                        .relationsOf(
                                                position
                                        )
                                        .isEmpty()

                                        || !analysis
                                        .transformationsOf(
                                                position
                                        )
                                        .isEmpty()
                )
                .toList();
    }

    /*
     * =================================================
     * 특정 대운 + Palace
     * =================================================
     */

    public List<DecadeLuckNatalRelationReference>
    relationsOf(

            int sequenceNumber,

            PalacePosition position

    ) {

        return periodAnalysis(
                sequenceNumber
        )
                .map(
                        analysis ->
                                analysis.relationsOf(
                                        position
                                )
                )
                .orElse(
                        List.of()
                );
    }

    public List<DecadeLuckNatalTransformationReference>
    transformationsOf(

            int sequenceNumber,

            PalacePosition position

    ) {

        return periodAnalysis(
                sequenceNumber
        )
                .map(
                        analysis ->
                                analysis.transformationsOf(
                                        position
                                )
                )
                .orElse(
                        List.of()
                );
    }
}