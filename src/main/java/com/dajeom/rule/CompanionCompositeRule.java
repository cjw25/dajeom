package com.dajeom.rule;

import com.dajeom.model.TenGodPowerSummary;
import com.dajeom.type.CompanionCompositePatternLevel;
import com.dajeom.type.CompanionCompositePatternType;
import com.dajeom.type.CompanionCompositeReasonType;
import com.dajeom.type.DayMasterStrengthLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-2.
 *
 * 비겁 복합구조 판정 Rule.
 *
 * =================================================
 *
 * 점수 합산 방식이 아니다.
 *
 * 각 Pattern마다 독립적인
 * 구조 조건을 적용한다.
 *
 * =================================================
 *
 * 주요 기준:
 *
 * 비겁 family share
 *
 * 15% 이상 -> 의미 있는 비겁 세력
 * 25% 이상 -> 강한 비겁 세력
 *
 * =================================================
 *
 * 재성 쟁탈:
 *
 * 비겁이 재성의 80% 이상 접근
 * +
 * 재성이 실제 존재
 *
 * 를 기본 조건으로 사용한다.
 *
 * 겁재가 의미 있게 존재하면
 * 한 단계 강하게 볼 수 있다.
 */
@Component
public class CompanionCompositeRule {

    public static final int
            MEANINGFUL_COMPANION_SHARE_BASIS_POINTS =
            1_500;

    public static final int
            STRONG_COMPANION_SHARE_BASIS_POINTS =
            2_500;

    public static final int
            WEALTH_APPROACH_PERCENT =
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
     * 비겁 family
     * =================================================
     */

    public boolean meaningfulFamily(
            int familyShareBasisPoints
    ) {

        return familyShareBasisPoints
                >= MEANINGFUL_COMPANION_SHARE_BASIS_POINTS;
    }

    public boolean strongFamily(
            int familyShareBasisPoints
    ) {

        return familyShareBasisPoints
                >= STRONG_COMPANION_SHARE_BASIS_POINTS;
    }

    /*
     * =================================================
     * 재성 상대관계
     * =================================================
     */

    public boolean approachesWealth(

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
                * (long) WEALTH_APPROACH_PERCENT;
    }

    public boolean exceedsWealth(

            int companionFamilyPower,

            int wealthFamilyPower

    ) {

        return wealthFamilyPower > 0
                && companionFamilyPower
                > wealthFamilyPower;
    }

    /*
     * =================================================
     * 신약 보조형 비겁
     * =================================================
     */

