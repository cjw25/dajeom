package com.dajeom.calculation;

import com.dajeom.domain.DayMasterStrengthStatus;
import com.dajeom.domain.LifeClaim;
import com.dajeom.domain.LifeClaimBasisCode;
import com.dajeom.domain.LifeClaimCode;
import com.dajeom.domain.NatalMyeongriProfile;
import com.dajeom.domain.NatalPatternDecisionStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * NatalMyeongriProfile을
 * AI 해석 직전의 의미 단위 LifeClaim으로 변환한다.
 *
 * =================================================
 *
 * 중요 원칙
 * =================================================
 *
 * Java가 명리 판단을 끝낸다.
 *
 * AI는 여기서 생성된 Claim을
 * NarrativeDirective 범위 안에서 문장화만 한다.
 *
 * =================================================
 *
 * 官殺 관련 중요 원칙
 * =================================================
 *
 * 官殺混雜의 최종 정리 여부는
 *
 * PatternOfficerKillingsPurificationAnalysis
 *
 * 만 사용한다.
 *
 * generic PatternStemCombinationPreservationAnalysis의
 * FULLY_BOUND 등을
 *
 * "官 또는 殺이 제거됐다"
 *
 * 라는 의미로 사용하지 않는다.
 */
@Component
public class NatalLifeClaimCalculator {

    public List<LifeClaim> calculate(
            NatalMyeongriProfile profile
    ) {

        Objects.requireNonNull(
                profile,
                "NatalMyeongriProfile은 필수입니다."
        );

        ClaimCollector claims =
                new ClaimCollector();

        addStrengthClaims(
                profile,
                claims
        );

        addStrengthSensitiveClaims(
                profile,
                claims
        );

        addWealthResourceClaims(
                profile,
                claims
        );

        addPatternClaims(
                profile,
                claims
        );

        addMonthCommandClaims(
                profile,
                claims
        );

        addStemCombinationClaims(
                profile,
                claims
        );

        addOfficerKillingsClaims(
                profile,
                claims
        );

        addYangBladeClaims(
                profile,
                claims
        );

        return claims.toClaims();
    }

    /*
     * =================================================
     * 강약 / 특수 구조
     * =================================================
     */

