package com.dajeom.analyzer;

import com.dajeom.model.DayMasterForceFormationAnalysis;
import com.dajeom.model.DayMasterGroundAnalysis;
import com.dajeom.model.DayMasterPressurePowerAnalysis;
import com.dajeom.model.DayMasterSeasonalAnalysis;
import com.dajeom.model.DayMasterSupportPowerAnalysis;
import com.dajeom.model.ExtremeStrengthStructureAnalysis;
import com.dajeom.model.ExtremeStrengthStructureEvidence;
import com.dajeom.rule.ExtremeStrengthStructureRule;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.ExtremeStrengthStructureType;
import com.dajeom.type.FollowWeakDirection;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 4차-7.
 *
 * 일반적인 신강·신약 판정 전에
 * 극단적인 구조가 존재하는지 검토한다.
 *
 * 사용하는 기존 분석:
 *
 * 4-2 득령
 * 4-3 득지
 * 4-4 득세
 * 4-5 인성·비겁 지원
 * 4-6 식상·재성·관살 압력
 *
 * 중요한 원칙:
 *
 * 여기서는 종격을 확정하지 않는다.
 *
 * 종강/종약 등은 Candidate 신호만 만든다.
 *
 * 실제 격국은 정밀화 5차에서
 * 별도로 판정한다.
 */
@Component
public class ExtremeStrengthStructureAnalyzer {

    private final ExtremeStrengthStructureRule
            extremeStrengthStructureRule;

    public ExtremeStrengthStructureAnalyzer(
            ExtremeStrengthStructureRule
                    extremeStrengthStructureRule
    ) {

        this.extremeStrengthStructureRule =
                Objects.requireNonNull(
                        extremeStrengthStructureRule,
                        "ExtremeStrengthStructureRule이 필요합니다."
                );
    }

