package com.dajeom.rule;

import com.dajeom.model.TenGodPowerSummary;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.WealthCompositePatternLevel;
import com.dajeom.type.WealthCompositePatternType;
import com.dajeom.type.WealthCompositeReasonType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-4.
 *
 * 재성 복합구조 Rule.
 *
 * =================================================
 *
 * 숫자 총점 방식이 아니다.
 *
 * 각 Pattern마다 독립적으로
 * 구조 조건을 판정한다.
 *
 * =================================================
 *
 * 재성 family share:
 *
 * 15% 이상 -> 의미 있는 재성
 * 25% 이상 -> 강한 재성
 *
 * =================================================
 *
 * 정재/편재 중심:
 *
 * 양쪽 모두 존재할 경우
 * 한쪽 세력이 상대의 125% 이상이며
 * 자체 세력도 의미 있어야
 * STRONG 중심 Pattern으로 본다.
 *
 * =================================================
 *
 * 비겁 압박:
 *
 * 비겁 세력이 재성 세력의
 * 80% 이상에 접근하면
 * 유의미한 압박 후보로 본다.
 *
 * =================================================
 *
 * 주의:
 *
 * 여기서 COMPANION_PRESSURED_WEALTH가
 * 성립해도 고전적 "비겁쟁재"를
 * 즉시 확정하지 않는다.
 */
@Component
public class WealthCompositeRule {

    public static final int
            MEANINGFUL_WEALTH_SHARE_BASIS_POINTS =
            1_500;

    public static final int
            STRONG_WEALTH_SHARE_BASIS_POINTS =
            2_500;

    public static final int
            CENTER_DOMINANCE_PERCENT =
            125;

    public static final int
            COMPANION_PRESSURE_APPROACH_PERCENT =
            80;

    /*
     * =================================================
     * 일간 강약
     * =================================================
     */

    public boolean weakSide(
            DayMasterStrengthLevel level
    ) {

        Objects.requireNonNull(
                level,
                "일간 강약 수준이 필요합니다."
        );

        return level
                == DayMasterStrengthLevel.BALANCED_WEAK

                || level
                == DayMasterStrengthLevel.WEAK

                || level
                == DayMasterStrengthLevel.EXTREME_WEAK;
    }

    public boolean strongSide(
            DayMasterStrengthLevel level
    ) {

        Objects.requireNonNull(
                level,
                "일간 강약 수준이 필요합니다."
        );

        return level
                == DayMasterStrengthLevel.BALANCED_STRONG

                || level
                == DayMasterStrengthLevel.STRONG

                || level
                == DayMasterStrengthLevel.EXTREME_STRONG;
    }

    public boolean balanced(
            DayMasterStrengthLevel level
    ) {

        return level
                == DayMasterStrengthLevel.BALANCED;
    }

    /*
     * =================================================
     * 재성 family
     * =================================================
     */

    public boolean meaningfulFamily(
            int wealthFamilyShareBasisPoints
    ) {

        return wealthFamilyShareBasisPoints
                >= MEANINGFUL_WEALTH_SHARE_BASIS_POINTS;
    }

    public boolean strongFamily(
            int wealthFamilyShareBasisPoints
    ) {

        return wealthFamilyShareBasisPoints
                >= STRONG_WEALTH_SHARE_BASIS_POINTS;
    }

    /*
     * =================================================
     * 정재/편재 우세
     * =================================================
     */

    public boolean directWealthClearlyDominates(

            int directWealthPower,

            int indirectWealthPower

    ) {

        if (
                directWealthPower <= 0
        ) {

            return false;
        }

        if (
                indirectWealthPower <= 0
        ) {

            return true;
        }

        return directWealthPower
                * 100L

                >= indirectWealthPower
                * (long) CENTER_DOMINANCE_PERCENT;
    }

    public boolean indirectWealthClearlyDominates(

            int indirectWealthPower,

            int directWealthPower

    ) {

        if (
                indirectWealthPower <= 0
        ) {

            return false;
        }

        if (
                directWealthPower <= 0
        ) {

            return true;
        }

        return indirectWealthPower
                * 100L

                >= directWealthPower
                * (long) CENTER_DOMINANCE_PERCENT;
    }

    /*
     * =================================================
     * 비겁 압박
     * =================================================
     */

    public boolean companionApproachesWealth(

            int companionFamilyPower,

            int wealthFamilyPower

    ) {

        if (
                companionFamilyPower <= 0
                        || wealthFamilyPower <= 0
        ) {

            return false;
        }

        return companionFamilyPower
                * 100L

                >= wealthFamilyPower
                * (long) COMPANION_PRESSURE_APPROACH_PERCENT;
    }

