package com.dajeom.rule;

import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.ExtremeStrengthStructureType;
import com.dajeom.type.FollowWeakDirection;
import org.springframework.stereotype.Component;

/**
 * 정밀화 4차-7.
 *
 * 극강·극약 및 종격 후보를 걸러내는
 * 다점 내부 판단 규칙.
 *
 * 이 Rule의 임계치는:
 *
 * 과학적 확률
 * 또는
 * 모든 명리학 유파의 공통 공식
 *
 * 이 아니다.
 *
 * 다점 엔진 내부 정책이며
 * Git revision을 통해 관리한다.
 *
 * 여기서 종격을 확정하지 않는다.
 */
@Component
public class ExtremeStrengthStructureRule {

    /**
     * 지원 또는 압력이 전체 비교 세력의
     * 75% 이상이면 극단 후보 검토.
     */
    private static final int
            EXTREME_SHARE_THRESHOLD =
            7_500;

    /**
     * 지원 또는 압력이 전체 비교 세력의
     * 90% 이상이면 종격 후보까지 검토.
     */
    private static final int
            FOLLOW_SHARE_THRESHOLD =
            9_000;

    /**
     * 종약 후보 상태에서
     * 식상·재성·관살 가운데 하나가
     * 전체 압력의 55% 이상이면
     * 해당 방향이 주도한다고 본다.
     */
    private static final int
            DOMINANT_PRESSURE_THRESHOLD =
            5_500;

    /**
     * 극강 후보.
     *
     * 숫자만 강하다고 극강으로 보지 않는다.
     *
     * 지원 우세
     * +
     * 계절/지지/주변 세력 중
     * 실제 기반이 있어야 한다.
     */
    public boolean isExtremeStrongCandidate(

            int supportShareBasisPoints,

            boolean receivesSeasonalSupport,

            boolean grounded,

            boolean establishedForce

    ) {

        if (
                supportShareBasisPoints
                        < EXTREME_SHARE_THRESHOLD
        ) {

            return false;
        }

        return receivesSeasonalSupport

                || grounded

                || establishedForce;
    }

    /**
     * 극약 후보.
     *
     * 압력 비중이 크면서:
     *
     * - 직접 득령이 아니고
     * - 월지/일지 정기 직접근 같은
     *   강한 중심근도 없고
     * - 강한 득세까지 형성되지 않은
     *
     * 경우를 후보로 본다.
     */
    public boolean isExtremeWeakCandidate(

            int pressureShareBasisPoints,

            boolean directCommandGain,

            boolean strongCentralDirectRoot,

            boolean widespreadForce

    ) {

        if (
                pressureShareBasisPoints
                        < EXTREME_SHARE_THRESHOLD
        ) {

            return false;
        }

        return !directCommandGain

                && !strongCentralDirectRoot

                && !widespreadForce;
    }

    /**
     * 종강 후보.
     *
     * 단순 극강보다 훨씬 엄격하게 본다.
     *
     * - 지원 비중 90% 이상
     * - 직접 득령
     * - 중심 지지의 강한 직접근
     * - 실제 득세 구조
     *
     * 를 동시에 요구한다.
     *
     * 그래도 최종 종격 확정은 아니다.
     */
    public boolean isFollowStrongCandidate(

            boolean extremeStrongCandidate,

            int supportShareBasisPoints,

            boolean directCommandGain,

            boolean strongCentralDirectRoot,

            boolean establishedForce

    ) {

        return extremeStrongCandidate

                && supportShareBasisPoints
                >= FOLLOW_SHARE_THRESHOLD

                && directCommandGain

                && strongCentralDirectRoot

                && establishedForce;
    }

    /**
     * 종약 계열 후보.
     *
     * - 압력 90% 이상
     * - 계절적 지원 없음
     * - 완전 무근
     * - 외부 비겁 지원 없음
     * - 확립된 지원망 없음
     *
     * 정도의 극단적인 약세 구조에만
     * 후보 신호를 준다.
     */
    public boolean isFollowWeakCandidate(

            boolean extremeWeakCandidate,

            int pressureShareBasisPoints,

            boolean receivesSeasonalSupport,

            boolean rootless,

            boolean hasCompanionSupport,

            boolean establishedForce

    ) {

        return extremeWeakCandidate

                && pressureShareBasisPoints
                >= FOLLOW_SHARE_THRESHOLD

                && !receivesSeasonalSupport

                && rootless

                && !hasCompanionSupport

                && !establishedForce;
    }

