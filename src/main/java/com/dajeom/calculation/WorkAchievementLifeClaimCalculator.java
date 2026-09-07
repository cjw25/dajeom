package com.dajeom.calculation;

import com.dajeom.domain.DayMasterStrengthStatus;
import com.dajeom.domain.LifeClaim;
import com.dajeom.domain.LifeClaimBasisCode;
import com.dajeom.domain.LifeClaimCode;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.NatalMyeongriProfile;
import com.dajeom.domain.NatalPatternDecisionStatus;
import com.dajeom.domain.NatalPatternType;
import com.dajeom.domain.PatternTenGodFoundationProfile;
import com.dajeom.domain.PatternTenGodFoundationState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
public class WorkAchievementLifeClaimCalculator {

    public List<LifeClaim> enrich(

            NatalMyeongriProfile profile,

            List<LifeClaim> existingClaims

    ) {

        Objects.requireNonNull(profile);
        Objects.requireNonNull(existingClaims);

        ClaimCollector claims =
                new ClaimCollector(
                        existingClaims
                );

        PatternTenGodFoundationProfile foundations =
                profile.foundations();

        addResourceWorkClaims(
                foundations,
                claims
        );

        addOutputWorkClaims(
                profile,
                foundations,
                claims
        );

        addWealthWorkClaims(
                profile,
                foundations,
                claims
        );

        addAuthorityWorkClaims(
                profile,
                foundations,
                claims
        );

        addCompanionWorkClaims(
                profile,
                foundations,
                claims
        );

        addStrengthWorkClaims(
                profile,
                claims
        );

        addPatternWorkClaims(
                profile,
                claims
        );

        addBindingWorkClaims(
                profile,
                claims
        );

        addStructuralFrictionClaims(
                profile,
                claims
        );

        return claims.toClaims();
    }

    private void addResourceWorkClaims(

            PatternTenGodFoundationProfile foundations,

            ClaimCollector claims

    ) {

        if (
                !familyPresent(
                        foundations,
                        MonthCommandPatternCandidateType.DIRECT_RESOURCE,
                        MonthCommandPatternCandidateType.INDIRECT_RESOURCE
                )
        ) {

            return;
        }

        EnumSet<LifeClaimBasisCode> bases =
                EnumSet.of(
                        LifeClaimBasisCode.RESOURCE_FAMILY_PRESENT
                );

        if (
                foundations
                        .resourceFoundationStable()
        ) {

            bases.add(
                    LifeClaimBasisCode.RESOURCE_FOUNDATION_CONFIRMED
            );
        }

        claims.add(
                LifeClaimCode
                        .PREPARATION_AND_REFLECTION_SUPPORT_STABILITY,
                bases
        );
    }

    private void addOutputWorkClaims(

            NatalMyeongriProfile profile,

            PatternTenGodFoundationProfile foundations,

            ClaimCollector claims

    ) {

        if (
                !familyPresent(
                        foundations,
                        MonthCommandPatternCandidateType.EATING_GOD,
                        MonthCommandPatternCandidateType.HURTING_OFFICER
                )
        ) {

            return;
        }

        EnumSet<LifeClaimBasisCode> bases =
                EnumSet.of(
                        LifeClaimBasisCode.OUTPUT_FAMILY_PRESENT
                );

        if (
                foundations
                        .eatingGodFoundationStable()

                        ||

                        foundations
                                .hurtingOfficerFoundationStable()
        ) {

            bases.add(
                    LifeClaimBasisCode.OUTPUT_FOUNDATION_CONFIRMED
            );
        }

        claims.add(
                LifeClaimCode
                        .EXPRESSION_NEEDS_A_CONSTRUCTIVE_OUTLET,
                bases
        );

        if (
                profile
                        .outputDrainsWeakStructure()
        ) {

            claims.add(
                    LifeClaimCode
                            .OUTPUT_CAN_CONSUME_PERSONAL_RESOURCES,

                    LifeClaimBasisCode
                            .OUTPUT_DRAINS_WEAK_STRUCTURE
            );

            claims.add(
                    LifeClaimCode
                            .RECOVERY_BEFORE_OVEREXTENSION,

                    LifeClaimBasisCode
                            .GENERAL_STRENGTH_WEAK_SIDE,

                    LifeClaimBasisCode
                            .OUTPUT_DRAINS_WEAK_STRUCTURE
            );
        }

        if (
                profile
                        .hurtingOfficerTooLightForResourceRoute()
        ) {

            claims.add(
                    LifeClaimCode
                            .OVERCONTROL_CAN_SUPPRESS_EXPRESSION,

                    LifeClaimBasisCode
                            .HURTING_OFFICER_TOO_LIGHT_FOR_RESOURCE_ROUTE
            );
        }
    }

