package com.dajeom.calculation;

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

/**
 * 재물 해석을 위한 원국 기반 LifeClaim 보강 Calculator.
 *
 * =================================================
 *
 * 이 Calculator는 재물의 절대적인 많고 적음을 판단하지 않는다.
 *
 * 다음과 같은 사건도 만들지 않는다.
 *
 * - 부자가 된다
 * - 돈을 잃는다
 * - 사업에 성공한다
 * - 투자로 큰돈을 번다
 * - 부동산 운이 좋다
 * - 특정 시기에 재물이 들어온다
 *
 * =================================================
 *
 * 여기서 다루는 것은:
 *
 * - 현실적 결과를 중요하게 보는가
 * - 시간/돈/노력/기회를 어떻게 배분하는가
 * - 성과 추구와 안정 기반이 충돌하는가
 * - 財가 印을 손상하는가
 * - 財가 과도한 印을 적절히 조절하는가
 * - 七殺 압박을 정리하면서 財를 보존하는가
 * - 財를 덜어내면서 印을 보존하는가
 *
 * 같은 "자원 운영 방식"이다.
 *
 * =================================================
 *
 * 동일 LifeClaimCode가 기존에 이미 존재한다면
 * 새 Claim을 중복 생성하지 않고 basis만 병합한다.
 */
@Component
public class WealthLifeClaimCalculator {

    /**
     * 기존 원국 Claim에
     * 재물 전용 Claim을 추가한다.
     */
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

        /*
         * =================================================
         * 1. 財 family 자체의 존재
         * =================================================
         */

        addBasicWealthClaims(
                profile,
                foundations,
                claims
        );

        /*
         * =================================================
         * 2. 財印 관계
         * =================================================
         */

        addWealthResourceClaims(
                profile,
                claims
        );

        /*
         * =================================================
         * 3. 印多用財
         * =================================================
         */

        addExcessResourceModerationClaims(
                profile,
                claims
        );

        /*
         * =================================================
         * 4. 存財合殺
         * =================================================
         */

        addPreservedWealthClaims(
                profile,
                claims
        );

        /*
         * =================================================
         * 5. 合財存印
         * =================================================
         */

        addPreservedResourceClaims(
                profile,
                claims
        );

        /*
         * =================================================
         * 6. 財格 자체가 원국 중심 구조인 경우
         * =================================================
         */

        addWealthPatternClaims(
                profile,
                claims
        );