    public ExtremeStrengthStructureAnalysis analyze(

            DayMasterSeasonalAnalysis seasonal,

            DayMasterGroundAnalysis ground,

            DayMasterForceFormationAnalysis formation,

            DayMasterSupportPowerAnalysis support,

            DayMasterPressurePowerAnalysis pressure

    ) {

        Objects.requireNonNull(
                seasonal,
                "득령 분석이 필요합니다."
        );

        Objects.requireNonNull(
                ground,
                "득지 분석이 필요합니다."
        );

        Objects.requireNonNull(
                formation,
                "득세 분석이 필요합니다."
        );

        Objects.requireNonNull(
                support,
                "지원 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                pressure,
                "압력 세력 분석이 필요합니다."
        );

        validateConsistency(
                seasonal,
                ground,
                formation,
                support,
                pressure
        );

        int totalSupportPower =
                support.totalSupportPower();

        int totalPressurePower =
                pressure.totalPressurePower();

        int supportShareBasisPoints =
                extremeStrengthStructureRule
                        .calculateSupportShareBasisPoints(
                                totalSupportPower,
                                totalPressurePower
                        );

        int pressureShareBasisPoints =
                extremeStrengthStructureRule
                        .calculatePressureShareBasisPoints(
                                totalSupportPower,
                                totalPressurePower
                        );

        boolean extremeStrongCandidate =
                extremeStrengthStructureRule
                        .isExtremeStrongCandidate(
                                supportShareBasisPoints,
                                seasonal.receivesSeasonalSupport(),
                                ground.grounded(),
                                formation.gainsEstablishedForce()
                        );

        boolean extremeWeakCandidate =
                extremeStrengthStructureRule
                        .isExtremeWeakCandidate(
                                pressureShareBasisPoints,
                                seasonal.directlyGainsCommand(),
                                ground.strongCentralDirectRoot(),
                                formation.gainsWidespreadForce()
                        );

        boolean followStrongCandidate =
                extremeStrengthStructureRule
                        .isFollowStrongCandidate(
                                extremeStrongCandidate,
                                supportShareBasisPoints,
                                seasonal.directlyGainsCommand(),
                                ground.strongCentralDirectRoot(),
                                formation.gainsEstablishedForce()
                        );

        boolean followWeakCandidate =
                extremeStrengthStructureRule
                        .isFollowWeakCandidate(
                                extremeWeakCandidate,
                                pressureShareBasisPoints,
                                seasonal.receivesSeasonalSupport(),
                                ground.rootless(),
                                support.hasCompanionSupport(),
                                formation.gainsEstablishedForce()
                        );

        DayMasterElementRole dominantPressureRole =
                extremeStrengthStructureRule
                        .determineDominantPressureRole(
                                pressure.outputDrainPower(),
                                pressure.wealthExertionPower(),
                                pressure.officerControlPower()
                        );

        int dominantPressureShareBasisPoints =
                extremeStrengthStructureRule
                        .calculateDominantPressureShareBasisPoints(
                                dominantPressureRole,
                                pressure.outputDrainPower(),
                                pressure.wealthExertionPower(),
                                pressure.officerControlPower(),
                                totalPressurePower
                        );

        FollowWeakDirection followWeakDirection =
                extremeStrengthStructureRule
                        .determineFollowWeakDirection(
                                followWeakCandidate,
                                pressure.outputDrainPower(),
                                pressure.wealthExertionPower(),
                                pressure.officerControlPower(),
                                totalPressurePower
                        );

        ExtremeStrengthStructureType primaryType =
                extremeStrengthStructureRule
                        .determinePrimaryType(
                                extremeStrongCandidate,
                                extremeWeakCandidate,
                                followStrongCandidate,
                                followWeakCandidate
                        );

        ExtremeStrengthStructureEvidence evidence =
                new ExtremeStrengthStructureEvidence(
                        seasonal.dayMaster(),
                        seasonal.dayMasterElement(),
                        seasonal.directlyGainsCommand(),
                        seasonal.receivesSeasonalSupport(),
                        ground.rootless(),
                        ground.centralGrounded(),
                        ground.strongCentralDirectRoot(),
                        formation.gainsEstablishedForce(),
                        formation.gainsWidespreadForce(),
                        support.companionSupportPower(),
                        support.resourceSupportPower(),
                        totalSupportPower,
                        pressure.outputDrainPower(),
                        pressure.wealthExertionPower(),
                        pressure.officerControlPower(),
                        totalPressurePower,
                        supportShareBasisPoints,
                        pressureShareBasisPoints,
                        dominantPressureRole,
                        dominantPressureShareBasisPoints
                );

        return new ExtremeStrengthStructureAnalysis(
                primaryType,
                extremeStrongCandidate,
                extremeWeakCandidate,
                followStrongCandidate,
                followWeakCandidate,
                followWeakDirection,
                evidence
        );
    }

    private void validateConsistency(

            DayMasterSeasonalAnalysis seasonal,

            DayMasterGroundAnalysis ground,

            DayMasterForceFormationAnalysis formation,

            DayMasterSupportPowerAnalysis support,

            DayMasterPressurePowerAnalysis pressure

    ) {

        if (
                seasonal.dayMaster()
                        != ground.dayMaster()

                        || seasonal.dayMaster()
                        != formation.dayMaster()

                        || seasonal.dayMaster()
                        != support.dayMaster()

                        || seasonal.dayMaster()
                        != pressure.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "4차 분석 결과들의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                seasonal.dayMasterElement()
                        != ground.dayMasterElement()

                        || seasonal.dayMasterElement()
                        != formation.dayMasterElement()

                        || seasonal.dayMasterElement()
                        != support.dayMasterElement()

                        || seasonal.dayMasterElement()
                        != pressure.dayMasterElement()
        ) {

            throw new IllegalArgumentException(
                    "4차 분석 결과들의 일간 오행이 서로 일치하지 않습니다."
            );
        }
    }
}