    private void addWealthWorkClaims(

            NatalMyeongriProfile profile,

            PatternTenGodFoundationProfile foundations,

            ClaimCollector claims

    ) {

        if (
                !familyPresent(
                        foundations,
                        MonthCommandPatternCandidateType.DIRECT_WEALTH,
                        MonthCommandPatternCandidateType.INDIRECT_WEALTH
                )
        ) {

            return;
        }

        EnumSet<LifeClaimBasisCode> bases =
                EnumSet.of(
                        LifeClaimBasisCode.WEALTH_FAMILY_PRESENT
                );

        if (
                foundations
                        .wealthFoundationStable()
        ) {

            bases.add(
                    LifeClaimBasisCode.WEALTH_FOUNDATION_CONFIRMED
            );
        }

        claims.add(
                LifeClaimCode
                        .PRACTICAL_RESOURCE_MANAGEMENT_IS_A_RECURRING_THEME,
                bases
        );

        if (
                foundations
                        .wealthFoundationStable()
        ) {

            claims.add(
                    LifeClaimCode
                            .MATERIAL_RESULTS_ARE_IMPORTANT_MOTIVATORS,

                    LifeClaimBasisCode.WEALTH_FAMILY_PRESENT,
                    LifeClaimBasisCode.WEALTH_FOUNDATION_CONFIRMED
            );
        }

        if (
                profile
                        .wealthModeratesExcessResource()
        ) {

            claims.add(
                    LifeClaimCode
                            .PRACTICAL_GOALS_CAN_CORRECT_OVERPROTECTION,

                    LifeClaimBasisCode.EXCESSIVE_RESOURCE_CONTEXT,
                    LifeClaimBasisCode.WEALTH_MODERATES_EXCESS_RESOURCE
            );

            claims.add(
                    LifeClaimCode
                            .BALANCE_STABILITY_AND_MATERIAL_GOALS,

                    LifeClaimBasisCode
                            .WEALTH_MODERATES_EXCESS_RESOURCE
            );

            return;
        }

        if (
                profile
                        .wealthResourceDirectConflictActive()
        ) {

            claims.add(
                    LifeClaimCode
                            .MATERIAL_PRESSURE_CAN_DISRUPT_STABILITY,

                    LifeClaimBasisCode
                            .WEALTH_RESOURCE_DIRECT_CONFLICT
            );
        }

        if (
                profile
                        .wealthResourceMutuallyNonObstructing()
        ) {

            claims.add(
                    LifeClaimCode
                            .MATERIAL_GOALS_AND_STABILITY_CAN_COEXIST,

                    LifeClaimBasisCode
                            .WEALTH_RESOURCE_MUTUALLY_NON_OBSTRUCTING
            );
        }
    }

    /*
     * 官殺 final authority:
     * PatternOfficerKillingsPurificationAnalysis
     */

