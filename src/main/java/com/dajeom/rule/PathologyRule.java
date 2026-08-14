package com.dajeom.rule;

import com.dajeom.model.StructureStatusEvidence;
import com.dajeom.type.ClimateAdjustmentNeedLevel;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.PathologySeverity;
import com.dajeom.type.StructureStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 6차-4.
 *
 * 병(病) 구조 탐지 Rule.
 *
 * 모든 threshold는 다점 엔진의
 * 내부 구조 판단 heuristic이다.
 *
 * 과학적·의학적 기준이 아니다.
 *
 * =================================================
 *
 * 주요 탐지:
 *
 * 1. 특정 오행 과도 집중
 *
 * 2. 신약 일간의:
 *    - 식상 설기
 *    - 재성 부담
 *    - 관살 압박
 *
 * 3. 신강 일간의:
 *    - 비겁·인성 생조 과다
 *
 * 4. 극단 한난조습
 *
 * 5. 조후 보정 충돌
 *
 * 6. 주격 파격/불안정
 */
@Component
public class PathologyRule {

    /*
     * =================================================
     * 오행 과다집중
     * =================================================
     */

    public static final int
            ELEMENT_CONCENTRATION_THRESHOLD =
            3_500;

    private static final int
            ELEMENT_CONCENTRATION_HIGH_THRESHOLD =
            4_500;

    private static final int
            ELEMENT_CONCENTRATION_CRITICAL_THRESHOLD =
            5_500;

    /*
     * =================================================
     * 신약 일간 압박
     * =================================================
     *
     * 식상/재성/관살은
     * 4-6의 상대 작용 비율과 동일한 방향으로
     * 유효 부담값을 만든다.
     *
     * 식상 80%
     * 재성 70%
     * 관살 100%
     *
     * 4차 세력 자체를 다시 계산하는 것이 아니라
     * 6차 병 구조 탐지용 비교값이다.
     */

    public static final int
            DAY_MASTER_PRESSURE_THRESHOLD =
            1_200;

    private static final int
            DAY_MASTER_PRESSURE_HIGH_THRESHOLD =
            2_000;

    private static final int
            DAY_MASTER_PRESSURE_CRITICAL_THRESHOLD =
            3_000;

    /*
     * =================================================
     * 신강 일간 생조 과다
     * =================================================
     */

    public static final int
            SUPPORT_OVERCONCENTRATION_THRESHOLD =
            5_000;

    private static final int
            SUPPORT_OVERCONCENTRATION_HIGH_THRESHOLD =
            6_500;

    private static final int
            SUPPORT_OVERCONCENTRATION_CRITICAL_THRESHOLD =
            7_500;

    /**
     * 특정 오행 과다 집중.
     */
    public PathologySeverity
    determineElementConcentrationSeverity(
            int shareBasisPoints
    ) {

        validateShare(
                shareBasisPoints
        );

        if (
                shareBasisPoints
                        < ELEMENT_CONCENTRATION_THRESHOLD
        ) {

            return PathologySeverity
                    .NONE;
        }

        if (
                shareBasisPoints
                        < ELEMENT_CONCENTRATION_HIGH_THRESHOLD
        ) {

            return PathologySeverity
                    .MODERATE;
        }

        if (
                shareBasisPoints
                        < ELEMENT_CONCENTRATION_CRITICAL_THRESHOLD
        ) {

            return PathologySeverity
                    .HIGH;
        }

        return PathologySeverity
                .CRITICAL;
    }

    /**
     * 식상·재성·관살의 실제 오행 비중을
     * 병 구조 비교용 유효 부담값으로 변환한다.
     *
     * 이 값은 에너지 보존량이 아니며
     * 확률이나 실제 힘의 절대량도 아니다.
     */
    public int calculateEffectivePressureBasisPoints(

            DayMasterElementRole role,

            int elementShareBasisPoints

    ) {

        Objects.requireNonNull(
                role,
                "일간 기준 오행 역할이 필요합니다."
        );

        validateShare(
                elementShareBasisPoints
        );

        return switch (
                role
                ) {

            case OUTPUT ->
                    elementShareBasisPoints
                            * 80
                            / 100;

            case WEALTH ->
                    elementShareBasisPoints
                            * 70
                            / 100;

            case OFFICER ->
                    elementShareBasisPoints;

            default ->
                    throw new IllegalArgumentException(
                            "압박 계산은 식상·재성·관살에만 적용할 수 있습니다: "
                                    + role
                    );
        };
    }

    /**
     * 신약 일간의 식상·재성·관살 부담.
     *
     * 신강·중화 측에서는 이 패턴을
     * 병 구조로 만들지 않는다.
     */
    public PathologySeverity
    determineWeakDayMasterPressureSeverity(

            DayMasterStrengthLevel strengthLevel,

            int effectivePressureBasisPoints

    ) {

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 단계가 필요합니다."
        );

        validateShare(
                effectivePressureBasisPoints
        );

        if (
                !strengthLevel.weakSide()
        ) {

            return PathologySeverity
                    .NONE;
        }

        if (
                effectivePressureBasisPoints
                        < DAY_MASTER_PRESSURE_THRESHOLD
        ) {

            return PathologySeverity
                    .NONE;
        }

        boolean extremeWeak =
                strengthLevel
                        == DayMasterStrengthLevel
                        .EXTREME_WEAK;

        boolean weak =
                strengthLevel
                        == DayMasterStrengthLevel
                        .WEAK;