    private void addStrengthClaims(

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

        DayMasterStrengthStatus strengthStatus =
                profile
                        .strengthSensitiveContext()
                        .strengthStatus();

        if (
                strengthStatus
                        == DayMasterStrengthStatus.STRONG_LEANING

                        ||

                        strengthStatus
                                == DayMasterStrengthStatus.CLEARLY_STRONG
        ) {

            claims.add(

                    LifeClaimCode
                            .SELF_DIRECTION_IS_EASIER_TO_SUSTAIN,

                    LifeClaimBasisCode
                            .GENERAL_STRENGTH_STRONG_SIDE
            );
        }

        if (
                strengthStatus
                        == DayMasterStrengthStatus.WEAK_LEANING

                        ||

                        strengthStatus
                                == DayMasterStrengthStatus.CLEARLY_WEAK
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
    }

    /*
     * =================================================
     * 강약 민감 구조
     * =================================================
     */

    private void addStrengthSensitiveClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

        if (
                profile
                        .outputDrainsWeakStructure()
        ) {

            claims.add(

                    LifeClaimCode
                            .RECOVERY_BEFORE_OVEREXTENSION,

                    LifeClaimBasisCode
                            .OUTPUT_DRAINS_WEAK_STRUCTURE
            );

            claims.add(

                    LifeClaimCode
                            .OUTPUT_CAN_CONSUME_PERSONAL_RESOURCES,

                    LifeClaimBasisCode
                            .OUTPUT_DRAINS_WEAK_STRUCTURE
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

        if (
                profile
                        .wealthModeratesExcessResource()
        ) {

            claims.add(

                    LifeClaimCode
                            .PRACTICAL_GOALS_CAN_CORRECT_OVERPROTECTION,

                    LifeClaimBasisCode
                            .EXCESSIVE_RESOURCE_CONTEXT,

                    LifeClaimBasisCode
                            .WEALTH_MODERATES_EXCESS_RESOURCE
            );

            claims.add(

                    LifeClaimCode
                            .BALANCE_STABILITY_AND_MATERIAL_GOALS,

                    LifeClaimBasisCode
                            .WEALTH_MODERATES_EXCESS_RESOURCE
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

    /*
     * =================================================
     * 財印
     * =================================================
     */

    private void addWealthResourceClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

        /*
         * 印多用財가 성립한 경우에는
         * 같은 財克印을 부정 Claim으로 중복 생성하지 않는다.
         */
        if (
                profile
                        .wealthModeratesExcessResource()
        ) {

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
     * =================================================
     * 格局
     * =================================================
     */

    private void addPatternClaims(

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

        NatalPatternDecisionStatus status =
                profile
                        .decidedPatternStatus()
                        .orElse(null);

        if (status == null) {

            return;
        }

        switch (status) {

            case FORMED ->

                    claims.add(

                            LifeClaimCode
                                    .LIFE_DIRECTION_HAS_A_COHERENT_STRUCTURE,

                            LifeClaimBasisCode
                                    .PATTERN_FORMED
                    );

            case FORMED_WITH_DEFECT ->

                    claims.add(

                            LifeClaimCode
                                    .CORE_STRENGTH_COMES_WITH_A_MANAGEABLE_DEFECT,

                            LifeClaimBasisCode
                                    .PATTERN_FORMED_WITH_DEFECT
                    );

            case BROKEN ->

                    claims.add(

                            LifeClaimCode
                                    .CORE_STRUCTURE_REQUIRES_ACTIVE_REORGANIZATION,

                            LifeClaimBasisCode
                                    .PATTERN_BROKEN
                    );

            case BROKEN_WITH_RESCUE ->

                    claims.add(

                            LifeClaimCode
                                    .DIFFICULTY_CAN_BE_CONVERTED_THROUGH_COMPENSATION,

                            LifeClaimBasisCode
                                    .PATTERN_BROKEN_WITH_RESCUE
                    );

            case REVIEW_REQUIRED ->

                    claims.add(

                            LifeClaimCode
                                    .MULTIPLE_STRUCTURAL_DIRECTIONS_REQUIRE_CONTEXT,

                            LifeClaimBasisCode
                                    .PATTERN_REVIEW_REQUIRED
                    );

            case UNFORMED ->

                    claims.add(

                            LifeClaimCode
                                    .LIFE_DIRECTION_DEPENDS_MORE_ON_CHOSEN_STRUCTURE,

                            LifeClaimBasisCode
                                    .PATTERN_UNFORMED
                    );
        }
    }

    /*
     * =================================================
     * 月令 교란
     * =================================================
     */

    private void addMonthCommandClaims(

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

    /*
     * =================================================
     * 天干合
     * =================================================
     *
     * 官殺混雜의 정리 여부는 여기서 판단하지 않는다.
     */

    private void addStemCombinationClaims(

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

        /*
         * 存財合殺.
         *
         * 이는 財 보존 관련 명확한 semantic helper이므로
         * 계속 사용한다.
         */
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

        /*
         * 合財存印.
         */
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

        /*
         * 제거:
         *
         * profile.officerKillingsClearedByStemCombination()
         *
         * generic binding 결과를
         * 官殺取清의 최종 판정으로 사용하지 않는다.
         */
    }

    /*
     * =================================================
     * 官殺混雜
     * =================================================
     *
     * 여기서는 PatternOfficerKillingsPurificationAnalysis만
     * 권위 판정으로 사용한다.
     */

    private void addOfficerKillingsClaims(

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

        /*
         * 최종 取清 판정은 오직 purification.
         */
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

    /*
     * =================================================
     * 陽刃
     * =================================================
     */

    private void addYangBladeClaims(

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

    /*
     * =================================================
     * Claim Collector
     * =================================================
     */

    private static final class ClaimCollector {

        private final Map<
                LifeClaimCode,
                EnumSet<LifeClaimBasisCode>
                > basesByCode =
                new EnumMap<>(
                        LifeClaimCode.class
                );

        private void add(

                LifeClaimCode code,

                LifeClaimBasisCode... bases

        ) {

            Objects.requireNonNull(
                    code,
                    "LifeClaimCode는 필수입니다."
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

            EnumSet<LifeClaimBasisCode> merged =
                    basesByCode
                            .computeIfAbsent(
                                    code,
                                    ignored ->
                                            EnumSet.noneOf(
                                                    LifeClaimBasisCode.class
                                            )
                            );

            for (
                    LifeClaimBasisCode basis
                    : bases
            ) {

                merged.add(
                        Objects.requireNonNull(
                                basis,
                                "LifeClaimBasisCode는 null일 수 없습니다."
                        )
                );
            }
        }

        private void add(

                LifeClaimCode code,

                Set<LifeClaimBasisCode> bases

        ) {

            Objects.requireNonNull(
                    code,
                    "LifeClaimCode는 필수입니다."
            );

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
                                    code,
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

            return List.copyOf(
                    result
            );
        }
    }
}