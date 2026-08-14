package com.dajeom.rule;

import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PassageBalanceState;
import com.dajeom.type.PassageBridgeState;
import com.dajeom.type.PassageConflictType;
import com.dajeom.type.PassageNeedLevel;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 6차-6.
 *
 * 상극 관계의 실제 통관 필요성을 판정한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 상극하는 두 오행이 존재한다는 이유만으로
 *    통관을 요구하지 않는다.
 *
 * 2.
 * 양쪽 모두 일정 세력을 가져야 한다.
 *
 * 3.
 * 양쪽 세력이 비슷할수록
 *    직접 충돌 가능성을 더 중요하게 본다.
 *
 * 4.
 * 중간 생조 오행이 이미 충분하면
 *    통관 필요도를 낮춘다.
 *
 * 5.
 * 일간이 상극 관계 안에 있고
 *    신약 상태에서 상대편에 밀리는 경우
 *    통관 필요도를 한 단계 강화할 수 있다.
 *
 * 6.
 * 6-5 약 후보와 통관 방향이 같다는 이유만으로
 *    현재 통관 문제가 해결됐다고 보지 않는다.
 *
 * 6-5 후보는 6-7 우선순위에 활용한다.
 */
@Component
public class PassageNeedRule {

    /**
     * 상극 대립으로 인정하기 위한
     * 약한 쪽의 최소 오행 비중.
     */
    public static final int
            MIN_ACTIVE_SIDE_SHARE =
            1_200;

    /**
     * 양쪽 세력 차이.
     */
    private static final int
            BALANCED_GAP_THRESHOLD =
            500;

    private static final int
            COMPETITIVE_GAP_THRESHOLD =
            1_200;

    private static final int
            UNEVEN_GAP_THRESHOLD =
            2_200;

    /**
     * 통관 오행 현재 확보 수준.
     */
    private static final int
            BRIDGE_ABSENT_THRESHOLD =
            300;

    private static final int
            BRIDGE_WEAK_THRESHOLD =
            1_000;

    private static final int
            BRIDGE_AVAILABLE_THRESHOLD =
            1_800;

    /**
     * 통관 기본 필요도 산정에 사용하는
     * 대립 최소세력.
     */
    private static final int
            MODERATE_CONFLICT_FLOOR =
            1_500;

    private static final int
            HIGH_CONFLICT_FLOOR =
            2_000;

    private static final int
            VERY_HIGH_CONFLICT_FLOOR =
            2_800;

    public PassageBalanceState
    determineBalanceState(

            int controllerShareBasisPoints,

            int controlledShareBasisPoints

    ) {

        validateShare(
                controllerShareBasisPoints
        );

        validateShare(
                controlledShareBasisPoints
        );

        int gap =
                Math.abs(
                        controllerShareBasisPoints
                                - controlledShareBasisPoints
                );

        if (
                gap <= BALANCED_GAP_THRESHOLD
        ) {

            return PassageBalanceState
                    .BALANCED;
        }

        if (
                gap <= COMPETITIVE_GAP_THRESHOLD
        ) {

            return PassageBalanceState
                    .COMPETITIVE;
        }

        if (
                gap <= UNEVEN_GAP_THRESHOLD
        ) {

            return PassageBalanceState
                    .UNEVEN;
        }

        return PassageBalanceState
                .ONE_SIDED;
    }

    public PassageBridgeState
    determineBridgeState(
            int bridgeShareBasisPoints
    ) {

        validateShare(
                bridgeShareBasisPoints
        );

        if (
                bridgeShareBasisPoints
                        < BRIDGE_ABSENT_THRESHOLD
        ) {

            return PassageBridgeState
                    .ABSENT;
        }

        if (
                bridgeShareBasisPoints
                        < BRIDGE_WEAK_THRESHOLD
        ) {

            return PassageBridgeState
                    .WEAK;
        }

        if (
                bridgeShareBasisPoints
                        < BRIDGE_AVAILABLE_THRESHOLD
        ) {

            return PassageBridgeState
                    .AVAILABLE;
        }

        return PassageBridgeState
                .STRONG;
    }

    /**
     * 통관 필요도.
     */
    public PassageNeedLevel determineNeedLevel(

            PassageConflictType conflictType,

            int controllerShareBasisPoints,

            int controlledShareBasisPoints,

            PassageBalanceState balanceState,

            PassageBridgeState bridgeState,

            FiveElement dayMasterElement,

            DayMasterStrengthLevel strengthLevel

    ) {

        Objects.requireNonNull(
                conflictType,
                "상극 관계 유형이 필요합니다."
        );

        Objects.requireNonNull(
                balanceState,
                "상극 균형 상태가 필요합니다."
        );

        Objects.requireNonNull(
                bridgeState,
                "통관 오행 상태가 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 단계가 필요합니다."
        );

        validateShare(
                controllerShareBasisPoints
        );

        validateShare(
                controlledShareBasisPoints
        );

        int floor =
                Math.min(
                        controllerShareBasisPoints,
                        controlledShareBasisPoints
                );

        /*
         * 둘 중 한쪽이 너무 약하면
         * "두 강한 세력이 서로 막고 있다"는
         * 통관 구조로 적극 판정하지 않는다.
         */
        if (
                floor < MIN_ACTIVE_SIDE_SHARE
        ) {

            return PassageNeedLevel
                    .NONE;
        }

        PassageNeedLevel base =
                determineBaseConflictNeed(
                        floor,
                        balanceState
                );

        PassageNeedLevel adjusted =
                applyBridgeAdjustment(
                        base,
                        bridgeState
                );

        boolean dayMasterDisadvantaged =
                determineDayMasterDisadvantaged(
                        conflictType,
                        controllerShareBasisPoints,
                        controlledShareBasisPoints,
                        dayMasterElement,
                        strengthLevel
                );

        if (
                dayMasterDisadvantaged
        ) {

            adjusted =
                    increaseOneLevel(
                            adjusted
                    );
        }

        return adjusted;
    }

