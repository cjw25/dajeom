package com.dajeom.model;

import com.dajeom.type.DecadeLuckTenGodSourceType;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 13차-4.
 *
 * 하나의 대운 Period에 대한
 * 십성 분석 결과.
 *
 * =================================================
 *
 * 하나의 대운은 최소:
 *
 * 1. 천간 십성 1개
 * 2. 지지 본기 십성 1개
 *
 * 를 가진다.
 *
 * 중기/여기는 해당 지지 구성에 따라
 * 존재하지 않을 수 있다.
 */
public record DecadeLuckPeriodTenGodAnalysis(

        HeavenlyStem dayMaster,

        DecadeLuckPeriod period,

        List<DecadeLuckTenGodEvidence> evidences

) {

    public DecadeLuckPeriodTenGodAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "대운 십성 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                period,
                "대운 Period가 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        /*
         * =================================================
         * Evidence 정합성
         * =================================================
         */

        for (
                DecadeLuckTenGodEvidence evidence
                : evidences
        ) {

            if (
                    evidence.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "대운 십성 Evidence의 일간이 일치하지 않습니다."
                );
            }

            if (
                    evidence.period()
                            != period
            ) {

                throw new IllegalArgumentException(
                        "다른 대운 Period의 십성 Evidence가 연결되었습니다."
                );
            }
        }

        /*
         * =================================================
         * Source Type 중복 방지
         * =================================================
         */

        for (
                DecadeLuckTenGodSourceType sourceType
                : DecadeLuckTenGodSourceType.values()
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
                        "동일 대운 Source Type이 중복되었습니다: "
                                + sourceType
                );
            }
        }

        /*
         * =================================================
         * 천간 십성 필수
         * =================================================
         */

        long visibleCount =
                evidences
                        .stream()
                        .filter(
                                DecadeLuckTenGodEvidence
                                        ::visible
                        )
                        .count();

        if (
                visibleCount != 1
        ) {

            throw new IllegalArgumentException(
                    "각 대운에는 정확히 하나의 천간 십성 Evidence가 필요합니다."
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
                                DecadeLuckTenGodEvidence
                                        ::mainHiddenStem
                        )
                        .count();

        if (
                mainHiddenCount != 1
        ) {

            throw new IllegalArgumentException(
                    "각 대운 지지에는 정확히 하나의 본기 십성 Evidence가 필요합니다."
            );
        }
    }

    public int sequenceNumber() {

        return period
                .sequenceNumber();
    }

    public SexagenaryPillar luckPillar() {

        return period
                .pillar();
    }

    public Optional<DecadeLuckTenGodEvidence>
    visibleEvidence() {

        return evidences
                .stream()
                .filter(
                        DecadeLuckTenGodEvidence
                                ::visible
                )
                .findFirst();
    }

    public List<DecadeLuckTenGodEvidence>
    hiddenEvidences() {

        return evidences
                .stream()
                .filter(
                        DecadeLuckTenGodEvidence
                                ::hidden
                )
                .toList();
    }

    public Optional<DecadeLuckTenGodEvidence>
    mainHiddenEvidence() {

        return evidences
                .stream()
                .filter(
                        DecadeLuckTenGodEvidence
                                ::mainHiddenStem
                )
                .findFirst();
    }

    public Optional<DecadeLuckTenGodEvidence>
    middleHiddenEvidence() {

        return evidences
                .stream()
                .filter(
                        DecadeLuckTenGodEvidence
                                ::middleHiddenStem
                )
                .findFirst();
    }

    public Optional<DecadeLuckTenGodEvidence>
    residualHiddenEvidence() {

        return evidences
                .stream()
                .filter(
                        DecadeLuckTenGodEvidence
                                ::residualHiddenStem
                )
                .findFirst();
    }

    public List<DecadeLuckTenGodEvidence> evidencesOf(
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