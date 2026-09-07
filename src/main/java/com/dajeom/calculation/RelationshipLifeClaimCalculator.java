package com.dajeom.calculation;

import com.dajeom.domain.LifeClaim;
import com.dajeom.domain.LifeClaimBasisCode;
import com.dajeom.domain.LifeClaimCode;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.NatalMyeongriProfile;
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

/**
 * 가족·인간관계 / 연애·결혼 영역의
 * 원국 LifeClaim 보강 Calculator.
 *
 * 官殺混雜의 최종 정리 여부는
 * PatternOfficerKillingsPurificationAnalysis만 사용한다.
 */
@Component
public class RelationshipLifeClaimCalculator {

    public List<LifeClaim> enrich(

            NatalMyeongriProfile profile,

            List<LifeClaim> existingClaims

    ) {

        Objects.requireNonNull(
                profile,
                "NatalMyeongriProfile은 필수입니다."
        );

        Objects.requireNonNull(
                existingClaims,
                "기존 LifeClaim 목록은 필수입니다."
        );

        ClaimCollector claims =
                new ClaimCollector(
                        existingClaims
                );

        PatternTenGodFoundationProfile foundations =
                profile.foundations();

        addResourceRelationshipClaim(
                foundations,
                claims
        );

        addOutputRelationshipClaim(
                foundations,
                claims
        );

        addAuthorityRelationshipClaim(
                foundations,
                claims
        );

        addCompanionRelationshipClaim(
                foundations,
                claims
        );

        addBindingRelationshipClaim(
                profile,
                claims
        );

        addAuthorityComplexityRelationshipClaims(
                profile,
                claims
        );

        addYangBladeRelationshipClaims(
                profile,
                claims
        );

        addRelationshipFrictionClaims(
                profile,
                claims
        );

        return claims.toClaims();
    }

    private void addResourceRelationshipClaim(

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
                    LifeClaimBasisCode
                            .RESOURCE_FOUNDATION_CONFIRMED
            );
        }

        claims.add(
                LifeClaimCode
                        .PREPARATION_AND_REFLECTION_SUPPORT_STABILITY,
                bases
        );
    }

    private void addOutputRelationshipClaim(

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
                    LifeClaimBasisCode
                            .OUTPUT_FOUNDATION_CONFIRMED
            );
        }

        claims.add(
                LifeClaimCode
                        .EXPRESSION_NEEDS_A_CONSTRUCTIVE_OUTLET,
                bases
        );
    }

    private void addAuthorityRelationshipClaim(

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
                    LifeClaimBasisCode
                            .AUTHORITY_FOUNDATION_CONFIRMED
            );
        }

        claims.add(
                LifeClaimCode
                        .RESPONSIBILITY_AND_EXPECTATIONS_SHAPE_CHOICES,
                bases
        );
    }

    private void addCompanionRelationshipClaim(

            PatternTenGodFoundationProfile foundations,

            ClaimCollector claims

    ) {

        if (
                !familyPresent(
                        foundations,
                        MonthCommandPatternCandidateType.MONTH_COMPANION,
                        MonthCommandPatternCandidateType.MONTH_ROB_WEALTH
                )
        ) {

            return;
        }

        EnumSet<LifeClaimBasisCode> bases =
                EnumSet.of(
                        LifeClaimBasisCode.COMPANION_FAMILY_PRESENT
                );

        boolean stable =
                foundations
                        .foundationStable(
                                MonthCommandPatternCandidateType.MONTH_COMPANION
                        )

                        ||

                        foundations
                                .foundationStable(
                                        MonthCommandPatternCandidateType.MONTH_ROB_WEALTH
                                );

        if (stable) {

            bases.add(
                    LifeClaimBasisCode
                            .COMPANION_FOUNDATION_CONFIRMED
            );
        }

        claims.add(
                LifeClaimCode
                        .AUTONOMY_AND_PEER_BOUNDARIES_MATTER,
                bases
        );
    }

    private void addBindingRelationshipClaim(

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
    }

    /*
     * =================================================
     * 官殺 관련
     * =================================================
     *
     * generic stem binding helper 사용 금지.
     */

    private void addAuthorityComplexityRelationshipClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

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

    private void addYangBladeRelationshipClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

        if (
                profile
                        .hasYangBlade()
        ) {

            claims.add(
                    LifeClaimCode
                            .DECISIVE_INDEPENDENT_DRIVE,

                    LifeClaimBasisCode
                            .YANG_BLADE_PRESENT
            );
        }

        if (
                profile
                        .hasMonthYangBlade()
        ) {

            claims.add(
                    LifeClaimCode
                            .STRONG_DRIVE_BENEFITS_FROM_CLEAR_RESPONSIBILITY,

                    LifeClaimBasisCode
                            .MONTH_YANG_BLADE_PRESENT
            );
        }

        if (
                profile
                        .hasActiveYangBladeKillingCombination()
        ) {

            claims.add(
                    LifeClaimCode
                            .STRONG_DRIVE_BENEFITS_FROM_CLEAR_RESPONSIBILITY,

                    LifeClaimBasisCode
                            .ACTIVE_YANG_BLADE_KILLING_COMBINATION
            );

            claims.add(
                    LifeClaimCode
                            .COMPETITIVE_DRIVE_NEEDS_DIRECTION,

                    LifeClaimBasisCode
                            .ACTIVE_YANG_BLADE_KILLING_COMBINATION
            );
        }
    }

    private void addRelationshipFrictionClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

        if (
                profile
                        .monthCommandDisrupted()
        ) {

            claims.add(
                    LifeClaimCode
                            .CORE_LIFE_PATTERN_HAS_INTERNAL_FRICTION,

                    LifeClaimBasisCode
                            .MONTH_COMMAND_DISRUPTED
            );
        }

        if (
                profile
                        .monthCommandDisruptionResolved()
        ) {

            claims.add(
                    LifeClaimCode
                            .INTERNAL_FRICTION_HAS_A_RESOLUTION_ROUTE,

                    LifeClaimBasisCode
                            .MONTH_COMMAND_DISRUPTED,

                    LifeClaimBasisCode
                            .MONTH_COMMAND_DISRUPTION_RESOLVED
            );
        }

        if (
                profile
                        .monthCommandDisruptionReviewRequired()
        ) {

            claims.add(
                    LifeClaimCode
                            .MULTIPLE_STRUCTURAL_DIRECTIONS_REQUIRE_CONTEXT,

                    LifeClaimBasisCode
                            .MONTH_COMMAND_DISRUPTION_REVIEW_REQUIRED
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

                Objects.requireNonNull(
                        claim,
                        "기존 LifeClaim에 null이 포함될 수 없습니다."
                );

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
                        "LifeClaim에는 최소 하나의 basis가 필요합니다."
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
                        "LifeClaim에는 최소 하나의 basis가 필요합니다."
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

            merged.addAll(
                    bases
            );
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