    private void addAuthorityWorkClaims(

            NatalMyeongriProfile profile,

            PatternTenGodFoundationProfile foundations,

            ClaimCollector claims

    ) {

        if (
                !familyPresent(
                        foundations,
                        MonthCommandPatternCandidateType.DIRECT_OFFICER,
                        MonthCommandPatternCandidateType.SEVEN_KILLINGS
                )
        ) {

            return;
        }

        EnumSet<LifeClaimBasisCode> bases =
                EnumSet.of(
                        LifeClaimBasisCode.AUTHORITY_FAMILY_PRESENT
                );

        if (
                foundations
                        .directOfficerFoundationStable()

                        ||

                        foundations
                                .sevenKillingsFoundationStable()
        ) {

            bases.add(
                    LifeClaimBasisCode.AUTHORITY_FOUNDATION_CONFIRMED
            );
        }

        claims.add(
                LifeClaimCode
                        .RESPONSIBILITY_AND_EXPECTATIONS_SHAPE_CHOICES,
                bases
        );

        if (
                profile
                        .officerKillingsPurification()
                        .mixed()
        ) {

            claims.add(
                    LifeClaimCode
                            .COMPLEX_AUTHORITY_EXPECTATIONS_REQUIRE_CLARITY,

                    LifeClaimBasisCode
                            .OFFICER_KILLINGS_MIXED
            );
        }

        if (
                profile
                        .officerKillingsPurification()
                        .mixtureCleared()
        ) {

            claims.add(
                    LifeClaimCode
                            .AUTHORITY_COMPLEXITY_CAN_BE_CHANNELLED,

                    LifeClaimBasisCode
                            .OFFICER_KILLINGS_MIXTURE_CLEARED
            );
        }

        if (
                profile
                        .officerKillingsPurification()
                        .reviewRequired()
        ) {

            claims.add(
                    LifeClaimCode
                            .MULTIPLE_STRUCTURAL_DIRECTIONS_REQUIRE_CONTEXT,

                    LifeClaimBasisCode
                            .OFFICER_KILLINGS_REVIEW_REQUIRED
            );
        }
    }

    private void addCompanionWorkClaims(

            NatalMyeongriProfile profile,

            PatternTenGodFoundationProfile foundations,

            ClaimCollector claims

    ) {

        if (
                familyPresent(
                        foundations,
                        MonthCommandPatternCandidateType.MONTH_COMPANION,
                        MonthCommandPatternCandidateType.MONTH_ROB_WEALTH
                )
        ) {

            EnumSet<LifeClaimBasisCode> bases =
                    EnumSet.of(
                            LifeClaimBasisCode.COMPANION_FAMILY_PRESENT
                    );

            if (
                    foundations
                            .foundationStable(
                                    MonthCommandPatternCandidateType.MONTH_COMPANION
                            )

                            ||

                            foundations
                                    .foundationStable(
                                            MonthCommandPatternCandidateType.MONTH_ROB_WEALTH
                                    )
            ) {

                bases.add(
                        LifeClaimBasisCode.COMPANION_FOUNDATION_CONFIRMED
                );
            }

            claims.add(
                    LifeClaimCode
                            .AUTONOMY_AND_PEER_BOUNDARIES_MATTER,
                    bases
            );
        }

        if (
                profile
                        .hasYangBlade()
        ) {

            claims.add(
                    LifeClaimCode
                            .DECISIVE_INDEPENDENT_DRIVE,

                    LifeClaimBasisCode.YANG_BLADE_PRESENT
            );
        }

        if (
                profile
                        .hasMonthYangBlade()
        ) {

            claims.add(
                    LifeClaimCode
                            .STRONG_DRIVE_BENEFITS_FROM_CLEAR_RESPONSIBILITY,

                    LifeClaimBasisCode.MONTH_YANG_BLADE_PRESENT
            );
        }

        if (
                profile
                        .hasActiveYangBladeKillingCombination()
        ) {

            claims.add(
                    LifeClaimCode
                            .COMPETITIVE_DRIVE_NEEDS_DIRECTION,

                    LifeClaimBasisCode
                            .ACTIVE_YANG_BLADE_KILLING_COMBINATION
            );

            claims.add(
                    LifeClaimCode
                            .STRONG_DRIVE_BENEFITS_FROM_CLEAR_RESPONSIBILITY,

                    LifeClaimBasisCode
                            .ACTIVE_YANG_BLADE_KILLING_COMBINATION
            );
        }
    }