    public boolean companionExceedsWealth(

            int companionFamilyPower,

            int wealthFamilyPower

    ) {

        return companionFamilyPower > 0
                && wealthFamilyPower > 0
                && companionFamilyPower
                > wealthFamilyPower;
    }

    /*
     * =================================================
     * 재성 발달
     * =================================================
     */

    public WealthCompositePatternLevel
    determineWealthDevelopedLevel(

            int wealthFamilyShareBasisPoints,

            int totalWealthOccurrenceCount,

            boolean directWealthVisible,

            boolean indirectWealthVisible

    ) {

        if (
                totalWealthOccurrenceCount <= 0
        ) {

            return null;
        }

        if (
                strongFamily(
                        wealthFamilyShareBasisPoints
                )
                        && totalWealthOccurrenceCount >= 3
        ) {

            return WealthCompositePatternLevel
                    .STRONG;
        }

        if (
                meaningfulFamily(
                        wealthFamilyShareBasisPoints
                )

                        || totalWealthOccurrenceCount >= 3

                        || (
                        directWealthVisible
                                && indirectWealthVisible
                )
        ) {

            return WealthCompositePatternLevel
                    .MEANINGFUL;
        }

        return WealthCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 정재 중심
     * =================================================
     */

    public WealthCompositePatternLevel
    determineDirectWealthCenteredLevel(

            TenGodPowerSummary directWealthSummary,

            TenGodPowerSummary indirectWealthSummary

    ) {

        Objects.requireNonNull(
                directWealthSummary,
                "정재 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                indirectWealthSummary,
                "편재 Summary가 필요합니다."
        );

        if (
                !directWealthSummary.present()
        ) {

            return null;
        }

        int directPower =
                directWealthSummary.allocatedPower();

        int indirectPower =
                indirectWealthSummary.allocatedPower();

        /*
         * 편재가 없는 순수 정재 구조.
         */
        if (
                !indirectWealthSummary.present()
        ) {

            if (
                    directWealthSummary.strongSide()
            ) {

                return WealthCompositePatternLevel
                        .STRONG;
            }

            if (
                    directWealthSummary.meaningful()
                            || directWealthSummary.visible()
                            || directWealthSummary.monthPresent()
            ) {

                return WealthCompositePatternLevel
                        .MEANINGFUL;
            }

            return WealthCompositePatternLevel
                    .REFERENCE;
        }

        /*
         * 정재가 편재보다 명확하게 우세.
         */
        if (
                directWealthClearlyDominates(
                        directPower,
                        indirectPower
                )
                        && directWealthSummary.meaningful()
        ) {

            return WealthCompositePatternLevel
                    .STRONG;
        }

        if (
                directPower
                        > indirectPower
        ) {

            return WealthCompositePatternLevel
                    .MEANINGFUL;
        }

        return null;
    }

    /*
     * =================================================
     * 편재 중심
     * =================================================
     */

    public WealthCompositePatternLevel
    determineIndirectWealthCenteredLevel(

            TenGodPowerSummary directWealthSummary,

            TenGodPowerSummary indirectWealthSummary

    ) {

        Objects.requireNonNull(
                directWealthSummary,
                "정재 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                indirectWealthSummary,
                "편재 Summary가 필요합니다."
        );

        if (
                !indirectWealthSummary.present()
        ) {

            return null;
        }

        int directPower =
                directWealthSummary.allocatedPower();

        int indirectPower =
                indirectWealthSummary.allocatedPower();

        /*
         * 정재가 없는 순수 편재 구조.
         */
        if (
                !directWealthSummary.present()
        ) {

            if (
                    indirectWealthSummary.strongSide()
            ) {

                return WealthCompositePatternLevel
                        .STRONG;
            }

            if (
                    indirectWealthSummary.meaningful()
                            || indirectWealthSummary.visible()
                            || indirectWealthSummary.monthPresent()
            ) {

                return WealthCompositePatternLevel
                        .MEANINGFUL;
            }

            return WealthCompositePatternLevel
                    .REFERENCE;
        }

        /*
         * 편재가 정재보다 명확하게 우세.
         */
        if (
                indirectWealthClearlyDominates(
                        indirectPower,
                        directPower
                )
                        && indirectWealthSummary.meaningful()
        ) {

            return WealthCompositePatternLevel
                    .STRONG;
        }

        if (
                indirectPower
                        > directPower
        ) {

            return WealthCompositePatternLevel
                    .MEANINGFUL;
        }

        return null;
    }

    /*
     * =================================================
     * 정재·편재 혼재
     * =================================================
     */

    public WealthCompositePatternLevel
    determineMixedWealthLevel(

            TenGodPowerSummary directWealthSummary,

            TenGodPowerSummary indirectWealthSummary

    ) {

        Objects.requireNonNull(
                directWealthSummary,
                "정재 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                indirectWealthSummary,
                "편재 Summary가 필요합니다."
        );

        if (
                !directWealthSummary.present()
                        || !indirectWealthSummary.present()
        ) {

            return null;
        }

        if (
                directWealthSummary.meaningful()
                        && indirectWealthSummary.meaningful()
        ) {

            return WealthCompositePatternLevel
                    .STRONG;
        }

        if (
                directWealthSummary.meaningful()
                        || indirectWealthSummary.meaningful()
                        || directWealthSummary.visible()
                        || indirectWealthSummary.visible()
        ) {

            return WealthCompositePatternLevel
                    .MEANINGFUL;
        }

        return WealthCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 신약 재성 부담
     * =================================================
     *
     * 이것만으로 고전적 재다신약을
     * 최종 확정하지 않는다.
     */

    public WealthCompositePatternLevel
    determineWeakDayMasterWealthBurdenLevel(

            DayMasterStrengthLevel strengthLevel,

            int wealthFamilyShareBasisPoints,

            int totalWealthOccurrenceCount

    ) {

        if (
                !weakSide(
                        strengthLevel
                )
                        || totalWealthOccurrenceCount <= 0
        ) {

            return null;
        }

        if (
                strongFamily(
                        wealthFamilyShareBasisPoints
                )
        ) {

            return WealthCompositePatternLevel
                    .STRONG;
        }

        if (
                meaningfulFamily(
                        wealthFamilyShareBasisPoints
                )
        ) {

            return WealthCompositePatternLevel
                    .MEANINGFUL;
        }

        return WealthCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 비겁 압박 재성
     * =================================================
     *
     * 10-2 WEALTH_CONTEST의
     * 재성 측 관점 Evidence.
     *
     * 여기에서도 비겁쟁재를 최종 확정하지 않는다.
     */

    public WealthCompositePatternLevel
    determineCompanionPressuredWealthLevel(

            int wealthFamilyPower,

            int companionFamilyPower,

            int wealthFamilyShareBasisPoints

    ) {

        if (
                wealthFamilyPower <= 0
                        || companionFamilyPower <= 0
        ) {

            return null;
        }

        if (
                !companionApproachesWealth(
                        companionFamilyPower,
                        wealthFamilyPower
                )
        ) {

            return null;
        }

        if (
                companionExceedsWealth(
                        companionFamilyPower,
                        wealthFamilyPower
                )
                        && meaningfulFamily(
                        wealthFamilyShareBasisPoints
                )
        ) {

            return WealthCompositePatternLevel
                    .STRONG;
        }

        return WealthCompositePatternLevel
                .MEANINGFUL;
    }

    /*
     * =================================================
     * Reason
     * =================================================
     */

    public List<WealthCompositeReasonType>
    buildReasons(

            WealthCompositePatternType patternType,

            TenGodPowerSummary directWealthSummary,

            TenGodPowerSummary indirectWealthSummary,

            DayMasterStrengthLevel strengthLevel,

            int wealthFamilyShareBasisPoints,

            int wealthFamilyPower,

            int companionFamilyPower,

            int officerFamilyPower,

            int resourceFamilyPower

    ) {

        Objects.requireNonNull(
                patternType,
                "재성 Pattern 종류가 필요합니다."
        );

        Objects.requireNonNull(
                directWealthSummary,
                "정재 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                indirectWealthSummary,
                "편재 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 수준이 필요합니다."
        );

        List<WealthCompositeReasonType> reasons =
                new ArrayList<>();

        /*
         * =================================================
         * 정재
         * =================================================
         */

        if (
                directWealthSummary.present()
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .DIRECT_WEALTH_PRESENT
            );
        }

        if (
                directWealthSummary.visible()
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .DIRECT_WEALTH_VISIBLE
            );
        }

        if (
                directWealthSummary.monthPresent()
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .DIRECT_WEALTH_MONTH_PRESENT
            );
        }

        if (
                directWealthSummary.meaningful()
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .DIRECT_WEALTH_MEANINGFUL_POWER
            );
        }

