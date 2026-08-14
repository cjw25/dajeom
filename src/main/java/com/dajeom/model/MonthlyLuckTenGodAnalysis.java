package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.MonthlyLuckTenGodSourceType;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 15차-3.
 *
 * 하나의 월운 전체 십성 분석.
 *
 * =================================================
 *
 * MonthlyLuckPeriod 하나에
 * MonthlyLuckTenGodAnalysis 하나가 대응한다.
 *
 * =================================================
 *
 * 포함:
 *
 * - 월운 천간 십성
 * - 지지 본기 십성
 * - 지지 중기 십성
 * - 지지 여기 십성
 *
 * =================================================
 *
 * 이 결과를 원국 TenGodCompositeAnalysis에
 * 합산하지 않는다.
 */
public record MonthlyLuckTenGodAnalysis(

        HeavenlyStem dayMaster,

        MonthlyLuckPeriod period,

        List<MonthlyLuckTenGodEvidence> evidences

) {

    public MonthlyLuckTenGodAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "월운 십성 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                period,
                "월운 십성 분석에는 MonthlyLuckPeriod가 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        /*
         * =================================================
         * Evidence Identity
         * =================================================
         */

        for (
                MonthlyLuckTenGodEvidence evidence
                : evidences
        ) {

            Objects.requireNonNull(
                    evidence,
                    "월운 십성 Evidence는 null일 수 없습니다."
            );

            if (
                    evidence.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "월운 십성 Evidence의 일간이 일치하지 않습니다."
                );
            }

            if (
                    evidence.period()
                            != period
            ) {

                throw new IllegalArgumentException(
                        "다른 MonthlyLuckPeriod의 십성 Evidence가 포함되었습니다."
                );
            }
        }

        /*
         * =================================================
         * Source Type은 각각 최대 1개
         * =================================================
         */

        for (
                MonthlyLuckTenGodSourceType sourceType
                : MonthlyLuckTenGodSourceType.values()
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
                        "동일 월운 십성 Source Type이 중복되었습니다: "
                                + sourceType
                );
            }
        }

        /*
         * =================================================
         * 월운 천간은 반드시 하나
         * =================================================
         */

        long visibleCount =
                evidences
                        .stream()
                        .filter(
                                MonthlyLuckTenGodEvidence
                                        ::visible
                        )
                        .count();

        if (
                visibleCount != 1
        ) {

            throw new IllegalArgumentException(
                    "월운에는 정확히 하나의 천간 십성 Evidence가 필요합니다."
            );
        }

        /*
         * =================================================
         * 월운 지지 본기도 반드시 하나
         * =================================================
         */

        long mainCount =
                evidences
                        .stream()
                        .filter(
                                MonthlyLuckTenGodEvidence
                                        ::mainHiddenStem
                        )
                        .count();

        if (
                mainCount != 1
        ) {

            throw new IllegalArgumentException(
                    "월운 지지에는 정확히 하나의 본기 십성 Evidence가 필요합니다."
            );
        }
    }

    /*
     * =================================================
     * 기본
     * =================================================
     */

    public int sajuYear() {

        return period.sajuYear();
    }

    public int monthIndex() {

        return period.monthIndex();
    }

    public int monthOrder() {

        return period.monthOrder();
    }

    public SexagenaryPillar monthlyPillar() {

        return period.pillar();
    }

    /*
     * =================================================
     * 월운 천간
     * =================================================
     */

    public Optional<MonthlyLuckTenGodEvidence>
    visibleEvidence() {

        return evidences
                .stream()
                .filter(
                        MonthlyLuckTenGodEvidence
                                ::visible
                )
                .findFirst();
    }

    /*
     * =================================================
     * 지장간
     * =================================================
     */

    public List<MonthlyLuckTenGodEvidence>
    hiddenEvidences() {

        return evidences
                .stream()
                .filter(
                        MonthlyLuckTenGodEvidence
                                ::hidden
                )
                .toList();
    }

    public Optional<MonthlyLuckTenGodEvidence>
    mainHiddenEvidence() {

        return evidences
                .stream()
                .filter(
                        MonthlyLuckTenGodEvidence
                                ::mainHiddenStem
                )
                .findFirst();
    }

    public Optional<MonthlyLuckTenGodEvidence>
    middleHiddenEvidence() {

        return evidences
                .stream()
                .filter(
                        MonthlyLuckTenGodEvidence
                                ::middleHiddenStem
                )
                .findFirst();
    }

    public Optional<MonthlyLuckTenGodEvidence>
    residualHiddenEvidence() {

        return evidences
                .stream()
                .filter(
                        MonthlyLuckTenGodEvidence
                                ::residualHiddenStem
                )
                .findFirst();
    }

    /*
     * =================================================
     * 십성별 조회
     * =================================================
     */

    public List<MonthlyLuckTenGodEvidence>
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