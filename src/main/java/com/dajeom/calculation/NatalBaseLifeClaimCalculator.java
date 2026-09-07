package com.dajeom.calculation;

import com.dajeom.domain.DayMasterStrengthStatus;
import com.dajeom.domain.LifeClaim;
import com.dajeom.domain.LifeClaimBasisCode;
import com.dajeom.domain.LifeClaimCode;
import com.dajeom.domain.LifeInterpretationCategory;
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
 * 기존 NatalLifeClaimCalculator가 만든 Claim으로
 * 채워지지 않은 카테고리를 원국의 기본 십성 family 구조로 보충한다.
 *
 * =================================================
 *
 * 이 Calculator는:
 *
 * - 연애 사건
 * - 결혼 시기
 * - 재물 금액
 * - 실제 과거 사건
 * - 질병
 * - 특정 직업
 *
 * 등을 생성하지 않는다.
 *
 * =================================================
 *
 * 실제 원국에 존재하는:
 *
 * 財
 * 官殺
 * 印
 * 食傷
 * 比劫
 *
 * family 구조를
 *
 * - 자원 관리
 * - 책임
 * - 안정
 * - 표현
 * - 독립성과 경계
 *
 * 같은 일반적 삶의 방식으로만 변환한다.
 *
 * =================================================
 *
 * 적용 순서는 점수나 강도 순위가 아니다.
 *
 * 이미 충분한 Claim이 있는 카테고리에
 * 일반 Claim을 무분별하게 추가하지 않기 위한
 * 결정론적 보충 순서다.
 */
@Component
public class NatalBaseLifeClaimCalculator {

    private final LifeClaimCategoryMapper categoryMapper;

    private final LifeClaimCoverageCalculator coverageCalculator;

    public NatalBaseLifeClaimCalculator(

            LifeClaimCategoryMapper categoryMapper,

            LifeClaimCoverageCalculator coverageCalculator

    ) {

        this.categoryMapper =
                Objects.requireNonNull(
                        categoryMapper,
                        "LifeClaimCategoryMapper는 필수입니다."
                );

        this.coverageCalculator =
                Objects.requireNonNull(
                        coverageCalculator,
                        "LifeClaimCoverageCalculator는 필수입니다."
                );
    }

    public List<LifeClaim> complete(

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

        ClaimCollector collector =
                new ClaimCollector(
                        existingClaims
                );

        PatternTenGodFoundationProfile foundations =
                profile.foundations();

        /*
         * =================================================
         * 印
         * =================================================
         */

        addIfNeeded(
                collector,
                resourceCandidate(
                        foundations
                )
        );

        /*
         * =================================================
         * 食傷
         * =================================================
         */

        addIfNeeded(
                collector,
                outputCandidate(
                        foundations
                )
        );

        /*
         * =================================================
         * 官殺
         * =================================================
         */

        addIfNeeded(
                collector,
                authorityCandidate(
                        foundations
                )
        );

        /*
         * =================================================
         * 比劫
         * =================================================
         */

        addIfNeeded(
                collector,
                companionCandidate(
                        foundations
                )
        );

        /*
         * =================================================
         * 財
         * =================================================
         */

        addIfNeeded(
                collector,
                wealthCandidate(
                        foundations
                )
        );

        /*
         * =================================================
         * 건강·생활 최소 rhythm
         * =================================================
         */

        addIfNeeded(
                collector,
                lifestyleCandidate(
                        profile
                )
        );

        return collector.toClaims();
    }

    /*
     * =================================================
     * 印
     * =================================================
     */

