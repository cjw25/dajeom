package com.dajeom.model;

import com.dajeom.type.AnnualLuckTenGodSourceType;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 14차-3.
 *
 * 하나의 세운 전체 십성 분석.
 *
 * =================================================
 *
 * 세운은 한 해 단위이므로
 * 13차 대운처럼:
 *
 * 전체 Analysis
 * +
 * PeriodAnalysis
 *
 * 2단계로 나누지 않는다.
 *
 * AnnualLuckPeriod 하나에
 * AnnualLuckTenGodAnalysis 하나가 대응한다.
 */
public record AnnualLuckTenGodAnalysis(

        HeavenlyStem dayMaster,

        AnnualLuckPeriod period,

        List<AnnualLuckTenGodEvidence> evidences

) {

    public AnnualLuckTenGodAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "세운 십성 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                period,
                "세운 십성 분석에는 AnnualLuckPeriod가 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        /*
         * =================================================
         * Evidence 기본 검증
         * =================================================
         */

        for (
                AnnualLuckTenGodEvidence evidence
                : evidences
        ) {

            Objects.requireNonNull(
                    evidence,
                    "세운 십성 Evidence는 null일 수 없습니다."
            );

            if (
                    evidence.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "세운 십성 Evidence의 일간이 일치하지 않습니다."
                );
            }

            if (
                    evidence.period()
                            != period
            ) {

                throw new IllegalArgumentException(
                        "다른 AnnualLuckPeriod의 십성 Evidence가 연결되었습니다."
                );
            }
        }

        /*
         * =================================================
         * Source Type 중복 검증
         * =================================================
         *
         * VISIBLE_STEM
         * BRANCH_MAIN
         * BRANCH_MIDDLE
         * BRANCH_RESIDUAL
         *
         * 각각 최대 1개.
         */

        for (
                AnnualLuckTenGodSourceType sourceType
                : AnnualLuckTenGodSourceType.values()
        ) {

            long count =
                    evidences
                            .stream()
                            .filter(
                                    evidence ->
                                            evidence.sourceType()
                                                    == sourceType
                            )
                            .count();

            if (
                    count > 1
            ) {

                throw new IllegalArgumentException(
                        "동일 세운 십성 Source Type이 중복되었습니다: "
                                + sourceType
                );
            }
        }

        /*
         * =================================================
         * 세운 천간 십성 필수
         * =================================================
         */

        long visibleCount =
                evidences
                        .stream()
                        .filter(
                                AnnualLuckTenGodEvidence
                                        ::visible
                        )
                        .count();

        if (
                visibleCount != 1
        ) {

            throw new IllegalArgumentException(
                    "세운에는 정확히 하나의 천간 십성 Evidence가 필요합니다."
            );
        }

        /*
         * =================================================
         * 지지 본기 필수
         * =================================================
         */

        long mainHiddenCount =
                evidences
                        .stream()
                        .filter(
                                AnnualLuckTenGodEvidence
                                        ::mainHiddenStem
                        )
                        .count();

        if (
                mainHiddenCount != 1
        ) {

            throw new IllegalArgumentException(
                    "세운 지지에는 정확히 하나의 본기 십성 Evidence가 필요합니다."
            );
        }
    }

    /*
     * =================================================
     * 기본
     * =================================================
     */

    public int targetYear() {

        return period
                .targetYear();
    }

    public SexagenaryPillar annualPillar() {

        return period
                .pillar();
    }

    /*
     * =================================================
     * 천간
     * =================================================
     */

    public Optional<AnnualLuckTenGodEvidence>
    visibleEvidence() {

        return evidences
                .stream()
                .filter(
                        AnnualLuckTenGodEvidence
                                ::visible
                )
                .findFirst();
    }

    /*
     * =================================================
     * 지지 지장간
     * =================================================
     */

    public List<AnnualLuckTenGodEvidence>
    hiddenEvidences() {

        return evidences
                .stream()
                .filter(
                        AnnualLuckTenGodEvidence
                                ::hidden
                )
                .toList();
    }

    public Optional<AnnualLuckTenGodEvidence>
    mainHiddenEvidence() {

        return evidences
                .stream()
                .filter(
                        AnnualLuckTenGodEvidence
                                ::mainHiddenStem
                )
                .findFirst();
    }

    public Optional<AnnualLuckTenGodEvidence>
    middleHiddenEvidence() {

        return evidences
                .stream()
                .filter(
                        AnnualLuckTenGodEvidence
                                ::middleHiddenStem
                )
                .findFirst();
    }

    public Optional<AnnualLuckTenGodEvidence>
    residualHiddenEvidence() {

        return evidences
                .stream()
                .filter(
                        AnnualLuckTenGodEvidence
                                ::residualHiddenStem
                )
                .findFirst();
    }

    /*
     * =================================================
     * 십성 조회
     * =================================================
     */

    public List<AnnualLuckTenGodEvidence>
    evidencesOf(
            StructureTenGodType tenGodType
    ) {

        if (
                tenGodType == null
        ) {

            return List.of();
        }

        return evidences
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
}