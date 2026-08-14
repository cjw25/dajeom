package com.dajeom.rule;

import com.dajeom.model.CompanionCompositeAnalysis;
import com.dajeom.model.OfficerCompositeAnalysis;
import com.dajeom.model.ResourceCompositeAnalysis;
import com.dajeom.model.TenGodFamilyChainAnalysis;
import com.dajeom.model.TenGodPowerSummary;
import com.dajeom.model.WealthCompositeAnalysis;
import com.dajeom.type.CompanionCompositePatternType;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.OfficerCompositePatternType;
import com.dajeom.type.ResourceCompositePatternType;
import com.dajeom.type.TenGodCorePatternLevel;
import com.dajeom.type.TenGodCorePatternReasonType;
import com.dajeom.type.TenGodCorePatternType;
import com.dajeom.type.TenGodFamilyInteractionType;
import com.dajeom.type.WealthCompositePatternType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-8.
 *
 * 핵심 십성 복합 Pattern Rule.
 *
 * =================================================
 *
 * 중요한 정책:
 *
 * 1.
 * "둘 다 존재한다"만으로 STRONG을 만들지 않는다.
 *
 * 2.
 * 개별 십성 세력과 10-7 Family 방향성을
 * 함께 검토한다.
 *
 * 3.
 * 10-2~10-6에서 이미 확인한 구조가 있다면
 * 교차근거로 사용한다.
 *
 * 4.
 * 길흉 판정은 하지 않는다.
 *
 * 5.
 * 격국/용신/합화는 아직 반영하지 않는다.
 *
 * -> 10-9에서 교차검증.
 */
@Component
public class TenGodCorePatternRule {

    public static final int
            BASIC_RELATIVE_POWER_PERCENT =
            60;

    public static final int
            MEANINGFUL_RELATIVE_POWER_PERCENT =
            80;

    public static final int
            MEANINGFUL_FAMILY_SHARE_BASIS_POINTS =
            1_500;

    /*
     * =================================================
     * 공통
     * =================================================
     */

    public boolean reachesPercent(

            int sourcePower,

            int targetPower,

            int percent

    ) {

        if (
                sourcePower <= 0
                        || targetPower <= 0
        ) {

            return false;
        }

        return sourcePower
                * 100L

                >= targetPower
                * (long) percent;
    }

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

    /*
     * =================================================
     * 식신제살
     * =================================================
     */

    public TenGodCorePatternLevel
    determineEatingGodControlsSevenKillings(

            TenGodPowerSummary eatingGod,

            TenGodPowerSummary sevenKillings,

            TenGodFamilyChainAnalysis chainAnalysis

    ) {

        if (
                !eatingGod.present()
                        || !sevenKillings.present()
        ) {

            return null;
        }

        boolean familyControl =
                chainAnalysis
                        .hasMeaningfulInteraction(
                                DayMasterElementRole.OUTPUT,
                                DayMasterElementRole.OFFICER,
                                TenGodFamilyInteractionType.CONTROL
                        );

        if (
                eatingGod.meaningful()
                        && sevenKillings.meaningful()
                        && familyControl
                        && reachesPercent(
                        eatingGod.allocatedPower(),
                        sevenKillings.allocatedPower(),
                        MEANINGFUL_RELATIVE_POWER_PERCENT
                )
        ) {

            return TenGodCorePatternLevel.STRONG;
        }

        if (
                familyControl
                        && reachesPercent(
                        eatingGod.allocatedPower(),
                        sevenKillings.allocatedPower(),
                        BASIC_RELATIVE_POWER_PERCENT
                )
        ) {

            return TenGodCorePatternLevel.MEANINGFUL;
        }

        return TenGodCorePatternLevel.REFERENCE;
    }

    /*
     * =================================================
     * 상관견관
     * =================================================
     */