    private ClaimCandidate resourceCandidate(
            PatternTenGodFoundationProfile foundations
    ) {

        boolean present =
                familyPresent(

                        foundations,

                        MonthCommandPatternCandidateType
                                .DIRECT_RESOURCE,

                        MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE
                );

        if (!present) {

            return ClaimCandidate.absent();
        }

        EnumSet<LifeClaimBasisCode> bases =
                EnumSet.of(
                        LifeClaimBasisCode
                                .RESOURCE_FAMILY_PRESENT
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

        return new ClaimCandidate(

                LifeClaimCode
                        .PREPARATION_AND_REFLECTION_SUPPORT_STABILITY,

                bases
        );
    }

    /*
     * =================================================
     * 食傷
     * =================================================
     */

    private ClaimCandidate outputCandidate(
            PatternTenGodFoundationProfile foundations
    ) {

        boolean present =
                familyPresent(

                        foundations,

                        MonthCommandPatternCandidateType
                                .EATING_GOD,

                        MonthCommandPatternCandidateType
                                .HURTING_OFFICER
                );

        if (!present) {

            return ClaimCandidate.absent();
        }

        EnumSet<LifeClaimBasisCode> bases =
                EnumSet.of(
                        LifeClaimBasisCode
                                .OUTPUT_FAMILY_PRESENT
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

        return new ClaimCandidate(

                LifeClaimCode
                        .EXPRESSION_NEEDS_A_CONSTRUCTIVE_OUTLET,

                bases
        );
    }

    /*
     * =================================================
     * 官殺
     * =================================================
     */

    private ClaimCandidate authorityCandidate(
            PatternTenGodFoundationProfile foundations
    ) {

        boolean present =
                familyPresent(

                        foundations,

                        MonthCommandPatternCandidateType
                                .DIRECT_OFFICER,

                        MonthCommandPatternCandidateType
                                .SEVEN_KILLINGS
                );

        if (!present) {

            return ClaimCandidate.absent();
        }

        EnumSet<LifeClaimBasisCode> bases =
                EnumSet.of(
                        LifeClaimBasisCode
                                .AUTHORITY_FAMILY_PRESENT
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

        return new ClaimCandidate(

                LifeClaimCode
                        .RESPONSIBILITY_AND_EXPECTATIONS_SHAPE_CHOICES,

                bases
        );
    }

    /*
     * =================================================
     * 比劫
     * =================================================
     */

    private ClaimCandidate companionCandidate(
            PatternTenGodFoundationProfile foundations
    ) {

        boolean present =
                familyPresent(

                        foundations,

                        MonthCommandPatternCandidateType
                                .MONTH_COMPANION,

                        MonthCommandPatternCandidateType
                                .MONTH_ROB_WEALTH
                );

        if (!present) {

            return ClaimCandidate.absent();
        }

        EnumSet<LifeClaimBasisCode> bases =
                EnumSet.of(
                        LifeClaimBasisCode
                                .COMPANION_FAMILY_PRESENT
                );

        boolean stable =
                foundations
                        .foundationStable(
                                MonthCommandPatternCandidateType
                                        .MONTH_COMPANION
                        )

                        ||

                        foundations
                                .foundationStable(
                                        MonthCommandPatternCandidateType
                                                .MONTH_ROB_WEALTH
                                );

        if (stable) {

            bases.add(
                    LifeClaimBasisCode
                            .COMPANION_FOUNDATION_CONFIRMED
            );
        }

        return new ClaimCandidate(

                LifeClaimCode
                        .AUTONOMY_AND_PEER_BOUNDARIES_MATTER,

                bases
        );
    }

    /*
     * =================================================
     * 財
     * =================================================
     */

    private ClaimCandidate wealthCandidate(
            PatternTenGodFoundationProfile foundations
    ) {

        boolean present =
                familyPresent(

                        foundations,

                        MonthCommandPatternCandidateType
                                .DIRECT_WEALTH,

                        MonthCommandPatternCandidateType
                                .INDIRECT_WEALTH
                );

        if (!present) {

            return ClaimCandidate.absent();
        }

        EnumSet<LifeClaimBasisCode> bases =
                EnumSet.of(
                        LifeClaimBasisCode
                                .WEALTH_FAMILY_PRESENT
                );

        if (
                foundations
                        .wealthFoundationStable()
        ) {

            bases.add(
                    LifeClaimBasisCode
                            .WEALTH_FOUNDATION_CONFIRMED
            );
        }

        return new ClaimCandidate(

                LifeClaimCode
                        .PRACTICAL_RESOURCE_MANAGEMENT_IS_A_RECURRING_THEME,

                bases
        );
    }

    /*
     * =================================================
     * 건강·생활용 기본 rhythm
     * =================================================
     *
     * 질병 판단이 아니다.
     *
     * 최종 강약 routing을
     * 생활 페이스 관리라는 좁은 범위로만 사용한다.
     */

    private ClaimCandidate lifestyleCandidate(
            NatalMyeongriProfile profile
    ) {

        EnumSet<LifeClaimBasisCode> bases =
                EnumSet.noneOf(
                        LifeClaimBasisCode.class
                );

        /*
         * 종격
         */
        if (
                profile
                        .followingStructureEstablished()
        ) {

            bases.add(
                    LifeClaimBasisCode
                            .FOLLOWING_STRUCTURE_ESTABLISHED
            );

            /*
             * 진화격
             */
        } else if (
                profile
                        .trueTransformationStructure()
        ) {

            bases.add(
                    LifeClaimBasisCode
                            .TRUE_TRANSFORMATION_STRUCTURE
            );

            /*
             * 일반 강약 + 특수 맥락
             */
        } else if (
                profile
                        .specialStrengthContextExists()
        ) {

            bases.add(
                    LifeClaimBasisCode
                            .GENERAL_STRENGTH_SPECIAL_CONTEXT
            );

            /*
             * 일반 신강약
             */
        } else if (
                profile
                        .generalStrengthUsed()
        ) {

            DayMasterStrengthStatus status =
                    profile
                            .strengthSensitiveContext()
                            .strengthStatus();

            if (
                    status
                            == DayMasterStrengthStatus
                            .STRONG_LEANING

                            ||

                            status
                                    == DayMasterStrengthStatus
                                    .CLEARLY_STRONG
            ) {

                bases.add(
                        LifeClaimBasisCode
                                .GENERAL_STRENGTH_STRONG_SIDE
                );

            } else if (
                    status
                            == DayMasterStrengthStatus
                            .WEAK_LEANING

                            ||

                            status
                                    == DayMasterStrengthStatus
                                    .CLEARLY_WEAK
            ) {

                bases.add(
                        LifeClaimBasisCode
                                .GENERAL_STRENGTH_WEAK_SIDE
                );
            }
        }

        if (bases.isEmpty()) {

            return ClaimCandidate.absent();
        }

        return new ClaimCandidate(

                LifeClaimCode
                        .SUSTAINABLE_ROUTINE_SHOULD_MATCH_ENERGY_PATTERN,

                bases
        );
    }

    /*
     * =================================================
     * 부족한 카테고리를 실제로 채우는 경우에만 추가
     * =================================================
     */

    private void addIfNeeded(

            ClaimCollector collector,

            ClaimCandidate candidate

    ) {

        if (!candidate.present()) {

            return;
        }

        List<LifeInterpretationCategory> missingCategories =
                coverageCalculator
                        .analyze(
                                collector.toClaims()
                        )
                        .missingCategories();

        /*
         * EnumSet.copyOf(emptyList)는 예외가 발생하므로
         * 먼저 빈 목록을 확인한다.
         */
        if (missingCategories.isEmpty()) {

            return;
        }

        EnumSet<LifeInterpretationCategory> missing =
                EnumSet.copyOf(
                        missingCategories
                );

        Set<LifeInterpretationCategory> candidateCategories =
                categoryMapper
                        .categoriesOf(
                                candidate.code()
                        );

        boolean fillsMissingCategory =
                candidateCategories
                        .stream()
                        .anyMatch(
                                missing::contains
                        );

        if (!fillsMissingCategory) {

            return;
        }

        collector.add(
                candidate.code(),
                candidate.bases()
        );
    }

    /*
     * =================================================
     * family 존재
     * =================================================
     */

    private boolean familyPresent(

            PatternTenGodFoundationProfile foundations,

            MonthCommandPatternCandidateType first,

            MonthCommandPatternCandidateType second

    ) {

        return foundations
                .foundationOf(first)
                .state()
                != PatternTenGodFoundationState.ABSENT

                ||

                foundations
                        .foundationOf(second)
                        .state()
                        != PatternTenGodFoundationState.ABSENT;
    }

    /*
     * =================================================
     * Candidate
     * =================================================
     */

    private record ClaimCandidate(

            LifeClaimCode code,

            Set<LifeClaimBasisCode> bases

    ) {

        private ClaimCandidate {

            bases =
                    bases == null
                            ? Set.of()
                            : Set.copyOf(bases);
        }

        private static ClaimCandidate absent() {

            return new ClaimCandidate(
                    null,
                    Set.of()
            );
        }

        private boolean present() {

            return code != null
                    &&
                    !bases.isEmpty();
        }
    }

    /*
     * =================================================
     * 기존 Claim까지 포함한 병합기
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

        private ClaimCollector(
                List<LifeClaim> existing
        ) {

            for (
                    LifeClaim claim
                    : existing
            ) {

                Objects.requireNonNull(
                        claim,
                        "기존 Claim에 null이 포함될 수 없습니다."
                );

                add(
                        claim.code(),
                        claim.bases()
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
                        "LifeClaim basis는 비어 있을 수 없습니다."
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

        private List<LifeClaim> toClaims() {

            List<LifeClaim> result =
                    new ArrayList<>();

            /*
             * enum 선언 순서로 반환한다.
             *
             * Map iteration 순서에 의존하지 않으므로
             * 실행마다 Claim 순서가 바뀌지 않는다.
             */
            for (
                    LifeClaimCode code
                    : LifeClaimCode.values()
            ) {

                EnumSet<LifeClaimBasisCode> bases =
                        basesByCode.get(
                                code
                        );

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

                                EnumSet.copyOf(
                                        bases
                                )
                        )
                );
            }

            return List.copyOf(
                    result
            );
        }
    }
}