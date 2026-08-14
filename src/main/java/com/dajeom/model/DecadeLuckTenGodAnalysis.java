package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 13차-4.
 *
 * 전체 대운 Sequence의 십성 분석.
 *
 * =================================================
 *
 * sequenceAnalysis
 *
 * 13-3 결과.
 *
 * =================================================
 *
 * periodAnalyses
 *
 * 각 10년 대운의:
 *
 * - 천간 십성
 * - 지지 본기
 * - 지지 중기
 * - 지지 여기
 *
 * 분석 결과.
 *
 * =================================================
 *
 * analysisAvailable=false
 *
 * 13-3에서 월주 경계 문제 때문에
 * 대운 Sequence 자체가 확정되지 않은 상태.
 */
public record DecadeLuckTenGodAnalysis(

        HeavenlyStem dayMaster,

        DecadeLuckSequenceAnalysis sequenceAnalysis,

        List<DecadeLuckPeriodTenGodAnalysis> periodAnalyses,

        boolean analysisAvailable,

        String unavailableReason

) {

    public DecadeLuckTenGodAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "대운 십성 분석에는 일간이 필요합니다."
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

        /*
         * =================================================
         * 계산 가능
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
                        "대운 Sequence가 없는데 십성 분석을 활성화할 수 없습니다."
                );
            }

            if (
                    periodAnalyses.size()
                            != sequenceAnalysis
                            .periods()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "대운 Period 수와 십성 분석 Period 수가 일치하지 않습니다."
                );
            }

            if (
                    !unavailableReason
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "계산 가능한 대운 십성 분석에는 unavailableReason이 없어야 합니다."
                );
            }
        }

        /*
         * =================================================
         * 계산 불가
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
                        "계산 불가 상태에서는 대운 십성 결과를 생성할 수 없습니다."
                );
            }

            if (
                    unavailableReason
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "대운 십성 계산 불가 사유가 필요합니다."
                );
            }
        }

        /*
         * =================================================
         * Period Identity 검증
         * =================================================
         */

        if (
                analysisAvailable
        ) {

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
                            "대운 Period에 대응하는 십성 분석이 없습니다: "
                                    + period.sequenceNumber()
                    );
                }
            }

            for (
                    DecadeLuckPeriodTenGodAnalysis analysis
                    : periodAnalyses
            ) {

                if (
                        analysis.dayMaster()
                                != dayMaster
                ) {

                    throw new IllegalArgumentException(
                            "대운 십성 분석들의 일간이 일치하지 않습니다."
                    );
                }
            }
        }
    }

    public int periodCount() {

        return periodAnalyses
                .size();
    }

    public Optional<DecadeLuckPeriodTenGodAnalysis>
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
     * 전체 Evidence
     * =================================================
     */

    public List<DecadeLuckTenGodEvidence> evidences() {

        return periodAnalyses
                .stream()
                .flatMap(
                        analysis ->
                                analysis
                                        .evidences()
                                        .stream()
                )
                .toList();
    }

    public List<DecadeLuckTenGodEvidence> evidencesOf(
            StructureTenGodType tenGodType
    ) {

        if (
                tenGodType == null
        ) {

            return List.of();
        }

        return evidences()
                .stream()
                .filter(
                        evidence ->
                                evidence.tenGodType()
                                        == tenGodType
                )
                .toList();
    }

    public boolean contains(
            StructureTenGodType tenGodType
    ) {

        return !evidencesOf(
                tenGodType
        )
                .isEmpty();
    }

    public boolean contains(

            int sequenceNumber,

            StructureTenGodType tenGodType

    ) {

        return periodAnalysis(
                sequenceNumber
        )
                .map(
                        analysis ->
                                analysis.contains(
                                        tenGodType
                                )
                )
                .orElse(
                        false
                );
    }

    /*
     * =================================================
     * 천간 십성
     * =================================================
     */

    public Optional<DecadeLuckTenGodEvidence>
    visibleEvidenceOf(
            int sequenceNumber
    ) {

        return periodAnalysis(
                sequenceNumber
        )
                .flatMap(
                        DecadeLuckPeriodTenGodAnalysis
                                ::visibleEvidence
                );
    }

    /*
     * =================================================
     * 지지 본기
     * =================================================
     */

    public Optional<DecadeLuckTenGodEvidence>
    mainHiddenEvidenceOf(
            int sequenceNumber
    ) {

        return periodAnalysis(
                sequenceNumber
        )
                .flatMap(
                        DecadeLuckPeriodTenGodAnalysis
                                ::mainHiddenEvidence
                );
    }
}