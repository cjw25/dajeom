package com.dajeom.rule;

import com.dajeom.model.DayMasterForceFormationAnalysis;
import com.dajeom.model.DayMasterGroundAnalysis;
import com.dajeom.model.DayMasterSeasonalAnalysis;
import com.dajeom.model.ExtremeStrengthStructureAnalysis;
import com.dajeom.type.DayMasterStrengthLevel;
import org.springframework.stereotype.Component;

/**
 * 정밀화 4차-8.
 *
 * 최종 신강·신약 판정 규칙.
 *
 * 다점 엔진 내부 기준이다.
 *
 * 이 수치는:
 *
 * - 확률
 * - 자연과학적 측정값
 * - 모든 명리 유파의 절대 공식
 *
 * 이 아니다.
 *
 * 신강·신약을 재현 가능한 방식으로
 * 계산하기 위한 명시적 정책이다.
 *
 * =================================================
 *
 * 기본 지수
 *
 * 4-5 지원력
 * VS
 * 4-6 압력
 *
 * 의 상대 비중을 사용한다.
 *
 * =================================================
 *
 * 구조 보정
 *
 * 4-2 득령
 * 4-3 득지
 * 4-4 득세
 *
 * 는 기존 오행 세력을 또 더하지 않고
 * 제한된 bp 보정만 적용한다.
 *
 * =================================================
 */
@Component
public class DayMasterStrengthRule {

    /**
     * 구조 보정 전체의 최대 범위.
     *
     * +/- 800bp
     *
     * 즉 최대 +/- 8%.
     *
     * 구조 Evidence가 이미 3차 세력에도
     * 일부 반영되어 있으므로
     * 과도한 중복 영향을 막기 위한 제한이다.
     */
    private static final int
            MAX_STRUCTURAL_ADJUSTMENT =
            800;

    /*
     * ------------------------------------------------
     * 득령 보정
     * ------------------------------------------------
     */

    private static final int
            DIRECT_COMMAND_GAIN_ADJUSTMENT =
            300;

    private static final int
            RESOURCE_SEASON_SUPPORT_ADJUSTMENT =
            150;

    private static final int
            OUTPUT_SEASON_DRAIN_ADJUSTMENT =
            -100;

    private static final int
            WEALTH_SEASON_EXERTION_ADJUSTMENT =
            -150;

    private static final int
            OFFICER_SEASON_CONTROL_ADJUSTMENT =
            -250;

    /*
     * ------------------------------------------------
     * 득지 보정
     * ------------------------------------------------
     */

    private static final int
            STRONG_CENTRAL_ROOT_ADJUSTMENT =
            400;

    private static final int
            CENTRAL_GROUND_ADJUSTMENT =
            250;

    private static final int
            GENERAL_GROUND_ADJUSTMENT =
            150;

    private static final int
            ROOTLESS_ADJUSTMENT =
            -250;

    /*
     * ------------------------------------------------
     * 득세 보정
     * ------------------------------------------------
     */

    private static final int
            WIDESPREAD_FORCE_ADJUSTMENT =
            300;

    private static final int
            ESTABLISHED_FORCE_ADJUSTMENT =
            200;

    private static final int
            LIMITED_FORCE_ADJUSTMENT =
            75;

    private static final int
            NO_FORCE_ADJUSTMENT =
            -100;

    /*
     * ------------------------------------------------
     * 최종 강약 구간
     * ------------------------------------------------
     *
     * 10000bp = 100%
     */

    private static final int
            EXTREME_STRONG_THRESHOLD =
            7_500;

    private static final int
            STRONG_THRESHOLD =
            6_500;

    private static final int
            BALANCED_STRONG_THRESHOLD =
            5_500;

    private static final int
            BALANCED_THRESHOLD =
            4_500;

    private static final int
            BALANCED_WEAK_THRESHOLD =
            3_500;

    private static final int
            EXTREME_WEAK_THRESHOLD =
            2_500;

    /**
     * 4-2 득령 구조 보정.
     *
     * 서로 배타적인 상태이므로
     * 중복 가산하지 않는다.
     */
    public int calculateSeasonalAdjustment(
            DayMasterSeasonalAnalysis seasonal
    ) {

        if (
                seasonal.directlyGainsCommand()
        ) {

            return DIRECT_COMMAND_GAIN_ADJUSTMENT;
        }

        if (
                seasonal.receivesResourceSupport()
        ) {

            return RESOURCE_SEASON_SUPPORT_ADJUSTMENT;
        }

        if (
                seasonal.drainedByOutput()
        ) {

            return OUTPUT_SEASON_DRAIN_ADJUSTMENT;
        }

        if (
                seasonal.expendsOnWealth()
        ) {

            return WEALTH_SEASON_EXERTION_ADJUSTMENT;
        }

        if (
                seasonal.controlledBySeason()
        ) {

            return OFFICER_SEASON_CONTROL_ADJUSTMENT;
        }

        return 0;
    }

    /**
     * 4-3 득지 구조 보정.
     *
     * 가장 강한 조건 하나만 적용한다.
     *
     * 따라서:
     *
     * 중심근 + 일반근
     *
     * 을 중복해서 더하지 않는다.
     */
    public int calculateGroundAdjustment(
            DayMasterGroundAnalysis ground
    ) {

        if (
                ground.strongCentralDirectRoot()
        ) {

            return STRONG_CENTRAL_ROOT_ADJUSTMENT;
        }

        if (
                ground.centralGrounded()
        ) {

            return CENTRAL_GROUND_ADJUSTMENT;
        }

        if (
                ground.grounded()
        ) {

            return GENERAL_GROUND_ADJUSTMENT;
        }

        if (
                ground.rootless()
        ) {

            return ROOTLESS_ADJUSTMENT;
        }

        return 0;
    }