    private void addStrengthWorkClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

        if (
                profile
                        .followingStructureEstablished()
        ) {

            claims.add(
                    LifeClaimCode
                            .DOMINANT_CONTEXT_STRONGLY_SHAPES_DECISIONS,

                    LifeClaimBasisCode
                            .FOLLOWING_STRUCTURE_ESTABLISHED
            );

            return;
        }

        if (
                profile
                        .trueTransformationStructure()
        ) {

            claims.add(
                    LifeClaimCode
                            .STRUCTURAL_ALIGNMENT_CHANGES_HOW_STRENGTH_IS_EXPRESSED,

                    LifeClaimBasisCode
                            .TRUE_TRANSFORMATION_STRUCTURE
            );

            return;
        }

        if (
                !profile
                        .generalStrengthUsed()
        ) {

            return;
        }

        DayMasterStrengthStatus status =
                profile
                        .strengthSensitiveContext()
                        .strengthStatus();

        if (
                status == DayMasterStrengthStatus.STRONG_LEANING
                        ||
                        status == DayMasterStrengthStatus.CLEARLY_STRONG
        ) {

            claims.add(
                    LifeClaimCode
                            .SELF_DIRECTION_IS_EASIER_TO_SUSTAIN,

                    LifeClaimBasisCode
                            .GENERAL_STRENGTH_STRONG_SIDE
            );
        }

        if (
                status == DayMasterStrengthStatus.WEAK_LEANING
                        ||
                        status == DayMasterStrengthStatus.CLEARLY_WEAK
        ) {

            claims.add(
                    LifeClaimCode
                            .PACING_AND_SUPPORT_MATTER_FOR_SUSTAINED_OUTPUT,

                    LifeClaimBasisCode
                            .GENERAL_STRENGTH_WEAK_SIDE
            );
        }

        if (
                profile
                        .specialStrengthContextExists()
        ) {

            claims.add(
                    LifeClaimCode
                            .BASELINE_TENDENCIES_REQUIRE_CONTEXTUAL_ADJUSTMENT,

                    LifeClaimBasisCode
                            .GENERAL_STRENGTH_SPECIAL_CONTEXT
            );
        }

