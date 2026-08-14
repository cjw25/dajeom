package com.dajeom.model;

import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.StructureBalanceLevel;
import com.dajeom.type.StructureTenGodType;

import java.util.Objects;

/**
 * 정밀화 5차-4.
 *
 * 하나의 격 후보와
 * 일간 강약을 결합한 Evidence.
 *
 * 예:
 *
 * 丁火 일간
 * 정재 庚金 후보
 *
 * 후보 자체는 ESTABLISHED일 수 있지만
 * 丁火가 신약하고 金 세력이 매우 강하다면
 *
 * StructureBalanceLevel.STRAINED
 *
 * 또는
 *
 * StructureBalanceLevel.IMBALANCED
 *
 * 로 판단될 수 있다.
 *
 * 반대로 일간이 충분히 강하다면
 * BALANCED 또는 WORKABLE이 될 수 있다.
 *
 * 주의:
 *
 * 이 값은 격의 길흉이 아니다.
 */
public record StructureBalanceEvidence(

        StructureEstablishmentEvidence
        establishmentEvidence,

        DayMasterStrengthLevel
        dayMasterStrengthLevel,

        int dayMasterStrengthBasisPoints,

        StructureBalanceLevel balanceLevel

) {

    public StructureBalanceEvidence {

        Objects.requireNonNull(
                establishmentEvidence,
                "격 성립 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterStrengthLevel,
                "일간 강약 단계가 필요합니다."
        );

        Objects.requireNonNull(
                balanceLevel,
                "격국 강약 균형 단계가 필요합니다."
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
                establishmentEvidence.inactive()
                        && !balanceLevel.notApplicable()
        ) {

            throw new IllegalArgumentException(
                    "비활성 격 후보는 강약 평가 대상이 아닙니다."
            );
        }

        if (
                !establishmentEvidence.inactive()
                        && balanceLevel.notApplicable()
        ) {

            throw new IllegalArgumentException(
                    "활성 격 후보는 강약 평가 결과가 필요합니다."
            );
        }
    }

    public StructureTenGodType tenGod() {

        return establishmentEvidence
                .tenGod();
    }

    public boolean mainQi() {

        return establishmentEvidence
                .mainQi();
    }

    public boolean middleQi() {

        return establishmentEvidence
                .middleQi();
    }

    public boolean residualQi() {

        return establishmentEvidence
                .residualQi();
    }

    public boolean standardStructureCandidate() {

        return establishmentEvidence
                .standardStructureCandidate();
    }

    public boolean specialStructureCandidate() {

        return establishmentEvidence
                .specialStructureCandidate();
    }

    public boolean activeCandidate() {

        return establishmentEvidence
                .activeCandidate();
    }

    public int elementFinalPower() {

        return establishmentEvidence
                .elementFinalPower();
    }

    public int elementShareBasisPoints() {

        return establishmentEvidence
                .elementShareBasisPoints();
    }

    public double elementSharePercent() {

        return establishmentEvidence
                .elementSharePercent();
    }

    public boolean balanced() {

        return balanceLevel
                .balanced();
    }

    public boolean workable() {

        return balanceLevel
                .workable();
    }

    public boolean strained() {

        return balanceLevel
                .strained();
    }

    public boolean imbalanced() {

        return balanceLevel
                .imbalanced();
    }

    public boolean acceptable() {

        return balanceLevel
                .acceptable();
    }

    public boolean burdened() {

        return balanceLevel
                .burdened();
    }

    /**
     * 비겁·인성 계열.
     *
     * 현재 일간에게 생조 또는
     * 동류 지원 방향으로 작용한다.
     */
    public boolean supportiveFamily() {

        return tenGod().companionGroup()
                || tenGod().resourceGroup();
    }

    /**
     * 식상·재성·관살.
     *
     * 일간이 힘을 쓰거나
     * 직접 압력을 받는 계열.
     */
    public boolean consumingOrControllingFamily() {

        return tenGod().outputGroup()
                || tenGod().wealthGroup()
                || tenGod().officerGroup();
    }

    /**
     * 신약한 일간에 식상·재성·관살이
     * 과도하게 작용하는 형태인지.
     *
     * 이것은 최종 파격 판정이 아니다.
     */
    public boolean weakDayMasterBurden() {

        return dayMasterStrengthLevel
                .weakSide()

                && consumingOrControllingFamily()

                && burdened();
    }

    /**
     * 이미 강한 일간에
     * 비겁·인성이 과하게 중첩되는 형태.
     *
     * 이것 역시 길흉 판단이 아니다.
     */
    public boolean strongDayMasterOverSupport() {

        return dayMasterStrengthLevel
                .strongSide()

                && supportiveFamily()

                && burdened();
    }
}