    public TenGodCorePatternLevel
    determineHurtingOfficerMeetsDirectOfficer(

            TenGodPowerSummary hurtingOfficer,

            TenGodPowerSummary directOfficer,

            TenGodFamilyChainAnalysis chainAnalysis

    ) {

        if (
                !hurtingOfficer.present()
                        || !directOfficer.present()
        ) {

            return null;
        }

        boolean familyControl =
                chainAnalysis
                        .hasMeaningfulInteraction(
                                DayMasterElementRole.OUTPUT,
                                DayMasterElementRole.OFFICER,
                                TenGodFamilyInteractionType.CONTROL
                        );

        if (
                hurtingOfficer.meaningful()
                        && directOfficer.meaningful()
                        && familyControl
                        && (
                        hurtingOfficer.visible()
                                || directOfficer.visible()
                                || hurtingOfficer.monthPresent()
                                || directOfficer.monthPresent()
                )
        ) {

            return TenGodCorePatternLevel.STRONG;
        }

        if (
                familyControl
                        && (
                        hurtingOfficer.meaningful()
                                || directOfficer.meaningful()
                                || hurtingOfficer.visible()
                                || directOfficer.visible()
                )
        ) {

            return TenGodCorePatternLevel.MEANINGFUL;
        }

        return TenGodCorePatternLevel.REFERENCE;
    }

    /*
     * =================================================
     * 상관패인
     *
     * 10-8에서는 정인을 핵심 印으로 사용한다.
     *
     * 편인의 식상 제약은 아래 편인도식과
     * 별도로 분리한다.
     * =================================================
     */

    public TenGodCorePatternLevel
    determineHurtingOfficerWithResource(

            TenGodPowerSummary hurtingOfficer,

            TenGodPowerSummary directResource,

            TenGodFamilyChainAnalysis chainAnalysis

    ) {

        if (
                !hurtingOfficer.present()
                        || !directResource.present()
        ) {

            return null;
        }

        boolean familyControl =
                chainAnalysis
                        .hasMeaningfulInteraction(
                                DayMasterElementRole.RESOURCE,
                                DayMasterElementRole.OUTPUT,
                                TenGodFamilyInteractionType.CONTROL
                        );

        if (
                hurtingOfficer.meaningful()
                        && directResource.meaningful()
                        && familyControl
                        && reachesPercent(
                        directResource.allocatedPower(),
                        hurtingOfficer.allocatedPower(),
                        MEANINGFUL_RELATIVE_POWER_PERCENT
                )
        ) {

            return TenGodCorePatternLevel.STRONG;
        }

        if (
                familyControl
                        && reachesPercent(
                        directResource.allocatedPower(),
                        hurtingOfficer.allocatedPower(),
                        BASIC_RELATIVE_POWER_PERCENT
                )
        ) {

            return TenGodCorePatternLevel.MEANINGFUL;
        }

        return TenGodCorePatternLevel.REFERENCE;
    }

    /*
     * =================================================
     * 편인도식 / 효신탈식
     * =================================================
     */

    public TenGodCorePatternLevel
    determineIndirectResourceSeizesEatingGod(

            TenGodPowerSummary indirectResource,

            TenGodPowerSummary eatingGod,

            TenGodFamilyChainAnalysis chainAnalysis

    ) {

        if (
                !indirectResource.present()
                        || !eatingGod.present()
        ) {

            return null;
        }

        boolean familyControl =
                chainAnalysis
                        .hasMeaningfulInteraction(
                                DayMasterElementRole.RESOURCE,
                                DayMasterElementRole.OUTPUT,
                                TenGodFamilyInteractionType.CONTROL
                        );

        if (
                indirectResource.meaningful()
                        && eatingGod.meaningful()
                        && familyControl
                        && reachesPercent(
                        indirectResource.allocatedPower(),
                        eatingGod.allocatedPower(),
                        MEANINGFUL_RELATIVE_POWER_PERCENT
                )
        ) {

            return TenGodCorePatternLevel.STRONG;
        }

        if (
                familyControl
                        && reachesPercent(
                        indirectResource.allocatedPower(),
                        eatingGod.allocatedPower(),
                        BASIC_RELATIVE_POWER_PERCENT
                )
        ) {

            return TenGodCorePatternLevel.MEANINGFUL;
        }

        return TenGodCorePatternLevel.REFERENCE;
    }

