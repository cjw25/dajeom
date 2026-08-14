package com.dajeom.rule;

import com.dajeom.model.TenGodPowerSummary;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.OfficerCompositePatternLevel;
import com.dajeom.type.OfficerCompositePatternType;
import com.dajeom.type.OfficerCompositeReasonType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-5.
 *
 * 관살 복합구조 Rule.
 *
 * =================================================
 *
 * 총점 계산이 아니다.
 *
 * Pattern마다 독립적인 구조 조건을 사용한다.
 *
 * =================================================
 *
 * 관살 family share:
 *
 * 15% 이상 -> 의미 있는 관살
 * 25% 이상 -> 강한 관살
 *
 * =================================================
 *
 * 정관/칠살 중심:
 *
 * 한쪽이 상대 세력의 125% 이상이고
 * 자체 십성도 의미 있는 수준이면
 * STRONG 중심 Pattern으로 본다.
 *
 * =================================================
 *
 * 식상/인성/재성 Context:
 *
 * 해당 family가 관살 세력의
 * 80% 이상에 접근할 경우
 * 비교적 강한 상호작용 Context로 본다.
 *
 * 주의:
 *
 * 이것은 식신제살, 살인상생,
 * 관인상생, 재생관의 최종 판정이 아니다.
 */
@Component
public class OfficerCompositeRule {

    public static final int
            MEANINGFUL_OFFICER_SHARE_BASIS_POINTS =
            1_500;

    public static final int
            STRONG_OFFICER_SHARE_BASIS_POINTS =
            2_500;

    public static final int
            CENTER_DOMINANCE_PERCENT =
            125;

    public static final int
            CONTEXT_APPROACH_PERCENT =
            80;

    /*
     * =================================================
     * 강약
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

    /*
     * =================================================
     * 관살 family
     * =================================================
     */

    public boolean meaningfulFamily(
            int shareBasisPoints
    ) {

        return shareBasisPoints
                >= MEANINGFUL_OFFICER_SHARE_BASIS_POINTS;
    }

    public boolean strongFamily(
            int shareBasisPoints
    ) {

        return shareBasisPoints
                >= STRONG_OFFICER_SHARE_BASIS_POINTS;
    }

    /*
     * =================================================
     * 중심성
     * =================================================
     */

    public boolean directOfficerClearlyDominates(

            int directOfficerPower,

            int sevenKillingsPower

    ) {

        if (
                directOfficerPower <= 0
        ) {

            return false;
        }

        if (
                sevenKillingsPower <= 0
        ) {

            return true;
        }

        return directOfficerPower
                * 100L

                >= sevenKillingsPower
                * (long) CENTER_DOMINANCE_PERCENT;
    }

    public boolean sevenKillingsClearlyDominates(

            int sevenKillingsPower,

            int directOfficerPower

    ) {

        if (
                sevenKillingsPower <= 0
        ) {

            return false;
        }

        if (
                directOfficerPower <= 0
        ) {

            return true;
        }

        return sevenKillingsPower
                * 100L

                >= directOfficerPower
                * (long) CENTER_DOMINANCE_PERCENT;
    }

    /*
     * =================================================
     * 상대 family
     * =================================================
     */

    public boolean approachesOfficer(

            int otherFamilyPower,

            int officerFamilyPower

    ) {

        if (
                otherFamilyPower <= 0
                        || officerFamilyPower <= 0
        ) {

            return false;
        }

        return otherFamilyPower
                * 100L

                >= officerFamilyPower
                * (long) CONTEXT_APPROACH_PERCENT;
    }

    public boolean exceedsOfficer(

            int otherFamilyPower,

            int officerFamilyPower

    ) {

        return otherFamilyPower > 0
                && officerFamilyPower > 0
                && otherFamilyPower
                > officerFamilyPower;
    }

    /*
     * =================================================
     * 관살 발달
     * =================================================
     */