        if (
                profile
                        .excessiveResourceContext()
        ) {

            claims.add(
                    LifeClaimCode
                            .TOO_MUCH_SUPPORT_CAN_DELAY_ACTION,

                    LifeClaimBasisCode
                            .EXCESSIVE_RESOURCE_CONTEXT
            );
        }
    }

    private void addPatternWorkClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

        if (
                !profile
                        .patternDecided()
        ) {

            claims.add(
                    LifeClaimCode
                            .MULTIPLE_STRUCTURAL_DIRECTIONS_REQUIRE_CONTEXT,

                    LifeClaimBasisCode
                            .PATTERN_NOT_YET_SELECTED
            );

            return;
        }

        NatalPatternType type =
                profile
                        .decidedPatternType()
                        .orElse(null);

        NatalPatternDecisionStatus status =
                profile
                        .decidedPatternStatus()
                        .orElse(null);

        if (
                type == null
                        ||
                        status == null
        ) {

            return;
        }

        switch (type) {

            case DIRECT_OFFICER,
                 SEVEN_KILLINGS ->

                    claims.add(
                            LifeClaimCode
                                    .RESPONSIBILITY_AND_EXPECTATIONS_SHAPE_CHOICES,

                            LifeClaimBasisCode
                                    .AUTHORITY_FAMILY_PRESENT
                    );

            case DIRECT_WEALTH,
                 INDIRECT_WEALTH -> {

                claims.add(
                        LifeClaimCode
                                .PRACTICAL_RESOURCE_MANAGEMENT_IS_A_RECURRING_THEME,

                        LifeClaimBasisCode
                                .WEALTH_FAMILY_PRESENT
                );

                claims.add(
                        LifeClaimCode
                                .MATERIAL_RESULTS_ARE_IMPORTANT_MOTIVATORS,

                        LifeClaimBasisCode
                                .WEALTH_FAMILY_PRESENT
                );
            }

            case EATING_GOD,
                 HURTING_OFFICER ->

                    claims.add(
                            LifeClaimCode
                                    .EXPRESSION_NEEDS_A_CONSTRUCTIVE_OUTLET,

                            LifeClaimBasisCode
                                    .OUTPUT_FAMILY_PRESENT
                    );

            case DIRECT_RESOURCE,
                 INDIRECT_RESOURCE ->

                    claims.add(
                            LifeClaimCode
                                    .PREPARATION_AND_REFLECTION_SUPPORT_STABILITY,

                            LifeClaimBasisCode
                                    .RESOURCE_FAMILY_PRESENT
                    );

            case JIANLU,
                 YUEJIE ->

                    claims.add(
                            LifeClaimCode
                                    .AUTONOMY_AND_PEER_BOUNDARIES_MATTER,

                            LifeClaimBasisCode
                                    .COMPANION_FAMILY_PRESENT
                    );

            case YANG_BLADE ->

                    claims.add(
                            LifeClaimCode
                                    .STRONG_DRIVE_BENEFITS_FROM_CLEAR_RESPONSIBILITY,

                            LifeClaimBasisCode
                                    .MONTH_YANG_BLADE_PRESENT
                    );
        }

        switch (status) {

            case FORMED ->
                    claims.add(
                            LifeClaimCode.LIFE_DIRECTION_HAS_A_COHERENT_STRUCTURE,
                            LifeClaimBasisCode.PATTERN_FORMED
                    );

            case FORMED_WITH_DEFECT ->
                    claims.add(
                            LifeClaimCode.CORE_STRENGTH_COMES_WITH_A_MANAGEABLE_DEFECT,
                            LifeClaimBasisCode.PATTERN_FORMED_WITH_DEFECT
                    );

            case BROKEN ->
                    claims.add(
                            LifeClaimCode.CORE_STRUCTURE_REQUIRES_ACTIVE_REORGANIZATION,
                            LifeClaimBasisCode.PATTERN_BROKEN
                    );

            case BROKEN_WITH_RESCUE ->
                    claims.add(
                            LifeClaimCode.DIFFICULTY_CAN_BE_CONVERTED_THROUGH_COMPENSATION,
                            LifeClaimBasisCode.PATTERN_BROKEN_WITH_RESCUE
                    );

            case REVIEW_REQUIRED ->
                    claims.add(
                            LifeClaimCode.MULTIPLE_STRUCTURAL_DIRECTIONS_REQUIRE_CONTEXT,
                            LifeClaimBasisCode.PATTERN_REVIEW_REQUIRED
                    );

            case UNFORMED ->
                    claims.add(
                            LifeClaimCode.LIFE_DIRECTION_DEPENDS_MORE_ON_CHOSEN_STRUCTURE,
                            LifeClaimBasisCode.PATTERN_UNFORMED
                    );
        }
    }

    private void addBindingWorkClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

        if (
                profile
                        .hasActiveNonDayMasterStemCombination()
        ) {

            claims.add(
                    LifeClaimCode
                            .STRONG_BINDING_DYNAMICS_CAN_REDIRECT_PRIORITIES,

                    LifeClaimBasisCode
                            .ACTIVE_NON_DAY_MASTER_STEM_COMBINATION
            );
        }

        if (
                profile
                        .sevenKillingsClearedPreservingWealth()
        ) {

            claims.add(
                    LifeClaimCode
                            .PRESSURE_CAN_BE_REDUCED_WITHOUT_LOSING_PRACTICAL_GOALS,

                    LifeClaimBasisCode
                            .SEVEN_KILLINGS_CLEARED_PRESERVING_WEALTH
            );
        }

        if (
                profile
                        .wealthClearedPreservingResource()
        ) {

            claims.add(
                    LifeClaimCode
                            .STABILITY_CAN_BE_PRESERVED_WHEN_MATERIAL_PRESSURE_IS_REDUCED,

                    LifeClaimBasisCode
                            .WEALTH_CLEARED_PRESERVING_RESOURCE
            );
        }
    }

    private void addStructuralFrictionClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

        if (
                profile
                        .monthCommandDisrupted()
        ) {

            claims.add(
                    LifeClaimCode.CORE_LIFE_PATTERN_HAS_INTERNAL_FRICTION,
                    LifeClaimBasisCode.MONTH_COMMAND_DISRUPTED
            );
        }

        if (
                profile
                        .monthCommandDisruptionResolved()
        ) {

            claims.add(
                    LifeClaimCode.INTERNAL_FRICTION_HAS_A_RESOLUTION_ROUTE,
                    LifeClaimBasisCode.MONTH_COMMAND_DISRUPTED,
                    LifeClaimBasisCode.MONTH_COMMAND_DISRUPTION_RESOLVED
            );
        }

        if (
                profile
                        .monthCommandDisruptionReviewRequired()
        ) {

            claims.add(
                    LifeClaimCode.MULTIPLE_STRUCTURAL_DIRECTIONS_REQUIRE_CONTEXT,
                    LifeClaimBasisCode.MONTH_COMMAND_DISRUPTION_REVIEW_REQUIRED
            );
        }
    }

    private boolean familyPresent(

            PatternTenGodFoundationProfile foundations,

            MonthCommandPatternCandidateType first,

            MonthCommandPatternCandidateType second

    ) {

        return foundationPresent(
                foundations,
                first
        )
                ||
                foundationPresent(
                        foundations,
                        second
                );
    }

    private boolean foundationPresent(

            PatternTenGodFoundationProfile foundations,

            MonthCommandPatternCandidateType type

    ) {

        return foundations
                .foundationOf(type)
                .state()
                != PatternTenGodFoundationState.ABSENT;
    }

    private static final class ClaimCollector {

        private final Map<
                LifeClaimCode,
                EnumSet<LifeClaimBasisCode>
                > basesByCode =
                new EnumMap<>(
                        LifeClaimCode.class
                );

        private ClaimCollector(
                List<LifeClaim> existingClaims
        ) {

            for (
                    LifeClaim claim
                    : existingClaims
            ) {

                add(
                        claim.code(),
                        claim.bases()
                );
            }
        }

        private void add(

                LifeClaimCode code,

                LifeClaimBasisCode... bases

        ) {

            EnumSet<LifeClaimBasisCode> merged =
                    basesByCode
                            .computeIfAbsent(
                                    Objects.requireNonNull(code),
                                    ignored ->
                                            EnumSet.noneOf(
                                                    LifeClaimBasisCode.class
                                            )
                            );

            if (
                    bases == null
                            ||
                            bases.length == 0
            ) {

                throw new IllegalArgumentException(
                        "basis가 필요합니다."
                );
            }

            for (
                    LifeClaimBasisCode basis
                    : bases
            ) {

                merged.add(
                        Objects.requireNonNull(basis)
                );
            }
        }

        private void add(

                LifeClaimCode code,

                Set<LifeClaimBasisCode> bases

        ) {

            if (
                    bases == null
                            ||
                            bases.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "basis가 필요합니다."
                );
            }

            EnumSet<LifeClaimBasisCode> merged =
                    basesByCode
                            .computeIfAbsent(
                                    Objects.requireNonNull(code),
                                    ignored ->
                                            EnumSet.noneOf(
                                                    LifeClaimBasisCode.class
                                            )
                            );

            merged.addAll(bases);
        }

        private List<LifeClaim> toClaims() {

            List<LifeClaim> result =
                    new ArrayList<>();

            for (
                    LifeClaimCode code
                    : LifeClaimCode.values()
            ) {

                EnumSet<LifeClaimBasisCode> bases =
                        basesByCode.get(code);

                if (
                        bases == null
                                ||
                                bases.isEmpty()
                ) {

                    continue;
                }

                result.add(
                        new LifeClaim(
                                code,
                                EnumSet.copyOf(bases)
                        )
                );
            }

            return List.copyOf(result);
        }
    }
}