    public CompanionCompositePatternLevel
    determineSupportiveLevel(

            DayMasterStrengthLevel strengthLevel,

            int familyShareBasisPoints,

            int totalOccurrenceCount

    ) {

        if (
                !weakSide(
                        strengthLevel
                )
                        || totalOccurrenceCount == 0
        ) {

            return null;
        }

        if (
                strongFamily(
                        familyShareBasisPoints
                )
        ) {

            return CompanionCompositePatternLevel
                    .STRONG;
        }

        if (
                meaningfulFamily(
                        familyShareBasisPoints
                )
        ) {

            return CompanionCompositePatternLevel
                    .MEANINGFUL;
        }

        return CompanionCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 비겁 군집
     * =================================================
     */

    public CompanionCompositePatternLevel
    determineClusterLevel(

            int familyShareBasisPoints,

            int totalOccurrenceCount,

            boolean companionVisible,

            boolean robWealthVisible

    ) {

        if (
                totalOccurrenceCount < 2
        ) {

            return null;
        }

        if (
                totalOccurrenceCount >= 4
                        && strongFamily(
                        familyShareBasisPoints
                )
        ) {

            return CompanionCompositePatternLevel
                    .STRONG;
        }

        if (
                totalOccurrenceCount >= 3
                        || meaningfulFamily(
                        familyShareBasisPoints
                )
                        || (
                        companionVisible
                                && robWealthVisible
                )
        ) {

            return CompanionCompositePatternLevel
                    .MEANINGFUL;
        }

        return CompanionCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 겁재 경쟁
     * =================================================
     */

    public CompanionCompositePatternLevel
    determineRobWealthCompetitionLevel(

            TenGodPowerSummary robWealthSummary,

            int familyShareBasisPoints

    ) {

        Objects.requireNonNull(
                robWealthSummary,
                "겁재 Summary가 필요합니다."
        );

        if (
                !robWealthSummary.present()
        ) {

            return null;
        }

        if (
                robWealthSummary.strongSide()
                        && robWealthSummary.visible()
        ) {

            return CompanionCompositePatternLevel
                    .STRONG;
        }

        if (
                robWealthSummary.meaningful()
                        || robWealthSummary.visible()

                        || meaningfulFamily(
                        familyShareBasisPoints
                )
        ) {

            return CompanionCompositePatternLevel
                    .MEANINGFUL;
        }

        return CompanionCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 재성 쟁탈
     * =================================================
     */

    public CompanionCompositePatternLevel
    determineWealthContestLevel(

            int companionFamilyPower,

            int wealthFamilyPower,

            TenGodPowerSummary robWealthSummary

    ) {

        Objects.requireNonNull(
                robWealthSummary,
                "겁재 Summary가 필요합니다."
        );

        if (
                wealthFamilyPower <= 0
                        || companionFamilyPower <= 0
        ) {

            return null;
        }

        if (
                exceedsWealth(
                        companionFamilyPower,
                        wealthFamilyPower
                )
                        && robWealthSummary.meaningful()
        ) {

            return CompanionCompositePatternLevel
                    .STRONG;
        }

        if (
                approachesWealth(
                        companionFamilyPower,
                        wealthFamilyPower
                )
        ) {

            return CompanionCompositePatternLevel
                    .MEANINGFUL;
        }

        if (
                robWealthSummary.present()
        ) {

            return CompanionCompositePatternLevel
                    .REFERENCE;
        }

        return null;
    }

    /*
     * =================================================
     * 신강 비겁 과다
     * =================================================
     */

    public CompanionCompositePatternLevel
    determineExcessiveLevel(

            DayMasterStrengthLevel strengthLevel,

            int familyShareBasisPoints,

            int totalOccurrenceCount

    ) {

        if (
                !strongSide(
                        strengthLevel
                )
                        || totalOccurrenceCount == 0
        ) {

            return null;
        }

        if (
                strongFamily(
                        familyShareBasisPoints
                )
                        && totalOccurrenceCount >= 3
        ) {

            return CompanionCompositePatternLevel
                    .STRONG;
        }

        if (
                meaningfulFamily(
                        familyShareBasisPoints
                )
        ) {

            return CompanionCompositePatternLevel
                    .MEANINGFUL;
        }

        return CompanionCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 비견·겁재 혼재
     * =================================================
     */

    public CompanionCompositePatternLevel
    determineMixedLevel(

            TenGodPowerSummary companionSummary,

            TenGodPowerSummary robWealthSummary

    ) {

        Objects.requireNonNull(
                companionSummary,
                "비견 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                robWealthSummary,
                "겁재 Summary가 필요합니다."
        );

        if (
                !companionSummary.present()
                        || !robWealthSummary.present()
        ) {

            return null;
        }

        if (
                companionSummary.meaningful()
                        && robWealthSummary.meaningful()
        ) {

            return CompanionCompositePatternLevel
                    .STRONG;
        }

        if (
                companionSummary.visible()
                        || robWealthSummary.visible()
        ) {

            return CompanionCompositePatternLevel
                    .MEANINGFUL;
        }

        return CompanionCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 공통 Reason
     * =================================================
     */

    public List<CompanionCompositeReasonType>
    buildReasons(

            CompanionCompositePatternType patternType,

            TenGodPowerSummary companionSummary,

            TenGodPowerSummary robWealthSummary,

            DayMasterStrengthLevel strengthLevel,

            int familyShareBasisPoints,

            int companionFamilyPower,

            int wealthFamilyPower

    ) {

        Objects.requireNonNull(
                patternType,
                "비겁 Pattern 종류가 필요합니다."
        );

        Objects.requireNonNull(
                companionSummary,
                "비견 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                robWealthSummary,
                "겁재 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 수준이 필요합니다."
        );

        List<CompanionCompositeReasonType> reasons =
                new ArrayList<>();

        /*
         * 비견
         */
        if (
                companionSummary.present()
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .COMPANION_PRESENT
            );
        }

        if (
                companionSummary.visible()
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .COMPANION_VISIBLE
            );
        }

        if (
                companionSummary.monthPresent()
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .COMPANION_MONTH_PRESENT
            );
        }

        if (
                companionSummary.meaningful()
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .COMPANION_MEANINGFUL_POWER
            );
        }

        /*
         * 겁재
         */
        if (
                robWealthSummary.present()
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .ROB_WEALTH_PRESENT
            );
        }

        if (
                robWealthSummary.visible()
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .ROB_WEALTH_VISIBLE
            );
        }