    /*
     * =================================================
     * 재생관
     * =================================================
     */

    public TenGodCorePatternLevel
    determineWealthGeneratesDirectOfficer(

            int wealthFamilyPower,

            int wealthFamilyShareBasisPoints,

            TenGodPowerSummary directOfficer,

            TenGodFamilyChainAnalysis chainAnalysis

    ) {

        if (
                wealthFamilyPower <= 0
                        || !directOfficer.present()
        ) {

            return null;
        }

        boolean meaningful =
                chainAnalysis
                        .hasMeaningfulInteraction(
                                DayMasterElementRole.WEALTH,
                                DayMasterElementRole.OFFICER,
                                TenGodFamilyInteractionType.GENERATION
                        );

        boolean strong =
                chainAnalysis
                        .hasStrongInteraction(
                                DayMasterElementRole.WEALTH,
                                DayMasterElementRole.OFFICER,
                                TenGodFamilyInteractionType.GENERATION
                        );

        if (
                strong
                        && directOfficer.meaningful()
                        && wealthFamilyShareBasisPoints
                        >= MEANINGFUL_FAMILY_SHARE_BASIS_POINTS
        ) {

            return TenGodCorePatternLevel.STRONG;
        }

        if (
                meaningful
                        && (
                        directOfficer.meaningful()
                                || directOfficer.visible()
                                || directOfficer.monthPresent()
                )
        ) {

            return TenGodCorePatternLevel.MEANINGFUL;
        }

        return TenGodCorePatternLevel.REFERENCE;
    }

    /*
     * =================================================
     * 관인상생
     *
     * 정관 + 정인을 우선 핵심 조합으로 사용한다.
     * =================================================
     */

    public TenGodCorePatternLevel
    determineDirectOfficerGeneratesResource(

            TenGodPowerSummary directOfficer,

            TenGodPowerSummary directResource,

            TenGodFamilyChainAnalysis chainAnalysis

    ) {

        if (
                !directOfficer.present()
                        || !directResource.present()
        ) {

            return null;
        }

        boolean meaningful =
                chainAnalysis
                        .hasMeaningfulInteraction(
                                DayMasterElementRole.OFFICER,
                                DayMasterElementRole.RESOURCE,
                                TenGodFamilyInteractionType.GENERATION
                        );

        if (
                meaningful
                        && directOfficer.meaningful()
                        && directResource.meaningful()
        ) {

            return TenGodCorePatternLevel.STRONG;
        }

        if (
                meaningful
                        && (
                        directOfficer.visible()
                                || directResource.visible()
                                || directOfficer.monthPresent()
                                || directResource.monthPresent()
                )
        ) {

            return TenGodCorePatternLevel.MEANINGFUL;
        }

        return TenGodCorePatternLevel.REFERENCE;
    }

    /*
     * =================================================
     * 살인상생
     *
     * 칠살 + 인성 Family.
     * =================================================
     */

    public TenGodCorePatternLevel
    determineSevenKillingsGeneratesResource(

            TenGodPowerSummary sevenKillings,

            int resourceFamilyPower,

            int resourceFamilyShareBasisPoints,

            TenGodFamilyChainAnalysis chainAnalysis

    ) {

        if (
                !sevenKillings.present()
                        || resourceFamilyPower <= 0
        ) {

            return null;
        }

        boolean meaningful =
                chainAnalysis
                        .hasMeaningfulInteraction(
                                DayMasterElementRole.OFFICER,
                                DayMasterElementRole.RESOURCE,
                                TenGodFamilyInteractionType.GENERATION
                        );

        if (
                meaningful
                        && sevenKillings.meaningful()
                        && resourceFamilyShareBasisPoints
                        >= MEANINGFUL_FAMILY_SHARE_BASIS_POINTS
        ) {

            return TenGodCorePatternLevel.STRONG;
        }

        if (
                meaningful
                        && (
                        sevenKillings.meaningful()
                                || sevenKillings.visible()
                                || sevenKillings.monthPresent()
                )
        ) {

            return TenGodCorePatternLevel.MEANINGFUL;
        }

        return TenGodCorePatternLevel.REFERENCE;
    }