    /**
     * 4-4 득세 구조 보정.
     *
     * 하나의 level만 적용한다.
     */
    public int calculateForceAdjustment(
            DayMasterForceFormationAnalysis formation
    ) {

        return switch (
                formation.level()
                ) {

            case WIDESPREAD ->
                    WIDESPREAD_FORCE_ADJUSTMENT;

            case ESTABLISHED ->
                    ESTABLISHED_FORCE_ADJUSTMENT;

            case LIMITED ->
                    LIMITED_FORCE_ADJUSTMENT;

            case NONE ->
                    NO_FORCE_ADJUSTMENT;
        };
    }

    /**
     * 세 구조 보정의 합계를 제한한다.
     *
     * 오행 세력 자체는 이미 3차에서:
     *
     * - 계절
     * - 통근
     * - 투간
     *
     * 을 반영했기 때문에
     *
     * 이 구조 보정이 두 번째 대규모
     * 세력 계산이 되지 않도록 제한한다.
     */
    public int calculateStructuralAdjustment(

            int seasonalAdjustment,

            int groundAdjustment,

            int forceAdjustment

    ) {

        int calculated =
                seasonalAdjustment
                        + groundAdjustment
                        + forceAdjustment;

        return Math.max(
                -MAX_STRUCTURAL_ADJUSTMENT,
                Math.min(
                        MAX_STRUCTURAL_ADJUSTMENT,
                        calculated
                )
        );
    }

    /**
     * 지원/압력 비중을 바탕으로
     * 구조 보정이 반영된 최종 지수 생성.
     */
    public int calculateAdjustedStrengthBasisPoints(

            int rawSupportShareBasisPoints,

            int structuralAdjustmentBasisPoints

    ) {

        return clampBasisPoints(
                rawSupportShareBasisPoints
                        + structuralAdjustmentBasisPoints
        );
    }

    /**
     * 최종 신강·신약 단계.
     *
     * 극단 구조 Candidate가 있다고 해서
     * 무조건 극강/극약으로 확정하지 않는다.
     *
     * 극단 Candidate
     * +
     * 최종 보정 지수
     *
     * 를 함께 확인한다.
     *
     * 종격 후보일 경우에는 이미
     * 훨씬 엄격한 4-7 조건을 통과했으므로
     * 극강 또는 극약 상태로 분류한다.
     *
     * 다만 종격 자체는 여전히 확정하지 않는다.
     */
    public DayMasterStrengthLevel determineLevel(

            int adjustedStrengthBasisPoints,

            ExtremeStrengthStructureAnalysis extreme

    ) {

        /*
         * 종강 후보.
         */
        if (
                extreme.followStrongCandidate()
        ) {

            return DayMasterStrengthLevel
                    .EXTREME_STRONG;
        }

        /*
         * 종약 후보.
         */
        if (
                extreme.followWeakCandidate()
        ) {

            return DayMasterStrengthLevel
                    .EXTREME_WEAK;
        }

        /*
         * 일반 극강 후보는
         * 최종 지수도 극강 범위여야 한다.
         */
        if (
                extreme.extremeStrongCandidate()
                        && adjustedStrengthBasisPoints
                        >= EXTREME_STRONG_THRESHOLD
        ) {

            return DayMasterStrengthLevel
                    .EXTREME_STRONG;
        }

        /*
         * 일반 극약 후보 역시
         * 최종 지수가 극약 범위여야 한다.
         */
        if (
                extreme.extremeWeakCandidate()
                        && adjustedStrengthBasisPoints
                        <= EXTREME_WEAK_THRESHOLD
        ) {

            return DayMasterStrengthLevel
                    .EXTREME_WEAK;
        }

        if (
                adjustedStrengthBasisPoints
                        >= STRONG_THRESHOLD
        ) {

            return DayMasterStrengthLevel
                    .STRONG;
        }

        if (
                adjustedStrengthBasisPoints
                        >= BALANCED_STRONG_THRESHOLD
        ) {

            return DayMasterStrengthLevel
                    .BALANCED_STRONG;
        }

        if (
                adjustedStrengthBasisPoints
                        >= BALANCED_THRESHOLD
        ) {

            return DayMasterStrengthLevel
                    .BALANCED;
        }

        if (
                adjustedStrengthBasisPoints
                        >= BALANCED_WEAK_THRESHOLD
        ) {

            return DayMasterStrengthLevel
                    .BALANCED_WEAK;
        }

        return DayMasterStrengthLevel
                .WEAK;
    }

    public boolean extremeOverrideApplied(

            DayMasterStrengthLevel level,

            ExtremeStrengthStructureAnalysis extreme

    ) {

        if (
                level.extremeStrong()
        ) {

            return extreme.extremeStrongCandidate()
                    || extreme.followStrongCandidate();
        }

        if (
                level.extremeWeak()
        ) {

            return extreme.extremeWeakCandidate()
                    || extreme.followWeakCandidate();
        }

        return false;
    }

    private int clampBasisPoints(
            int value
    ) {

        return Math.max(
                0,
                Math.min(
                        10_000,
                        value
                )
        );
    }
}