        return claims.toClaims();
    }

    /*
     * =================================================
     * 1. 기본 財 구조
     * =================================================
     */

    private void addBasicWealthClaims(

            NatalMyeongriProfile profile,

            PatternTenGodFoundationProfile foundations,

            ClaimCollector claims

    ) {

        boolean wealthPresent =
                wealthFamilyPresent(
                        foundations
                );

        if (!wealthPresent) {

            return;
        }

        EnumSet<LifeClaimBasisCode> bases =
                EnumSet.of(
                        LifeClaimBasisCode
                                .WEALTH_FAMILY_PRESENT
                );

        /*
         * 財가 단순 존재하는 수준이 아니라
         * 실제 foundation까지 확인된 경우.
         */
        if (
                foundations
                        .wealthFoundationStable()
        ) {

            bases.add(
                    LifeClaimBasisCode
                            .WEALTH_FOUNDATION_CONFIRMED
            );
        }

        /*
         * 돈 그 자체가 아니라:
         *
         * - 시간
         * - 노력
         * - 기회
         * - 자원
         * - 실익
         *
         * 을 어떻게 배분할 것인가가
         * 반복적으로 중요한 문제가 될 수 있다는 Claim.
         */
        claims.add(

                LifeClaimCode
                        .PRACTICAL_RESOURCE_MANAGEMENT_IS_A_RECURRING_THEME,

                bases
        );

        /*
         * 財 foundation이 실제 확인된 경우에는
         * 현실 결과가 행동 동기로 작동하는 성향까지
         * 한 단계 더 확정할 수 있다.
         */
        if (
                foundations
                        .wealthFoundationStable()
        ) {

            claims.add(

                    LifeClaimCode
                            .MATERIAL_RESULTS_ARE_IMPORTANT_MOTIVATORS,

                    LifeClaimBasisCode
                            .WEALTH_FAMILY_PRESENT,

                    LifeClaimBasisCode
                            .WEALTH_FOUNDATION_CONFIRMED
            );
        }
    }

    /*
     * =================================================
     * 2. 財印 관계
     * =================================================
     */

    private void addWealthResourceClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

        /*
         * =================================================
         * 印多用財가 이미 성립했다면
         *
         * 같은 財克印 구조를
         * "안정 기반을 손상한다"는 부정 Claim으로
         * 동시에 생성하지 않는다.
         * =================================================
         */

        if (
                profile
                        .wealthModeratesExcessResource()
        ) {

            return;
        }

        /*
         * 財와 印이 실제 직접 충돌.
         *
         * 현실 성과 압박이:
         *
         * - 준비
         * - 안정
         * - 회복
         *
         * 같은 기반을 침범할 수 있다는 의미.
         */
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

        /*
         * 財와 印이 함께 존재해도
         * 실제로 서로를 깨뜨리지 않는 배치라면:
         *
         * 성과 추구
         * +
         * 안정 기반
         *
         * 을 병행할 수 있는 구조로 본다.
         */
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
     * 3. 印多用財
     * =================================================
     */

    private void addExcessResourceModerationClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

        if (
                !profile
                        .wealthModeratesExcessResource()
        ) {

            return;
        }

        /*
         * raw 財克印이라고 무조건 부정적으로 보지 않는다.
         *
         * 이미:
         *
         * 身印 쪽이 과하고
         * +
         * 財가 실제로 그 과잉을 조절하는 구조
         *
         * 까지 Java Rule에서 확정된 경우다.
         */
        claims.add(

                LifeClaimCode
                        .PRACTICAL_GOALS_CAN_CORRECT_OVERPROTECTION,

                LifeClaimBasisCode
                        .EXCESSIVE_RESOURCE_CONTEXT,

                LifeClaimBasisCode
                        .WEALTH_MODERATES_EXCESS_RESOURCE,

                LifeClaimBasisCode
                        .WEALTH_RESOURCE_DIRECT_CONFLICT
        );

        /*
         * 안정과 현실 목표 중
         * 하나를 없애는 구조가 아니라
         * 적절히 균형을 잡는 방향.
         */
        claims.add(

                LifeClaimCode
                        .BALANCE_STABILITY_AND_MATERIAL_GOALS,

                LifeClaimBasisCode
                        .WEALTH_MODERATES_EXCESS_RESOURCE
        );
    }

    /*
     * =================================================
     * 4. 存財合殺
     * =================================================
     */

    private void addPreservedWealthClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

        if (
                !profile
                        .sevenKillingsClearedPreservingWealth()
        ) {

            return;
        }

        /*
         * 압박/경쟁/부담 요소를 정리하면서도
         * 財를 보존하는 구조.
         *
         * 재물 성공을 뜻하는 것이 아니다.
         *
         * 불필요한 압박을 줄이면서도
         * 현실 목표는 유지할 수 있다는 의미다.
         */
        claims.add(

                LifeClaimCode
                        .PRESSURE_CAN_BE_REDUCED_WITHOUT_LOSING_PRACTICAL_GOALS,

                LifeClaimBasisCode
                        .SEVEN_KILLINGS_CLEARED_PRESERVING_WEALTH
        );
    }

    /*
     * =================================================
     * 5. 合財存印
     * =================================================
     */

    private void addPreservedResourceClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

        if (
                !profile
                        .wealthClearedPreservingResource()
        ) {

            return;
        }

        /*
         * 財의 압박이 제거되면서
         * 印의 안정·보호 기반이 보존되는 구조.
         *
         * 돈을 포기한다는 의미가 아니다.
         *
         * 지나친 성과 압박을 낮춰
         * 유지 가능한 기반을 지키는 의미로 번역한다.
         */
        claims.add(

                LifeClaimCode
                        .STABILITY_CAN_BE_PRESERVED_WHEN_MATERIAL_PRESSURE_IS_REDUCED,

                LifeClaimBasisCode
                        .WEALTH_CLEARED_PRESERVING_RESOURCE
        );
    }

    /*
     * =================================================
     * 6. 財格
     * =================================================
     */

    private void addWealthPatternClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

        if (
                !profile
                        .patternDecided()
        ) {

            return;
        }

        NatalPatternType patternType =
                profile
                        .decidedPatternType()
                        .orElse(null);

        if (
                patternType
                        != NatalPatternType.DIRECT_WEALTH

                        &&

                        patternType
                                != NatalPatternType.INDIRECT_WEALTH
        ) {

            return;
        }

        NatalPatternDecisionStatus status =
                profile
                        .decidedPatternStatus()
                        .orElse(null);

        if (status == null) {

            return;
        }

        /*
         * =================================================
         * 財格이 실제 성립
         * =================================================
         */

        if (
                status
                        == NatalPatternDecisionStatus.FORMED
        ) {

            claims.add(

                    LifeClaimCode
                            .MATERIAL_RESULTS_ARE_IMPORTANT_MOTIVATORS,

                    LifeClaimBasisCode
                            .PATTERN_FORMED,

                    LifeClaimBasisCode
                            .WEALTH_FAMILY_PRESENT
            );

            return;
        }

        /*
         * =================================================
         * 財格은 성립하지만 결함 존재
         * =================================================
         */

        if (
                status
                        == NatalPatternDecisionStatus.FORMED_WITH_DEFECT
        ) {

            claims.add(

                    LifeClaimCode
                            .MATERIAL_RESULTS_ARE_IMPORTANT_MOTIVATORS,

                    LifeClaimBasisCode
                            .PATTERN_FORMED_WITH_DEFECT,

                    LifeClaimBasisCode
                            .WEALTH_FAMILY_PRESENT
            );

            claims.add(

                    LifeClaimCode
                            .CORE_STRENGTH_COMES_WITH_A_MANAGEABLE_DEFECT,

                    LifeClaimBasisCode
                            .PATTERN_FORMED_WITH_DEFECT
            );

            return;
        }

        /*
         * =================================================
         * 財格이 깨졌으나 구응 존재
         * =================================================
         */

        if (
                status
                        == NatalPatternDecisionStatus.BROKEN_WITH_RESCUE
        ) {

            claims.add(

                    LifeClaimCode
                            .DIFFICULTY_CAN_BE_CONVERTED_THROUGH_COMPENSATION,

                    LifeClaimBasisCode
                            .PATTERN_BROKEN_WITH_RESCUE
            );

            claims.add(

                    LifeClaimCode
                            .PRACTICAL_RESOURCE_MANAGEMENT_IS_A_RECURRING_THEME,

                    LifeClaimBasisCode
                            .PATTERN_BROKEN_WITH_RESCUE,

                    LifeClaimBasisCode
                            .WEALTH_FAMILY_PRESENT
            );

            return;
        }

        /*
         * =================================================
         * 財格이 실제로 파격
         * =================================================
         *
         * "돈을 잃는다"가 아니다.
         *
         * 기존 자원 운영 방식의
         * 적극적 재조정 필요성으로만 번역한다.
         */

        if (
                status
                        == NatalPatternDecisionStatus.BROKEN
        ) {

            claims.add(

                    LifeClaimCode
                            .CORE_STRUCTURE_REQUIRES_ACTIVE_REORGANIZATION,

                    LifeClaimBasisCode
                            .PATTERN_BROKEN
            );

            claims.add(

                    LifeClaimCode
                            .PRACTICAL_RESOURCE_MANAGEMENT_IS_A_RECURRING_THEME,

                    LifeClaimBasisCode
                            .PATTERN_BROKEN,

                    LifeClaimBasisCode
                            .WEALTH_FAMILY_PRESENT
            );

            return;
        }

        /*
         * =================================================
         * 財格은 선택됐지만 아직 REVIEW
         * =================================================
         */

        if (
                status
                        == NatalPatternDecisionStatus.REVIEW_REQUIRED
        ) {

            claims.add(

                    LifeClaimCode
                            .MULTIPLE_STRUCTURAL_DIRECTIONS_REQUIRE_CONTEXT,

                    LifeClaimBasisCode
                            .PATTERN_REVIEW_REQUIRED
            );

            return;
        }

        /*
         * =================================================
         * 財 관련 경로 자체가 성격되지 않음
         * =================================================
         */

        if (
                status
                        == NatalPatternDecisionStatus.UNFORMED
        ) {

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
     * 財 family 존재 여부
     * =================================================
     */

    private boolean wealthFamilyPresent(
            PatternTenGodFoundationProfile foundations
    ) {

        return foundationPresent(

                foundations,

                MonthCommandPatternCandidateType
                        .DIRECT_WEALTH
        )

                ||

                foundationPresent(

                        foundations,

                        MonthCommandPatternCandidateType
                                .INDIRECT_WEALTH
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

    /*
     * =================================================
     * Claim 병합기
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
             * LifeClaimCode 선언 순서로 반환한다.
             *
             * 결과 순서를 Map iteration에 맡기지 않는다.
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