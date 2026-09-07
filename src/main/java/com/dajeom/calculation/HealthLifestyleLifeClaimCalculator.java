package com.dajeom.calculation;

import com.dajeom.domain.DayMasterStrengthStatus;
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
 * 건강·생활 해석을 위한 원국 기반 LifeClaim 보강 Calculator.
 *
 * =================================================
 *
 * 매우 중요:
 *
 * 이 Calculator는 의학적 건강 상태를 판단하지 않는다.
 *
 * 다음을 절대 생성하지 않는다.
 *
 * - 특정 질병
 * - 특정 장기 이상
 * - 체질 진단
 * - 수명
 * - 사고
 * - 입원
 * - 수술
 * - 정신질환
 * - 치료 방법
 * - 약물 복용
 *
 * =================================================
 *
 * 여기서 다루는 것은 오직 생활 운영 방식이다.
 *
 * 예:
 *
 * - 활동량과 회복의 균형
 * - 일을 많이 한 뒤 회복 시간을 확보하는가
 * - 준비와 휴식이 안정감을 만드는가
 * - 출력 활동이 자기 자원을 많이 사용하는가
 * - 성과 압박이 생활 리듬을 침범하는가
 * - 과도한 안정 지향이 움직임을 줄이는가
 * - 자신의 활동 패턴에 맞는 루틴이 필요한가
 *
 * =================================================
 *
 * 동일 LifeClaimCode가 기존 Claim에 존재하면
 * 중복 생성하지 않고 basis만 병합한다.
 */
@Component
public class HealthLifestyleLifeClaimCalculator {

    /**
     * 기존 LifeClaim 목록에
     * 건강·생활 영역의 Claim을 보강한다.
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
         * 1. 기본 활동 리듬
         * =================================================
         */
        addEnergyRhythmClaims(
                profile,
                claims
        );

        /*
         * =================================================
         * 2. 身弱 + 食傷 설기
         * =================================================
         */
        addOutputDrainClaims(
                profile,
                claims
        );

        /*
         * =================================================
         * 3. 印 계열
         * =================================================
         */
        addResourceLifestyleClaims(
                profile,
                foundations,
                claims
        );

        /*
         * =================================================
         * 4. 食傷 계열
         * =================================================
         */
        addOutputLifestyleClaims(
                foundations,
                claims
        );

        /*
         * =================================================
         * 5. 財印 직접 충돌
         * =================================================
         */
        addMaterialPressureClaims(
                profile,
                claims
        );

        /*
         * =================================================
         * 6. 印多用財
         * =================================================
         */
        addExcessResourceModerationClaims(
                profile,
                claims
        );

        /*
         * =================================================
         * 7. 合財存印
         * =================================================
         */
        addResourcePreservationClaims(
                profile,
                claims
        );