    /*
     * =================================================
     * 재극인
     * =================================================
     */

    public TenGodCorePatternLevel
    determineWealthControlsResource(

            int wealthFamilyPower,

            int resourceFamilyPower,

            int wealthFamilyShareBasisPoints,

            int resourceFamilyShareBasisPoints,

            TenGodFamilyChainAnalysis chainAnalysis

    ) {

        if (
                wealthFamilyPower <= 0
                        || resourceFamilyPower <= 0
        ) {

            return null;
        }

        boolean meaningful =
                chainAnalysis
                        .hasMeaningfulInteraction(
                                DayMasterElementRole.WEALTH,
                                DayMasterElementRole.RESOURCE,
                                TenGodFamilyInteractionType.CONTROL
                        );

        boolean strong =
                chainAnalysis
                        .hasStrongInteraction(
                                DayMasterElementRole.WEALTH,
                                DayMasterElementRole.RESOURCE,
                                TenGodFamilyInteractionType.CONTROL
                        );

        if (
                strong
                        && wealthFamilyShareBasisPoints
                        >= MEANINGFUL_FAMILY_SHARE_BASIS_POINTS
                        && resourceFamilyShareBasisPoints
                        >= MEANINGFUL_FAMILY_SHARE_BASIS_POINTS
                        && reachesPercent(
                        wealthFamilyPower,
                        resourceFamilyPower,
                        MEANINGFUL_RELATIVE_POWER_PERCENT
                )
        ) {

            return TenGodCorePatternLevel.STRONG;
        }

        if (
                meaningful
        ) {

            return TenGodCorePatternLevel.MEANINGFUL;
        }

        return TenGodCorePatternLevel.REFERENCE;
    }

    /*
     * =================================================
     * 비겁쟁재
     * =================================================
     */

    public TenGodCorePatternLevel
    determineCompanionContestsWealth(

            CompanionCompositeAnalysis companionAnalysis,

            WealthCompositeAnalysis wealthAnalysis,

            TenGodPowerSummary robWealth,

            TenGodFamilyChainAnalysis chainAnalysis

    ) {

        if (
                companionAnalysis.companionFamilyPower() <= 0
                        || wealthAnalysis.wealthFamilyPower() <= 0
        ) {

            return null;
        }

        boolean familyControl =
                chainAnalysis
                        .hasMeaningfulInteraction(
                                DayMasterElementRole.COMPANION,
                                DayMasterElementRole.WEALTH,
                                TenGodFamilyInteractionType.CONTROL
                        );

        boolean companionSide =
                companionAnalysis
                        .hasMeaningfulPattern(
                                CompanionCompositePatternType.WEALTH_CONTEST
                        );

        boolean wealthSide =
                wealthAnalysis
                        .hasMeaningfulPattern(
                                WealthCompositePatternType.COMPANION_PRESSURED_WEALTH
                        );

        if (
                familyControl
                        && companionSide
                        && wealthSide
                        && (
                        robWealth.meaningful()

                                || companionAnalysis
                                .companionFamilyPower()
                                >= wealthAnalysis
                                .wealthFamilyPower()
                )
        ) {

            return TenGodCorePatternLevel.STRONG;
        }

        if (
                familyControl
                        && (
                        companionSide
                                || wealthSide
                )
        ) {

            return TenGodCorePatternLevel.MEANINGFUL;
        }

        return TenGodCorePatternLevel.REFERENCE;
    }

