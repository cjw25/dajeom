package com.dajeom.model;

import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureBalanceLevel;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-4.
 *
 * 전체 월령 격 후보와
 * 일간 신강·신약을 결합한 분석 결과.
 *
 * 이 단계에서도:
 *
 * - 주격
 * - 보조격
 * - 성격
 * - 파격
 * - 용신
 *
 * 은 결정하지 않는다.
 */
public record StructureBalanceAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        DayMasterStrengthLevel dayMasterStrengthLevel,

        int dayMasterStrengthBasisPoints,

        StructureBalanceEvidence mainQiEvidence,

        List<StructureBalanceEvidence> evidences

) {

    public StructureBalanceAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterStrengthLevel,
                "일간 강약 단계가 필요합니다."
        );

        Objects.requireNonNull(
                mainQiEvidence,
                "월령 정기 격 Evidence가 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                dayMasterStrengthBasisPoints < 0
                        || dayMasterStrengthBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "일간 강도 지수는 0~10000bp 사이여야 합니다."
            );
        }

        if (
                evidences.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "격국 강약 Evidence가 하나 이상 필요합니다."
            );
        }

        if (
                !mainQiEvidence.mainQi()
        ) {

            throw new IllegalArgumentException(
                    "mainQiEvidence는 월령 정기여야 합니다."
            );
        }

        long mainQiCount =
                evidences
                        .stream()
                        .filter(
                                StructureBalanceEvidence
                                        ::mainQi
                        )
                        .count();

        if (
                mainQiCount != 1
        ) {

            throw new IllegalArgumentException(
                    "월령 정기 격 Evidence는 정확히 하나여야 합니다."
            );
        }

        for (
                StructureBalanceEvidence evidence
                : evidences
        ) {

            if (
                    evidence.dayMasterStrengthLevel()
                            != dayMasterStrengthLevel
            ) {

                throw new IllegalArgumentException(
                        "격국 Evidence의 일간 강약 단계가 서로 일치하지 않습니다."
                );
            }

            if (
                    evidence.dayMasterStrengthBasisPoints()
                            != dayMasterStrengthBasisPoints
            ) {

                throw new IllegalArgumentException(
                        "격국 Evidence의 일간 강도 지수가 서로 일치하지 않습니다."
                );
            }
        }
    }

    public double dayMasterStrengthPercent() {

        return dayMasterStrengthBasisPoints
                / 100.0;
    }

    public List<StructureBalanceEvidence>
    evaluatedEvidences() {

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence
                                        .balanceLevel()
                                        .evaluated()
                )
                .toList();
    }

    public List<StructureBalanceEvidence>
    balancedEvidences() {

        return evidences
                .stream()
                .filter(
                        StructureBalanceEvidence
                                ::balanced
                )
                .toList();
    }

    public List<StructureBalanceEvidence>
    workableEvidences() {

        return evidences
                .stream()
                .filter(
                        StructureBalanceEvidence
                                ::workable
                )
                .toList();
    }

    public List<StructureBalanceEvidence>
    burdenedEvidences() {

        return evidences
                .stream()
                .filter(
                        StructureBalanceEvidence
                                ::burdened
                )
                .toList();
    }

    public List<StructureBalanceEvidence>
    acceptableEvidences() {

        return evidences
                .stream()
                .filter(
                        StructureBalanceEvidence
                                ::acceptable
                )
                .toList();
    }

    public List<StructureBalanceEvidence>
    weakDayMasterBurdenEvidences() {

        return evidences
                .stream()
                .filter(
                        StructureBalanceEvidence
                                ::weakDayMasterBurden
                )
                .toList();
    }

    public List<StructureBalanceEvidence>
    strongDayMasterOverSupportEvidences() {

        return evidences
                .stream()
                .filter(
                        StructureBalanceEvidence
                                ::strongDayMasterOverSupport
                )
                .toList();
    }

    public boolean mainQiBalanced() {

        return mainQiEvidence
                .balanced();
    }

    public boolean mainQiWorkable() {

        return mainQiEvidence
                .workable();
    }

    public boolean mainQiBurdened() {

        return mainQiEvidence
                .burdened();
    }

    /**
     * 월령 정기 격 후보 자체는
     * 성립 근거가 있지만 일간 강약과는
     * 부담 관계에 있는 구조인지.
     */
    public boolean establishedMainQiButBurdened() {

        return mainQiEvidence
                .establishmentEvidence()
                .meaningfulCandidate()

                && mainQiEvidence
                .burdened();
    }

    public boolean hasWeakDayMasterBurden() {

        return !weakDayMasterBurdenEvidences()
                .isEmpty();
    }

    public boolean hasStrongDayMasterOverSupport() {

        return !strongDayMasterOverSupportEvidences()
                .isEmpty();
    }

    public Optional<StructureBalanceEvidence>
    evidenceOf(
            StructureTenGodType tenGod
    ) {

        if (
                tenGod == null
        ) {

            return Optional.empty();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.tenGod()
                                        == tenGod
                )
                .findFirst();
    }

    public int countLevel(
            StructureBalanceLevel level
    ) {

        if (
                level == null
        ) {

            return 0;
        }

        return (int) evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.balanceLevel()
                                        == level
                )
                .count();
    }
}