    public OfficerCompositePatternLevel
    determineOfficerDevelopedLevel(

            int officerFamilyShareBasisPoints,

            int totalOccurrenceCount,

            boolean directOfficerVisible,

            boolean sevenKillingsVisible

    ) {

        if (
                totalOccurrenceCount <= 0
        ) {

            return null;
        }

        if (
                strongFamily(
                        officerFamilyShareBasisPoints
                )
                        && totalOccurrenceCount >= 3
        ) {

            return OfficerCompositePatternLevel
                    .STRONG;
        }

        if (
                meaningfulFamily(
                        officerFamilyShareBasisPoints
                )

                        || totalOccurrenceCount >= 3

                        || (
                        directOfficerVisible
                                && sevenKillingsVisible
                )
        ) {

            return OfficerCompositePatternLevel
                    .MEANINGFUL;
        }

        return OfficerCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 정관 중심
     * =================================================
     */

    public OfficerCompositePatternLevel
    determineDirectOfficerCenteredLevel(

            TenGodPowerSummary directOfficerSummary,

            TenGodPowerSummary sevenKillingsSummary

    ) {

        Objects.requireNonNull(
                directOfficerSummary,
                "정관 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                sevenKillingsSummary,
                "칠살 Summary가 필요합니다."
        );

        if (
                !directOfficerSummary.present()
        ) {

            return null;
        }

        int directPower =
                directOfficerSummary.allocatedPower();

        int killingPower =
                sevenKillingsSummary.allocatedPower();

        if (
                !sevenKillingsSummary.present()
        ) {

            if (
                    directOfficerSummary.strongSide()
            ) {

                return OfficerCompositePatternLevel
                        .STRONG;
            }

            if (
                    directOfficerSummary.meaningful()
                            || directOfficerSummary.visible()
                            || directOfficerSummary.monthPresent()
            ) {

                return OfficerCompositePatternLevel
                        .MEANINGFUL;
            }

            return OfficerCompositePatternLevel
                    .REFERENCE;
        }

        if (
                directOfficerClearlyDominates(
                        directPower,
                        killingPower
                )
                        && directOfficerSummary.meaningful()
        ) {

            return OfficerCompositePatternLevel
                    .STRONG;
        }

        if (
                directPower > killingPower
        ) {

            return OfficerCompositePatternLevel
                    .MEANINGFUL;
        }

        return null;
    }

    /*
     * =================================================
     * 칠살 중심
     * =================================================
     */

    public OfficerCompositePatternLevel
    determineSevenKillingsCenteredLevel(

            TenGodPowerSummary directOfficerSummary,

            TenGodPowerSummary sevenKillingsSummary

    ) {

        Objects.requireNonNull(
                directOfficerSummary,
                "정관 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                sevenKillingsSummary,
                "칠살 Summary가 필요합니다."
        );

        if (
                !sevenKillingsSummary.present()
        ) {

            return null;
        }

        int directPower =
                directOfficerSummary.allocatedPower();

        int killingPower =
                sevenKillingsSummary.allocatedPower();

        if (
                !directOfficerSummary.present()
        ) {

            if (
                    sevenKillingsSummary.strongSide()
            ) {

                return OfficerCompositePatternLevel
                        .STRONG;
            }

            if (
                    sevenKillingsSummary.meaningful()
                            || sevenKillingsSummary.visible()
                            || sevenKillingsSummary.monthPresent()
            ) {

                return OfficerCompositePatternLevel
                        .MEANINGFUL;
            }

            return OfficerCompositePatternLevel
                    .REFERENCE;
        }

        if (
                sevenKillingsClearlyDominates(
                        killingPower,
                        directPower
                )
                        && sevenKillingsSummary.meaningful()
        ) {

            return OfficerCompositePatternLevel
                    .STRONG;
        }

        if (
                killingPower > directPower
        ) {

            return OfficerCompositePatternLevel
                    .MEANINGFUL;
        }

        return null;
    }

    /*
     * =================================================
     * 정관·칠살 혼재
     * =================================================
     */

    public OfficerCompositePatternLevel
    determineMixedOfficerKillingsLevel(

            TenGodPowerSummary directOfficerSummary,

            TenGodPowerSummary sevenKillingsSummary

    ) {

        if (
                !directOfficerSummary.present()
                        || !sevenKillingsSummary.present()
        ) {

            return null;
        }

        if (
                directOfficerSummary.meaningful()
                        && sevenKillingsSummary.meaningful()
        ) {

            return OfficerCompositePatternLevel
                    .STRONG;
        }

        if (
                directOfficerSummary.meaningful()
                        || sevenKillingsSummary.meaningful()
                        || directOfficerSummary.visible()
                        || sevenKillingsSummary.visible()
        ) {

            return OfficerCompositePatternLevel
                    .MEANINGFUL;
        }

        return OfficerCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 신약 관살 압박
     * =================================================
     */

    public OfficerCompositePatternLevel
    determineWeakDayMasterPressureLevel(

            DayMasterStrengthLevel strengthLevel,

            int officerFamilyShareBasisPoints,

            int totalOccurrenceCount

    ) {

        if (
                !weakSide(
                        strengthLevel
                )
                        || totalOccurrenceCount <= 0
        ) {

            return null;
        }

        if (
                strongFamily(
                        officerFamilyShareBasisPoints
                )
        ) {

            return OfficerCompositePatternLevel
                    .STRONG;
        }

        if (
                meaningfulFamily(
                        officerFamilyShareBasisPoints
                )
        ) {

            return OfficerCompositePatternLevel
                    .MEANINGFUL;
        }

        return OfficerCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 식상 제어 Context
     * =================================================
     */

    public OfficerCompositePatternLevel
    determineOutputControlContextLevel(

            int officerFamilyPower,

            int outputFamilyPower

    ) {

        if (
                officerFamilyPower <= 0
                        || outputFamilyPower <= 0
        ) {

            return null;
        }

        if (
                exceedsOfficer(
                        outputFamilyPower,
                        officerFamilyPower
                )
        ) {

            return OfficerCompositePatternLevel
                    .STRONG;
        }

        if (
                approachesOfficer(
                        outputFamilyPower,
                        officerFamilyPower
                )
        ) {

            return OfficerCompositePatternLevel
                    .MEANINGFUL;
        }

        return OfficerCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 인성 연결 Context
     * =================================================
     */

    public OfficerCompositePatternLevel
    determineResourceMediationContextLevel(

            int officerFamilyPower,

            int resourceFamilyPower

    ) {

        if (
                officerFamilyPower <= 0
                        || resourceFamilyPower <= 0
        ) {

            return null;
        }

        if (
                exceedsOfficer(
                        resourceFamilyPower,
                        officerFamilyPower
                )
        ) {

            return OfficerCompositePatternLevel
                    .STRONG;
        }

        if (
                approachesOfficer(
                        resourceFamilyPower,
                        officerFamilyPower
                )
        ) {

            return OfficerCompositePatternLevel
                    .MEANINGFUL;
        }

        return OfficerCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 재성 생조 Context
     * =================================================
     */

    public OfficerCompositePatternLevel
    determineWealthSupportContextLevel(

            int officerFamilyPower,

            int wealthFamilyPower

    ) {

        if (
                officerFamilyPower <= 0
                        || wealthFamilyPower <= 0
        ) {

            return null;
        }

        if (
                exceedsOfficer(
                        wealthFamilyPower,
                        officerFamilyPower
                )
        ) {

            return OfficerCompositePatternLevel
                    .STRONG;
        }

        if (
                approachesOfficer(
                        wealthFamilyPower,
                        officerFamilyPower
                )
        ) {

            return OfficerCompositePatternLevel
                    .MEANINGFUL;
        }

        return OfficerCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * Reason
     * =================================================
     */

    public List<OfficerCompositeReasonType>
    buildReasons(

            OfficerCompositePatternType patternType,

            TenGodPowerSummary directOfficerSummary,

            TenGodPowerSummary sevenKillingsSummary,

            DayMasterStrengthLevel strengthLevel,

            int officerFamilyShareBasisPoints,

            int officerFamilyPower,

            int outputFamilyPower,

            int resourceFamilyPower,

            int wealthFamilyPower

    ) {

        Objects.requireNonNull(
                patternType,
                "관살 Pattern 종류가 필요합니다."
        );

        List<OfficerCompositeReasonType> reasons =
                new ArrayList<>();

        /*
         * 정관
         */
        if (
                directOfficerSummary.present()
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .DIRECT_OFFICER_PRESENT
            );
        }

        if (
                directOfficerSummary.visible()
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .DIRECT_OFFICER_VISIBLE
            );
        }

        if (
                directOfficerSummary.monthPresent()
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .DIRECT_OFFICER_MONTH_PRESENT
            );
        }

        if (
                directOfficerSummary.meaningful()
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .DIRECT_OFFICER_MEANINGFUL_POWER
            );
        }

        /*
         * 칠살
         */
        if (
                sevenKillingsSummary.present()
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .SEVEN_KILLINGS_PRESENT
            );
        }

        if (
                sevenKillingsSummary.visible()
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .SEVEN_KILLINGS_VISIBLE
            );
        }

        if (
                sevenKillingsSummary.monthPresent()
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .SEVEN_KILLINGS_MONTH_PRESENT
            );
        }