    /*
     * =================================================
     * 재다신약
     * =================================================
     */

    public TenGodCorePatternLevel
    determineWealthHeavyWeakDayMaster(

            WealthCompositeAnalysis wealthAnalysis

    ) {

        if (
                !weakSide(
                        wealthAnalysis.dayMasterStrengthLevel()
                )
                        || wealthAnalysis.wealthFamilyPower() <= 0
        ) {

            return null;
        }

        if (
                wealthAnalysis
                        .hasStrongPattern(
                                WealthCompositePatternType
                                        .WEAK_DAY_MASTER_WEALTH_BURDEN
                        )
        ) {

            return TenGodCorePatternLevel.STRONG;
        }

        if (
                wealthAnalysis
                        .hasMeaningfulPattern(
                                WealthCompositePatternType
                                        .WEAK_DAY_MASTER_WEALTH_BURDEN
                        )
        ) {

            return TenGodCorePatternLevel.MEANINGFUL;
        }

        return TenGodCorePatternLevel.REFERENCE;
    }

    /*
     * =================================================
     * 관살혼재
     * =================================================
     */

    public TenGodCorePatternLevel
    determineMixedOfficerKillings(

            OfficerCompositeAnalysis officerAnalysis

    ) {

        if (
                !officerAnalysis.directOfficerPresent()
                        || !officerAnalysis.sevenKillingsPresent()
        ) {

            return null;
        }

        if (
                officerAnalysis
                        .hasStrongPattern(
                                OfficerCompositePatternType
                                        .MIXED_OFFICER_KILLINGS
                        )
        ) {

            return TenGodCorePatternLevel.STRONG;
        }

        if (
                officerAnalysis
                        .hasMeaningfulPattern(
                                OfficerCompositePatternType
                                        .MIXED_OFFICER_KILLINGS
                        )
        ) {

            return TenGodCorePatternLevel.MEANINGFUL;
        }

        return TenGodCorePatternLevel.REFERENCE;
    }

    /*
     * =================================================
     * Reason
     * =================================================
     */