        /*
         * =================================================
         * 편재
         * =================================================
         */

        if (
                indirectWealthSummary.present()
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .INDIRECT_WEALTH_PRESENT
            );
        }

        if (
                indirectWealthSummary.visible()
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .INDIRECT_WEALTH_VISIBLE
            );
        }

        if (
                indirectWealthSummary.monthPresent()
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .INDIRECT_WEALTH_MONTH_PRESENT
            );
        }

        if (
                indirectWealthSummary.meaningful()
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .INDIRECT_WEALTH_MEANINGFUL_POWER
            );
        }

        /*
         * =================================================
         * 중심 우세
         * =================================================
         */

        if (
                directWealthSummary.allocatedPower()
                        > indirectWealthSummary.allocatedPower()
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .DIRECT_WEALTH_DOMINATES
            );

        } else if (
                indirectWealthSummary.allocatedPower()
                        > directWealthSummary.allocatedPower()
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .INDIRECT_WEALTH_DOMINATES
            );
        }

        /*
         * =================================================
         * 재성 전체
         * =================================================
         */

        if (
                meaningfulFamily(
                        wealthFamilyShareBasisPoints
                )
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .WEALTH_FAMILY_MEANINGFUL
            );
        }

        if (
                strongFamily(
                        wealthFamilyShareBasisPoints
                )
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .WEALTH_FAMILY_STRONG
            );
        }

        int totalOccurrenceCount =
                directWealthSummary.occurrenceCount()
                        + indirectWealthSummary.occurrenceCount();

        if (
                totalOccurrenceCount >= 2
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .MULTIPLE_WEALTH_SOURCES
            );
        }

        if (
                directWealthSummary.present()
                        && indirectWealthSummary.present()
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .DIRECT_AND_INDIRECT_WEALTH_COEXIST
            );
        }

        /*
         * =================================================
         * 일간 강약
         * =================================================
         */

        if (
                weakSide(
                        strengthLevel
                )
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .DAY_MASTER_WEAK_SIDE
            );

        } else if (
                strongSide(
                        strengthLevel
                )
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .DAY_MASTER_STRONG_SIDE
            );

        } else {

            reasons.add(
                    WealthCompositeReasonType
                            .DAY_MASTER_BALANCED
            );
        }

        /*
         * =================================================
         * 비겁
         * =================================================
         */

        if (
                companionFamilyPower > 0
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .COMPANION_PRESENT
            );
        }

        if (
                companionApproachesWealth(
                        companionFamilyPower,
                        wealthFamilyPower
                )
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .COMPANION_APPROACHES_WEALTH_POWER
            );
        }

        if (
                companionExceedsWealth(
                        companionFamilyPower,
                        wealthFamilyPower
                )
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .COMPANION_EXCEEDS_WEALTH_POWER
            );
        }

        /*
         * =================================================
         * 관살 Context
         * =================================================
         */

        if (
                officerFamilyPower > 0
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .OFFICER_PRESENT
            );
        }

        if (
                wealthFamilyPower > 0
                        && officerFamilyPower > 0
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .WEALTH_AND_OFFICER_COEXIST
            );
        }

        /*
         * =================================================
         * 인성 Context
         * =================================================
         */

        if (
                resourceFamilyPower > 0
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .RESOURCE_PRESENT
            );
        }

        if (
                wealthFamilyPower > 0
                        && resourceFamilyPower > 0
        ) {

            reasons.add(
                    WealthCompositeReasonType
                            .WEALTH_AND_RESOURCE_COEXIST
            );
        }

        /*
         * =================================================
         * Pattern
         * =================================================
         */

        switch (
                patternType
        ) {

            case WEALTH_DEVELOPED ->
                    reasons.add(
                            WealthCompositeReasonType
                                    .WEALTH_DEVELOPED_PATTERN
                    );

            case DIRECT_WEALTH_CENTERED ->
                    reasons.add(
                            WealthCompositeReasonType
                                    .DIRECT_WEALTH_CENTERED_PATTERN
                    );

            case INDIRECT_WEALTH_CENTERED ->
                    reasons.add(
                            WealthCompositeReasonType
                                    .INDIRECT_WEALTH_CENTERED_PATTERN
                    );

            case MIXED_WEALTH ->
                    reasons.add(
                            WealthCompositeReasonType
                                    .MIXED_WEALTH_PATTERN
                    );

            case WEAK_DAY_MASTER_WEALTH_BURDEN ->
                    reasons.add(
                            WealthCompositeReasonType
                                    .WEAK_DAY_MASTER_WEALTH_BURDEN_PATTERN
                    );

            case COMPANION_PRESSURED_WEALTH ->
                    reasons.add(
                            WealthCompositeReasonType
                                    .COMPANION_PRESSURED_WEALTH_PATTERN
                    );
        }

        return List.copyOf(
                reasons
        );
    }
}