    /**
     * 식상·재성·관살 중
     * 실제 압력의 주도 역할을 구한다.
     *
     * 명확한 55% 이상 독점이 없으면
     * 종약 혼합 방향으로 본다.
     */
    public FollowWeakDirection determineFollowWeakDirection(

            boolean followWeakCandidate,

            int outputPower,

            int wealthPower,

            int officerPower,

            int totalPressurePower

    ) {

        if (
                !followWeakCandidate
        ) {

            return FollowWeakDirection.NONE;
        }

        if (
                totalPressurePower <= 0
        ) {

            return FollowWeakDirection.MIXED;
        }

        DayMasterElementRole dominant =
                determineDominantPressureRole(
                        outputPower,
                        wealthPower,
                        officerPower
                );

        if (
                dominant == null
        ) {

            return FollowWeakDirection.MIXED;
        }

        int dominantPower =
                switch (
                        dominant
                        ) {

                    case OUTPUT ->
                            outputPower;

                    case WEALTH ->
                            wealthPower;

                    case OFFICER ->
                            officerPower;

                    default ->
                            0;
                };

        int share =
                calculateShareBasisPoints(
                        dominantPower,
                        totalPressurePower
                );

        if (
                share
                        < DOMINANT_PRESSURE_THRESHOLD
        ) {

            return FollowWeakDirection.MIXED;
        }

        return switch (
                dominant
                ) {

            case OUTPUT ->
                    FollowWeakDirection.OUTPUT;

            case WEALTH ->
                    FollowWeakDirection.WEALTH;

            case OFFICER ->
                    FollowWeakDirection.OFFICER;

            default ->
                    FollowWeakDirection.MIXED;
        };
    }

    /**
     * 세 압력 가운데 단독 최댓값.
     *
     * 동률이면 null.
     */
    public DayMasterElementRole
    determineDominantPressureRole(

            int outputPower,

            int wealthPower,

            int officerPower

    ) {

        int max =
                Math.max(
                        outputPower,
                        Math.max(
                                wealthPower,
                                officerPower
                        )
                );

        if (
                max <= 0
        ) {

            return null;
        }

        int maxCount =
                0;

        DayMasterElementRole result =
                null;

        if (
                outputPower == max
        ) {

            maxCount++;

            result =
                    DayMasterElementRole.OUTPUT;
        }

        if (
                wealthPower == max
        ) {

            maxCount++;

            result =
                    DayMasterElementRole.WEALTH;
        }

        if (
                officerPower == max
        ) {

            maxCount++;

            result =
                    DayMasterElementRole.OFFICER;
        }

        return maxCount == 1
                ? result
                : null;
    }

    public int calculateSupportShareBasisPoints(

            int totalSupportPower,

            int totalPressurePower

    ) {

        int total =
                totalSupportPower
                        + totalPressurePower;

        return calculateShareBasisPoints(
                totalSupportPower,
                total
        );
    }

    public int calculatePressureShareBasisPoints(

            int totalSupportPower,

            int totalPressurePower

    ) {

        int total =
                totalSupportPower
                        + totalPressurePower;

        if (
                total <= 0
        ) {

            return 0;
        }

        int supportShare =
                calculateSupportShareBasisPoints(
                        totalSupportPower,
                        totalPressurePower
                );

        return 10_000
                - supportShare;
    }

    public int calculateDominantPressureShareBasisPoints(

            DayMasterElementRole dominantRole,

            int outputPower,

            int wealthPower,

            int officerPower,

            int totalPressurePower

    ) {

        if (
                dominantRole == null
                        || totalPressurePower <= 0
        ) {

            return 0;
        }

        int dominantPower =
                switch (
                        dominantRole
                        ) {

                    case OUTPUT ->
                            outputPower;

                    case WEALTH ->
                            wealthPower;

                    case OFFICER ->
                            officerPower;

                    default ->
                            0;
                };

        return calculateShareBasisPoints(
                dominantPower,
                totalPressurePower
        );
    }

    public ExtremeStrengthStructureType
    determinePrimaryType(

            boolean extremeStrongCandidate,

            boolean extremeWeakCandidate,

            boolean followStrongCandidate,

            boolean followWeakCandidate

    ) {

        /*
         * 더 특수한 후보를 먼저 반환한다.
         */
        if (
                followStrongCandidate
        ) {

            return ExtremeStrengthStructureType
                    .FOLLOW_STRONG_CANDIDATE;
        }

        if (
                followWeakCandidate
        ) {

            return ExtremeStrengthStructureType
                    .FOLLOW_WEAK_CANDIDATE;
        }

        if (
                extremeStrongCandidate
        ) {

            return ExtremeStrengthStructureType
                    .EXTREME_STRONG_CANDIDATE;
        }

        if (
                extremeWeakCandidate
        ) {

            return ExtremeStrengthStructureType
                    .EXTREME_WEAK_CANDIDATE;
        }

        return ExtremeStrengthStructureType
                .NORMAL;
    }

    private int calculateShareBasisPoints(

            int part,

            int total

    ) {

        if (
                part <= 0
                        || total <= 0
        ) {

            return 0;
        }

        return Math.max(
                0,
                Math.min(
                        10_000,
                        (int) Math.round(
                                part
                                        * 10_000.0
                                        / total
                        )
                )
        );
    }
}