    public List<TenGodCorePatternReasonType>
    buildReasons(

            TenGodCorePatternType patternType,

            TenGodCorePatternLevel level,

            int primaryPower,

            int secondaryPower,

            int primaryShareBasisPoints,

            int secondaryShareBasisPoints,

            boolean primaryMeaningful,

            boolean secondaryMeaningful,

            boolean primaryVisible,

            boolean secondaryVisible,

            boolean primaryMonthPresent,

            boolean secondaryMonthPresent,

            boolean directionalInteractionPresent,

            boolean directionalInteractionMeaningful,

            boolean directionalInteractionStrong,

            boolean weakDayMaster,

            boolean crossAnalysisCorroborated

    ) {

        Objects.requireNonNull(
                patternType,
                "Pattern 종류가 필요합니다."
        );

        Objects.requireNonNull(
                level,
                "Pattern 수준이 필요합니다."
        );

        List<TenGodCorePatternReasonType> reasons =
                new ArrayList<>();

        if (
                primaryPower > 0
        ) {

            reasons.add(
                    TenGodCorePatternReasonType
                            .PRIMARY_TEN_GOD_PRESENT
            );
        }

        if (
                secondaryPower > 0
        ) {

            reasons.add(
                    TenGodCorePatternReasonType
                            .SECONDARY_TEN_GOD_PRESENT
            );
        }

        if (
                primaryMeaningful
        ) {

            reasons.add(
                    TenGodCorePatternReasonType
                            .PRIMARY_TEN_GOD_MEANINGFUL
            );
        }

        if (
                secondaryMeaningful
        ) {

            reasons.add(
                    TenGodCorePatternReasonType
                            .SECONDARY_TEN_GOD_MEANINGFUL
            );
        }

        if (
                primaryMeaningful
                        && secondaryMeaningful
        ) {

            reasons.add(
                    TenGodCorePatternReasonType
                            .BOTH_SIDES_MEANINGFUL
            );
        }

        if (
                primaryVisible
        ) {

            reasons.add(
                    TenGodCorePatternReasonType
                            .PRIMARY_TEN_GOD_VISIBLE
            );
        }

        if (
                secondaryVisible
        ) {

            reasons.add(
                    TenGodCorePatternReasonType
                            .SECONDARY_TEN_GOD_VISIBLE
            );
        }

        if (
                primaryMonthPresent
        ) {

            reasons.add(
                    TenGodCorePatternReasonType
                            .PRIMARY_MONTH_ANCHORED
            );
        }

        if (
                secondaryMonthPresent
        ) {

            reasons.add(
                    TenGodCorePatternReasonType
                            .SECONDARY_MONTH_ANCHORED
            );
        }

        if (
                reachesPercent(
                        primaryPower,
                        secondaryPower,
                        MEANINGFUL_RELATIVE_POWER_PERCENT
                )
        ) {

            reasons.add(
                    TenGodCorePatternReasonType
                            .PRIMARY_APPROACHES_SECONDARY_POWER
            );
        }

        if (
                primaryPower > 0
                        && secondaryPower > 0
        ) {

            if (
                    primaryPower > secondaryPower
            ) {

                reasons.add(
                        TenGodCorePatternReasonType
                                .PRIMARY_EXCEEDS_SECONDARY_POWER
                );

            } else if (
                    secondaryPower > primaryPower
            ) {

                reasons.add(
                        TenGodCorePatternReasonType
                                .SECONDARY_EXCEEDS_PRIMARY_POWER
                );
            }
        }

        if (
                directionalInteractionPresent
        ) {

            reasons.add(
                    TenGodCorePatternReasonType
                            .FAMILY_INTERACTION_PRESENT
            );
        }

        if (
                directionalInteractionMeaningful
        ) {

            reasons.add(
                    TenGodCorePatternReasonType
                            .FAMILY_INTERACTION_MEANINGFUL
            );
        }

        if (
                directionalInteractionStrong
        ) {

            reasons.add(
                    TenGodCorePatternReasonType
                            .FAMILY_INTERACTION_STRONG
            );
        }

        if (
                weakDayMaster
        ) {

            reasons.add(
                    TenGodCorePatternReasonType
                            .WEAK_DAY_MASTER
            );
        }

        if (
                crossAnalysisCorroborated
        ) {

            switch (
                    patternType
            ) {

                case COMPANION_CONTESTS_WEALTH -> {

                    reasons.add(
                            TenGodCorePatternReasonType
                                    .COMPANION_WEALTH_CONTEST_CORROBORATED
                    );

                    reasons.add(
                            TenGodCorePatternReasonType
                                    .WEALTH_PRESSURE_CORROBORATED
                    );
                }

                case WEALTH_HEAVY_WEAK_DAY_MASTER ->
                        reasons.add(
                                TenGodCorePatternReasonType
                                        .WEALTH_BURDEN_CORROBORATED
                        );

                case MIXED_DIRECT_OFFICER_AND_SEVEN_KILLINGS ->
                        reasons.add(
                                TenGodCorePatternReasonType
                                        .OFFICER_KILLINGS_MIXTURE_CORROBORATED
                        );

                case EATING_GOD_CONTROLS_SEVEN_KILLINGS,
                     HURTING_OFFICER_MEETS_DIRECT_OFFICER ->
                        reasons.add(
                                TenGodCorePatternReasonType
                                        .OUTPUT_CONTROL_CONTEXT_CORROBORATED
                        );

                case HURTING_OFFICER_WITH_RESOURCE,
                     INDIRECT_RESOURCE_SEIZES_EATING_GOD ->
                        reasons.add(
                                TenGodCorePatternReasonType
                                        .RESOURCE_OUTPUT_CONTEXT_CORROBORATED
                        );

                case DIRECT_OFFICER_GENERATES_RESOURCE,
                     SEVEN_KILLINGS_GENERATES_RESOURCE ->
                        reasons.add(
                                TenGodCorePatternReasonType
                                        .OFFICER_RESOURCE_CONTEXT_CORROBORATED
                        );

                case WEALTH_GENERATES_DIRECT_OFFICER ->
                        reasons.add(
                                TenGodCorePatternReasonType
                                        .WEALTH_OFFICER_CONTEXT_CORROBORATED
                        );

                case WEALTH_CONTROLS_RESOURCE ->
                        reasons.add(
                                TenGodCorePatternReasonType
                                        .WEALTH_RESOURCE_CONTEXT_CORROBORATED
                        );
            }
        }

        switch (
                patternType
        ) {

            case EATING_GOD_CONTROLS_SEVEN_KILLINGS ->
                    reasons.add(
                            TenGodCorePatternReasonType
                                    .EATING_GOD_CONTROLS_SEVEN_KILLINGS_PATTERN
                    );

            case HURTING_OFFICER_MEETS_DIRECT_OFFICER ->
                    reasons.add(
                            TenGodCorePatternReasonType
                                    .HURTING_OFFICER_MEETS_DIRECT_OFFICER_PATTERN
                    );

            case HURTING_OFFICER_WITH_RESOURCE ->
                    reasons.add(
                            TenGodCorePatternReasonType
                                    .HURTING_OFFICER_WITH_RESOURCE_PATTERN
                    );

            case INDIRECT_RESOURCE_SEIZES_EATING_GOD ->
                    reasons.add(
                            TenGodCorePatternReasonType
                                    .INDIRECT_RESOURCE_SEIZES_EATING_GOD_PATTERN
                    );

            case WEALTH_GENERATES_DIRECT_OFFICER ->
                    reasons.add(
                            TenGodCorePatternReasonType
                                    .WEALTH_GENERATES_DIRECT_OFFICER_PATTERN
                    );

            case DIRECT_OFFICER_GENERATES_RESOURCE ->
                    reasons.add(
                            TenGodCorePatternReasonType
                                    .DIRECT_OFFICER_GENERATES_RESOURCE_PATTERN
                    );

            case SEVEN_KILLINGS_GENERATES_RESOURCE ->
                    reasons.add(
                            TenGodCorePatternReasonType
                                    .SEVEN_KILLINGS_GENERATES_RESOURCE_PATTERN
                    );

            case WEALTH_CONTROLS_RESOURCE ->
                    reasons.add(
                            TenGodCorePatternReasonType
                                    .WEALTH_CONTROLS_RESOURCE_PATTERN
                    );

            case COMPANION_CONTESTS_WEALTH ->
                    reasons.add(
                            TenGodCorePatternReasonType
                                    .COMPANION_CONTESTS_WEALTH_PATTERN
                    );

            case WEALTH_HEAVY_WEAK_DAY_MASTER ->
                    reasons.add(
                            TenGodCorePatternReasonType
                                    .WEALTH_HEAVY_WEAK_DAY_MASTER_PATTERN
                    );

            case MIXED_DIRECT_OFFICER_AND_SEVEN_KILLINGS ->
                    reasons.add(
                            TenGodCorePatternReasonType
                                    .MIXED_OFFICER_KILLINGS_PATTERN
                    );
        }

        switch (
                level
        ) {

            case REFERENCE ->
                    reasons.add(
                            TenGodCorePatternReasonType
                                    .REFERENCE_PATTERN
                    );

            case MEANINGFUL ->
                    reasons.add(
                            TenGodCorePatternReasonType
                                    .MEANINGFUL_PATTERN
                    );

            case STRONG ->
                    reasons.add(
                            TenGodCorePatternReasonType
                                    .STRONG_PATTERN
                    );
        }

        reasons.add(
                TenGodCorePatternReasonType
                        .REQUIRES_PHASE_10_9_CROSS_VALIDATION
        );

        return List.copyOf(
                reasons
        );
    }
}