        if (
                robWealthSummary.monthPresent()
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .ROB_WEALTH_MONTH_PRESENT
            );
        }

        if (
                robWealthSummary.meaningful()
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .ROB_WEALTH_MEANINGFUL_POWER
            );
        }

        /*
         * 비겁 전체
         */
        if (
                meaningfulFamily(
                        familyShareBasisPoints
                )
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .COMPANION_FAMILY_MEANINGFUL
            );
        }

        if (
                strongFamily(
                        familyShareBasisPoints
                )
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .COMPANION_FAMILY_STRONG
            );
        }

        int occurrenceCount =
                companionSummary.occurrenceCount()
                        + robWealthSummary.occurrenceCount();

        if (
                occurrenceCount >= 2
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .MULTIPLE_COMPANION_SOURCES
            );
        }

        if (
                companionSummary.present()
                        && robWealthSummary.present()
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .COMPANION_AND_ROB_WEALTH_COEXIST
            );
        }

        /*
         * 강약
         */
        if (
                weakSide(
                        strengthLevel
                )
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .DAY_MASTER_WEAK_SIDE
            );

        } else if (
                strongSide(
                        strengthLevel
                )
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .DAY_MASTER_STRONG_SIDE
            );

        } else {

            reasons.add(
                    CompanionCompositeReasonType
                            .DAY_MASTER_BALANCED
            );
        }

        /*
         * 재성
         */
        if (
                wealthFamilyPower > 0
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .WEALTH_PRESENT
            );
        }

        if (
                wealthFamilyPower > 0
                        && companionFamilyPower > 0
                        && approachesWealth(
                        companionFamilyPower,
                        wealthFamilyPower
                )
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .COMPANION_APPROACHES_WEALTH_POWER
            );
        }

        if (
                exceedsWealth(
                        companionFamilyPower,
                        wealthFamilyPower
                )
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .COMPANION_EXCEEDS_WEALTH_POWER
            );
        }

        if (
                robWealthSummary.meaningful()
                        && wealthFamilyPower > 0
        ) {

            reasons.add(
                    CompanionCompositeReasonType
                            .ROB_WEALTH_WITH_WEALTH
            );
        }

        /*
         * Pattern 자체
         */
        switch (
                patternType
        ) {

            case SUPPORTIVE_COMPANION ->
                    reasons.add(
                            CompanionCompositeReasonType
                                    .SUPPORTIVE_PATTERN
                    );

            case COMPANION_CLUSTER ->
                    reasons.add(
                            CompanionCompositeReasonType
                                    .CLUSTER_PATTERN
                    );

            case ROB_WEALTH_COMPETITION ->
                    reasons.add(
                            CompanionCompositeReasonType
                                    .COMPETITION_PATTERN
                    );

            case WEALTH_CONTEST ->
                    reasons.add(
                            CompanionCompositeReasonType
                                    .WEALTH_CONTEST_PATTERN
                    );

            case EXCESSIVE_COMPANION ->
                    reasons.add(
                            CompanionCompositeReasonType
                                    .EXCESSIVE_PATTERN
                    );

            case MIXED_COMPANION_ROB_WEALTH ->
                    reasons.add(
                            CompanionCompositeReasonType
                                    .MIXED_PATTERN
                    );
        }

        return List.copyOf(
                reasons
        );
    }
}