        if (
                sevenKillingsSummary.meaningful()
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .SEVEN_KILLINGS_MEANINGFUL_POWER
            );
        }

        /*
         * 중심 우세
         */
        if (
                directOfficerSummary.allocatedPower()
                        > sevenKillingsSummary.allocatedPower()
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .DIRECT_OFFICER_DOMINATES
            );

        } else if (
                sevenKillingsSummary.allocatedPower()
                        > directOfficerSummary.allocatedPower()
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .SEVEN_KILLINGS_DOMINATES
            );
        }

        /*
         * 관살 전체
         */
        if (
                meaningfulFamily(
                        officerFamilyShareBasisPoints
                )
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .OFFICER_FAMILY_MEANINGFUL
            );
        }

        if (
                strongFamily(
                        officerFamilyShareBasisPoints
                )
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .OFFICER_FAMILY_STRONG
            );
        }

        int totalOccurrenceCount =
                directOfficerSummary.occurrenceCount()
                        + sevenKillingsSummary.occurrenceCount();

        if (
                totalOccurrenceCount >= 2
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .MULTIPLE_OFFICER_SOURCES
            );
        }

        if (
                directOfficerSummary.present()
                        && sevenKillingsSummary.present()
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .DIRECT_OFFICER_AND_SEVEN_KILLINGS_COEXIST
            );
        }

        /*
         * 신강·신약
         */
        if (
                weakSide(
                        strengthLevel
                )
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .DAY_MASTER_WEAK_SIDE
            );

        } else if (
                strongSide(
                        strengthLevel
                )
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .DAY_MASTER_STRONG_SIDE
            );

        } else {

            reasons.add(
                    OfficerCompositeReasonType
                            .DAY_MASTER_BALANCED
            );
        }

        /*
         * 식상
         */
        if (
                outputFamilyPower > 0
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .OUTPUT_PRESENT
            );
        }

        if (
                approachesOfficer(
                        outputFamilyPower,
                        officerFamilyPower
                )
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .OUTPUT_APPROACHES_OFFICER_POWER
            );
        }

        if (
                exceedsOfficer(
                        outputFamilyPower,
                        officerFamilyPower
                )
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .OUTPUT_EXCEEDS_OFFICER_POWER
            );
        }

        /*
         * 인성
         */
        if (
                resourceFamilyPower > 0
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .RESOURCE_PRESENT
            );
        }

        if (
                approachesOfficer(
                        resourceFamilyPower,
                        officerFamilyPower
                )
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .RESOURCE_APPROACHES_OFFICER_POWER
            );
        }

        if (
                exceedsOfficer(
                        resourceFamilyPower,
                        officerFamilyPower
                )
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .RESOURCE_EXCEEDS_OFFICER_POWER
            );
        }

        /*
         * 재성
         */
        if (
                wealthFamilyPower > 0
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .WEALTH_PRESENT
            );
        }

        if (
                approachesOfficer(
                        wealthFamilyPower,
                        officerFamilyPower
                )
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .WEALTH_APPROACHES_OFFICER_POWER
            );
        }

        if (
                exceedsOfficer(
                        wealthFamilyPower,
                        officerFamilyPower
                )
        ) {

            reasons.add(
                    OfficerCompositeReasonType
                            .WEALTH_EXCEEDS_OFFICER_POWER
            );
        }

        /*
         * Pattern
         */
        switch (
                patternType
        ) {

            case OFFICER_DEVELOPED ->
                    reasons.add(
                            OfficerCompositeReasonType
                                    .OFFICER_DEVELOPED_PATTERN
                    );

            case DIRECT_OFFICER_CENTERED ->
                    reasons.add(
                            OfficerCompositeReasonType
                                    .DIRECT_OFFICER_CENTERED_PATTERN
                    );

            case SEVEN_KILLINGS_CENTERED ->
                    reasons.add(
                            OfficerCompositeReasonType
                                    .SEVEN_KILLINGS_CENTERED_PATTERN
                    );

            case MIXED_OFFICER_KILLINGS ->
                    reasons.add(
                            OfficerCompositeReasonType
                                    .MIXED_OFFICER_KILLINGS_PATTERN
                    );

            case WEAK_DAY_MASTER_OFFICER_PRESSURE ->
                    reasons.add(
                            OfficerCompositeReasonType
                                    .WEAK_DAY_MASTER_OFFICER_PRESSURE_PATTERN
                    );

            case OUTPUT_CONTROL_CONTEXT ->
                    reasons.add(
                            OfficerCompositeReasonType
                                    .OUTPUT_CONTROL_CONTEXT_PATTERN
                    );

            case RESOURCE_MEDIATION_CONTEXT ->
                    reasons.add(
                            OfficerCompositeReasonType
                                    .RESOURCE_MEDIATION_CONTEXT_PATTERN
                    );

            case WEALTH_SUPPORT_CONTEXT ->
                    reasons.add(
                            OfficerCompositeReasonType
                                    .WEALTH_SUPPORT_CONTEXT_PATTERN
                    );
        }

        return List.copyOf(
                reasons
        );
    }
}