        /*
         * 극약 일간에서
         * 이미 큰 압력이 존재하면
         * 한 단계 더 엄격하게 본다.
         */
        if (
                extremeWeak
                        && effectivePressureBasisPoints
                        >= DAY_MASTER_PRESSURE_HIGH_THRESHOLD
        ) {

            return PathologySeverity
                    .CRITICAL;
        }

        if (
                effectivePressureBasisPoints
                        >= DAY_MASTER_PRESSURE_CRITICAL_THRESHOLD
        ) {

            return PathologySeverity
                    .CRITICAL;
        }

        if (
                effectivePressureBasisPoints
                        >= DAY_MASTER_PRESSURE_HIGH_THRESHOLD
        ) {

            return PathologySeverity
                    .HIGH;
        }

        if (
                extremeWeak
                        || weak
        ) {

            return PathologySeverity
                    .HIGH;
        }

        return PathologySeverity
                .MODERATE;
    }

    /**
     * 강한 일간에
     * 비겁 + 인성이 과도하게 집중됐는지.
     */
    public PathologySeverity
    determineSupportOverconcentrationSeverity(

            DayMasterStrengthLevel strengthLevel,

            int combinedSupportShareBasisPoints

    ) {

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 단계가 필요합니다."
        );

        validateShare(
                combinedSupportShareBasisPoints
        );

        if (
                !strengthLevel.strongSide()
        ) {

            return PathologySeverity
                    .NONE;
        }

        if (
                combinedSupportShareBasisPoints
                        < SUPPORT_OVERCONCENTRATION_THRESHOLD
        ) {

            return PathologySeverity
                    .NONE;
        }

        boolean extremeStrong =
                strengthLevel
                        == DayMasterStrengthLevel
                        .EXTREME_STRONG;

        if (
                combinedSupportShareBasisPoints
                        >= SUPPORT_OVERCONCENTRATION_CRITICAL_THRESHOLD
        ) {

            return PathologySeverity
                    .CRITICAL;
        }

        if (
                extremeStrong
                        && combinedSupportShareBasisPoints
                        >= SUPPORT_OVERCONCENTRATION_HIGH_THRESHOLD
        ) {

            return PathologySeverity
                    .CRITICAL;
        }

        if (
                combinedSupportShareBasisPoints
                        >= SUPPORT_OVERCONCENTRATION_HIGH_THRESHOLD
        ) {

            return PathologySeverity
                    .HIGH;
        }

        return PathologySeverity
                .MODERATE;
    }

    /**
     * 극단 한난·조습의 병 구조 심각도.
     *
     * LOW/MODERATE 수준의 조후 불균형은
     * 아직 "극단 병 구조"로 만들지 않는다.
     */
    public PathologySeverity
    determineExtremeClimateSeverity(
            ClimateAdjustmentNeedLevel need
    ) {

        Objects.requireNonNull(
                need,
                "조후 필요도가 필요합니다."
        );

        if (
                need.critical()
        ) {

            return PathologySeverity
                    .CRITICAL;
        }

        if (
                need.high()
        ) {

            return PathologySeverity
                    .HIGH;
        }

        return PathologySeverity
                .NONE;
    }

    /**
     * 寒燥 / 熱濕처럼
     * 두 조후 축의 보정 방향이 경쟁하는 구조.
     */
    public PathologySeverity
    determineClimateCorrectionConflictSeverity(

            boolean correctionConflict,

            ClimateAdjustmentNeedLevel overallNeed

    ) {

        Objects.requireNonNull(
                overallNeed,
                "전체 조후 필요도가 필요합니다."
        );

        if (
                !correctionConflict
        ) {

            return PathologySeverity
                    .NONE;
        }

        if (
                overallNeed.critical()
        ) {

            return PathologySeverity
                    .HIGH;
        }

        if (
                overallNeed.high()
        ) {

            return PathologySeverity
                    .HIGH;
        }

        if (
                overallNeed.meaningful()
        ) {

            return PathologySeverity
                    .MODERATE;
        }

        return PathologySeverity
                .LOW;
    }

    /**
     * 5차 주격의 파격 손상.
     */
    public PathologySeverity
    determinePrimaryStructureDamageSeverity(
            StructureStatusEvidence statusEvidence
    ) {

        Objects.requireNonNull(
                statusEvidence,
                "격국 상태 Evidence가 필요합니다."
        );

        if (
                !statusEvidence.standardPrimary()
        ) {

            return PathologySeverity
                    .NONE;
        }

        if (
                statusEvidence.strongDamage()
        ) {

            return PathologySeverity
                    .HIGH;
        }

        if (
                statusEvidence.meaningfulDamage()
        ) {

            return PathologySeverity
                    .MODERATE;
        }

        return PathologySeverity
                .NONE;
    }

    /**
     * 주격 자체가 UNSTABLE인지.
     *
     * SPECIAL_REVIEW는
     * 특수격이라는 이유만으로 병으로 보지 않는다.
     */
    public PathologySeverity
    determinePrimaryStructureInstabilitySeverity(
            StructureStatus status
    ) {

        Objects.requireNonNull(
                status,
                "격국 상태가 필요합니다."
        );

        if (
                status.unstable()
        ) {

            return PathologySeverity
                    .HIGH;
        }

        return PathologySeverity
                .NONE;
    }

    private void validateShare(
            int value
    ) {

        if (
                value < 0
                        || value > 10_000
        ) {

            throw new IllegalArgumentException(
                    "비교 지수는 0~10000bp 사이여야 합니다."
            );
        }
    }
}