    /**
     * 일간이 해당 상극 관계에서
     * 실제로 불리한 쪽에 있는지.
     *
     * 조건:
     *
     * 1.
     * 일간 오행이 해당 관계 안에 있음.
     *
     * 2.
     * 일간이 신약측.
     *
     * 3.
     * 반대 오행이 일간 오행보다
     *    최소 500bp 이상 강함.
     *
     * 신강 일간은 단순히 상극 상대가 있다고
     * 불리하다고 판정하지 않는다.
     */
    public boolean determineDayMasterDisadvantaged(

            PassageConflictType conflictType,

            int controllerShareBasisPoints,

            int controlledShareBasisPoints,

            FiveElement dayMasterElement,

            DayMasterStrengthLevel strengthLevel

    ) {

        Objects.requireNonNull(
                conflictType,
                "상극 관계 유형이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 단계가 필요합니다."
        );

        if (
                !conflictType.involves(
                        dayMasterElement
                )
        ) {

            return false;
        }

        if (
                !weakSide(
                        strengthLevel
                )
        ) {

            return false;
        }

        int dayMasterSideShare;

        int opposingShare;

        if (
                conflictType.controllerIs(
                        dayMasterElement
                )
        ) {

            dayMasterSideShare =
                    controllerShareBasisPoints;

            opposingShare =
                    controlledShareBasisPoints;

        } else {

            dayMasterSideShare =
                    controlledShareBasisPoints;

            opposingShare =
                    controllerShareBasisPoints;
        }

        return opposingShare
                >= dayMasterSideShare + 500;
    }

    private PassageNeedLevel
    determineBaseConflictNeed(

            int conflictFloorBasisPoints,

            PassageBalanceState balanceState

    ) {

        /*
         * 강한 양쪽 세력이
         * 팽팽하게 충돌.
         */
        if (
                conflictFloorBasisPoints
                        >= VERY_HIGH_CONFLICT_FLOOR

                        && balanceState
                        .stronglyOpposed()
        ) {

            return PassageNeedLevel
                    .HIGH;
        }

        /*
         * 양쪽 모두 20% 이상 수준이고
         * 한쪽 독주가 아니라면
         * 의미 있는 통관 필요.
         */
        if (
                conflictFloorBasisPoints
                        >= HIGH_CONFLICT_FLOOR

                        && !balanceState
                        .oneSided()
        ) {

            return PassageNeedLevel
                    .MODERATE;
        }

        /*
         * 15% 이상 양쪽이 살아 있고
         * 팽팽하다면 MODERATE.
         */
        if (
                conflictFloorBasisPoints
                        >= MODERATE_CONFLICT_FLOOR

                        && balanceState
                        .stronglyOpposed()
        ) {

            return PassageNeedLevel
                    .MODERATE;
        }

        return PassageNeedLevel
                .LOW;
    }

    private PassageNeedLevel
    applyBridgeAdjustment(

            PassageNeedLevel base,

            PassageBridgeState bridgeState

    ) {

        if (
                base.none()
        ) {

            return base;
        }

        /*
         * 중간 오행이 거의 없으면
         * 직접 충돌을 완화할 흐름이 없으므로
         * 한 단계 강화.
         */
        if (
                bridgeState.absent()
        ) {

            return increaseOneLevel(
                    base
            );
        }

        /*
         * 약한 bridge는
         * 존재는 하지만 충분히 작동한다고
         * 보기 어려우므로 그대로 유지.
         */
        if (
                bridgeState.weak()
        ) {

            return base;
        }

        /*
         * 어느 정도 bridge가 있으면
         * 한 단계 완화.
         */
        if (
                bridgeState.available()
        ) {

            return decreaseOneLevel(
                    base
            );
        }

        /*
         * 충분한 bridge가 있으면
         * 두 단계 완화.
         */
        return decreaseOneLevel(
                decreaseOneLevel(
                        base
                )
        );
    }

    private PassageNeedLevel increaseOneLevel(
            PassageNeedLevel level
    ) {

        return switch (
                level
                ) {

            case NONE ->
                    PassageNeedLevel.LOW;

            case LOW ->
                    PassageNeedLevel.MODERATE;

            case MODERATE ->
                    PassageNeedLevel.HIGH;

            case HIGH,
                 CRITICAL ->
                    PassageNeedLevel.CRITICAL;
        };
    }

    private PassageNeedLevel decreaseOneLevel(
            PassageNeedLevel level
    ) {

        return switch (
                level
                ) {

            case NONE,
                 LOW ->
                    PassageNeedLevel.NONE;

            case MODERATE ->
                    PassageNeedLevel.LOW;

            case HIGH ->
                    PassageNeedLevel.MODERATE;

            case CRITICAL ->
                    PassageNeedLevel.HIGH;
        };
    }

    private boolean weakSide(
            DayMasterStrengthLevel level
    ) {

        return level
                == DayMasterStrengthLevel.BALANCED_WEAK

                || level
                == DayMasterStrengthLevel.WEAK

                || level
                == DayMasterStrengthLevel.EXTREME_WEAK;
    }

    private void validateShare(
            int value
    ) {

        if (
                value < 0
                        || value > 10_000
        ) {

            throw new IllegalArgumentException(
                    "오행 비중은 0~10000bp 사이여야 합니다."
            );
        }
    }
}