        return claims.toClaims();
    }

    /*
     * =================================================
     * 1. 기본 활동 리듬
     * =================================================
     */

    private void addEnergyRhythmClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

        EnumSet<LifeClaimBasisCode> bases =
                EnumSet.noneOf(
                        LifeClaimBasisCode.class
                );

        /*
         * 종격
         *
         * 일반적인 강약 방식보다
         * 환경과 전체 흐름에 맞춰 움직이는 구조이므로
         * 생활 리듬 역시 고정된 방식보다
         * 실제 환경에 맞게 설계하는 방향으로 사용한다.
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
             * 일반 강약
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
                            == DayMasterStrengthStatus.STRONG_LEANING

                            ||

                            status
                                    == DayMasterStrengthStatus.CLEARLY_STRONG
            ) {

                bases.add(
                        LifeClaimBasisCode
                                .GENERAL_STRENGTH_STRONG_SIDE
                );

            } else if (
                    status
                            == DayMasterStrengthStatus.WEAK_LEANING

                            ||

                            status
                                    == DayMasterStrengthStatus.CLEARLY_WEAK
            ) {

                bases.add(
                        LifeClaimBasisCode
                                .GENERAL_STRENGTH_WEAK_SIDE
                );
            }
        }

        if (bases.isEmpty()) {

            return;
        }

        /*
         * 건강 판단이 아니라:
         *
         * 자신의 활동 패턴에 맞는
         * 일정·집중·휴식 루틴을 만들어야 한다는
         * 생활 운영 Claim.
         */
        claims.add(

                LifeClaimCode
                        .SUSTAINABLE_ROUTINE_SHOULD_MATCH_ENERGY_PATTERN,

                bases
        );

        /*
         * 약한 쪽 일반 강약이라면
         * 장기 지속성을 위해 속도와 지원 구조가
         * 더 중요하다는 Claim도 추가한다.
         */
        if (
                bases.contains(
                        LifeClaimBasisCode
                                .GENERAL_STRENGTH_WEAK_SIDE
                )
        ) {

            claims.add(

                    LifeClaimCode
                            .PACING_AND_SUPPORT_MATTER_FOR_SUSTAINED_OUTPUT,

                    LifeClaimBasisCode
                            .GENERAL_STRENGTH_WEAK_SIDE
            );
        }
    }

    /*
     * =================================================
     * 2. 身弱 + 食傷 설기
     * =================================================
     */

    private void addOutputDrainClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

        if (
                !profile
                        .outputDrainsWeakStructure()
        ) {

            return;
        }

        /*
         * 활동과 출력이 많아질수록
         * 회복 시간을 먼저 확보하는 것이 중요.
         */
        claims.add(

                LifeClaimCode
                        .RECOVERY_BEFORE_OVEREXTENSION,

                LifeClaimBasisCode
                        .GENERAL_STRENGTH_WEAK_SIDE,

                LifeClaimBasisCode
                        .OUTPUT_DRAINS_WEAK_STRUCTURE
        );

        /*
         * 표현·생산·성과 활동이
         * 자기 자원을 많이 사용할 수 있다는 의미.
         *
         * "체력이 약하다"는 의미가 아니다.
         */
        claims.add(

                LifeClaimCode
                        .OUTPUT_CAN_CONSUME_PERSONAL_RESOURCES,

                LifeClaimBasisCode
                        .OUTPUT_DRAINS_WEAK_STRUCTURE
        );

        /*
         * 생활 루틴 Claim에도
         * 해당 근거를 병합한다.
         */
        claims.add(

                LifeClaimCode
                        .SUSTAINABLE_ROUTINE_SHOULD_MATCH_ENERGY_PATTERN,

                LifeClaimBasisCode
                        .GENERAL_STRENGTH_WEAK_SIDE,

                LifeClaimBasisCode
                        .OUTPUT_DRAINS_WEAK_STRUCTURE
        );
    }

    /*
     * =================================================
     * 3. 印 계열
     * =================================================
     */

    private void addResourceLifestyleClaims(

            NatalMyeongriProfile profile,

            PatternTenGodFoundationProfile foundations,

            ClaimCollector claims

    ) {

        boolean resourcePresent =
                familyPresent(

                        foundations,

                        MonthCommandPatternCandidateType
                                .DIRECT_RESOURCE,

                        MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE
                );

        if (!resourcePresent) {

            return;
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

        /*
         * 생활에서는:
         *
         * - 준비
         * - 정리
         * - 회복 시간
         * - 익숙한 루틴
         *
         * 이 안정감을 만드는 자원으로 번역한다.
         */
        claims.add(

                LifeClaimCode
                        .PREPARATION_AND_REFLECTION_SUPPORT_STABILITY,

                bases
        );

        /*
         * 印 과다까지 확정된 경우.
         *
         * 안정만 추구하다가
         * 활동 자체가 지나치게 줄어드는 패턴을 경계.
         */
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

    /*
     * =================================================
     * 4. 食傷 계열
     * =================================================
     */

    private void addOutputLifestyleClaims(

            PatternTenGodFoundationProfile foundations,

            ClaimCollector claims

    ) {

        boolean outputPresent =
                familyPresent(

                        foundations,

                        MonthCommandPatternCandidateType
                                .EATING_GOD,

                        MonthCommandPatternCandidateType
                                .HURTING_OFFICER
                );

        if (!outputPresent) {

            return;
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

        /*
         * 건강 영역이라고 해서
         * 食傷을 신체 기관으로 번역하지 않는다.
         *
         * 생활에서는:
         *
         * - 말하기
         * - 표현
         * - 활동
         * - 생산
         * - 문제 해결
         *
         * 등 외부 출력이 적절히 존재해야
         * 생활 리듬이 자연스럽다는 정도만 사용.
         */
        claims.add(

                LifeClaimCode
                        .EXPRESSION_NEEDS_A_CONSTRUCTIVE_OUTLET,

                bases
        );
    }

    /*
     * =================================================
     * 5. 財印 직접 충돌
     * =================================================
     */

    private void addMaterialPressureClaims(

            NatalMyeongriProfile profile,

            ClaimCollector claims

    ) {

        /*
         * 印多用財가 성립한 경우는
         * 같은 財克印을 부정적으로 중복 해석하지 않는다.
         */
        if (
                profile
                        .wealthModeratesExcessResource()
        ) {

            return;
        }

        if (
                !profile
                        .wealthResourceDirectConflictActive()
        ) {

            return;
        }

        /*
         * 건강·생활에서는:
         *
         * 돈이 나쁘다
         *
         * 가 아니라
         *
         * 성과·마감·현실 압박이
         * 휴식·안정·생활 기반까지 침범하지 않게
         * 운영해야 한다는 의미.
         */
        claims.add(

                LifeClaimCode
                        .MATERIAL_PRESSURE_CAN_DISRUPT_STABILITY,

                LifeClaimBasisCode
                        .WEALTH_RESOURCE_DIRECT_CONFLICT
        );
    }

    /*
     * =================================================
     * 6. 印多用財
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
         * 과도한 안정/준비를
         * 현실적인 목표와 활동이 적절히 움직이는 구조.
         *
         * 건강에서는:
         *
         * 무조건 쉬기
         *
         * 보다
         *
         * 적절한 활동과 회복의 균형으로 번역.
         */
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

    /*
     * =================================================
     * 7. 合財存印
     * =================================================
     */

    private void addResourcePreservationClaims(

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
         * 과도한 성과 압박을 줄였을 때
         * 안정과 회복 기반을 지킬 수 있다는 의미.
         *
         * 건강 카테고리에서 유용하지만
         * 질병과는 전혀 연결하지 않는다.
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
     * family 존재 여부
     * =================================================
     */

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