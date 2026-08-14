package com.dajeom.model;

import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.Objects;

/**
 * 정밀화 4차 전체 신강·신약 분석 결과.
 *
 * 포함:
 *
 * 4-1 일간 기준 오행 관계
 * 4-2 득령
 * 4-3 득지
 * 4-4 득세
 * 4-5 비겁·인성 지원력
 * 4-6 식상·재성·관살 압력
 * 4-7 극단구조 후보
 * 4-8 최종 Strength
 *
 * 이후:
 *
 * - 격국
 * - 조후
 * - 용신
 *
 * 등이 이 결과를 사용할 수 있다.
 */
public record DayMasterStrengthAnalysis(

        ElementStrengthAnalysis elementStrengthAnalysis,

        DayMasterElementRelationAnalysis
        elementRelationAnalysis,

        DayMasterSeasonalAnalysis
        seasonalAnalysis,

        DayMasterGroundAnalysis
        groundAnalysis,

        DayMasterForceFormationAnalysis
        forceFormationAnalysis,

        DayMasterSupportPowerAnalysis
        supportPowerAnalysis,

        DayMasterPressurePowerAnalysis
        pressurePowerAnalysis,

        ExtremeStrengthStructureAnalysis
        extremeStructureAnalysis,

        DayMasterStrengthEvidence evidence,

        DayMasterStrengthLevel level

) {

    public DayMasterStrengthAnalysis {

        Objects.requireNonNull(
                elementStrengthAnalysis,
                "오행 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                elementRelationAnalysis,
                "일간 오행 관계 분석이 필요합니다."
        );

        Objects.requireNonNull(
                seasonalAnalysis,
                "득령 분석이 필요합니다."
        );

        Objects.requireNonNull(
                groundAnalysis,
                "득지 분석이 필요합니다."
        );

        Objects.requireNonNull(
                forceFormationAnalysis,
                "득세 분석이 필요합니다."
        );

        Objects.requireNonNull(
                supportPowerAnalysis,
                "지원 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                pressurePowerAnalysis,
                "압력 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                extremeStructureAnalysis,
                "극단 구조 분석이 필요합니다."
        );

        Objects.requireNonNull(
                evidence,
                "최종 Strength Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                level,
                "신강·신약 단계가 필요합니다."
        );

        validateConsistency(
                elementStrengthAnalysis,
                elementRelationAnalysis,
                seasonalAnalysis,
                groundAnalysis,
                forceFormationAnalysis,
                supportPowerAnalysis,
                pressurePowerAnalysis,
                evidence
        );
    }

    private static void validateConsistency(

            ElementStrengthAnalysis elementStrength,

            DayMasterElementRelationAnalysis relations,

            DayMasterSeasonalAnalysis seasonal,

            DayMasterGroundAnalysis ground,

            DayMasterForceFormationAnalysis formation,

            DayMasterSupportPowerAnalysis support,

            DayMasterPressurePowerAnalysis pressure,

            DayMasterStrengthEvidence evidence

    ) {

        HeavenlyStem dayMaster =
                elementStrength.dayMaster();

        if (
                dayMaster != relations.dayMaster()
                        || dayMaster != seasonal.dayMaster()
                        || dayMaster != ground.dayMaster()
                        || dayMaster != formation.dayMaster()
                        || dayMaster != support.dayMaster()
                        || dayMaster != pressure.dayMaster()
                        || dayMaster != evidence.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "신강·신약 분석 단계의 일간이 서로 일치하지 않습니다."
            );
        }

        FiveElement dayMasterElement =
                elementStrength
                        .dayMasterElement();

        if (
                dayMasterElement
                        != relations.dayMasterElement()

                        || dayMasterElement
                        != seasonal.dayMasterElement()

                        || dayMasterElement
                        != ground.dayMasterElement()

                        || dayMasterElement
                        != formation.dayMasterElement()

                        || dayMasterElement
                        != support.dayMasterElement()

                        || dayMasterElement
                        != pressure.dayMasterElement()

                        || dayMasterElement
                        != evidence.dayMasterElement()
        ) {

            throw new IllegalArgumentException(
                    "신강·신약 분석 단계의 일간 오행이 서로 일치하지 않습니다."
            );
        }

        if (
                support.totalSupportPower()
                        != evidence.totalSupportPower()
        ) {

            throw new IllegalArgumentException(
                    "최종 Strength Evidence의 지원력이 4-5 결과와 일치하지 않습니다."
            );
        }

        if (
                pressure.totalPressurePower()
                        != evidence.totalPressurePower()
        ) {

            throw new IllegalArgumentException(
                    "최종 Strength Evidence의 압력이 4-6 결과와 일치하지 않습니다."
            );
        }
    }

    public HeavenlyStem dayMaster() {

        return elementStrengthAnalysis
                .dayMaster();
    }

    public FiveElement dayMasterElement() {

        return elementStrengthAnalysis
                .dayMasterElement();
    }

    public String levelKorean() {

        return level.koreanName();
    }

    /**
     * 구조 보정까지 반영한
     * 내부 강도 지수.
     *
     * 확률이 아니다.
     */
    public int strengthBasisPoints() {

        return evidence
                .adjustedStrengthBasisPoints();
    }

    public double strengthPercent() {

        return evidence
                .adjustedStrengthPercent();
    }

    public int supportPower() {

        return supportPowerAnalysis
                .totalSupportPower();
    }

    public int pressurePower() {

        return pressurePowerAnalysis
                .totalPressurePower();
    }

    public boolean strongSide() {

        return level.strongSide();
    }

    public boolean weakSide() {

        return level.weakSide();
    }

    public boolean balancedZone() {

        return level.balancedZone();
    }

    public boolean extreme() {

        return level.extreme();
    }

    public boolean directlyGainsCommand() {

        return seasonalAnalysis
                .directlyGainsCommand();
    }

    public boolean seasonallySupported() {

        return seasonalAnalysis
                .receivesSeasonalSupport();
    }

    public boolean grounded() {

        return groundAnalysis
                .grounded();
    }

    public boolean rootless() {

        return groundAnalysis
                .rootless();
    }

    public boolean gainsForce() {

        return forceFormationAnalysis
                .gainsSomeForce();
    }

    public boolean gainsEstablishedForce() {

        return forceFormationAnalysis
                .gainsEstablishedForce();
    }

    /**
     * 종격 가능성을 5차에서
     * 추가 검토해야 하는지.
     */
    public boolean requiresFollowStructureReview() {

        return extremeStructureAnalysis
                .requiresFollowStructureReview();
    }

    public boolean completeChart() {

        return elementStrengthAnalysis
                .completeChart();
    }

    public boolean hourUnknown() {

        return elementStrengthAnalysis
                .hourUnknown();
    }
}