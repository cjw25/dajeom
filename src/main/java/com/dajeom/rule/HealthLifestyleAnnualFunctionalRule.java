package com.dajeom.rule;

import com.dajeom.model.AnnualCrossFunctionalSynthesis;
import com.dajeom.model.AnnualFunctionCapacityContext;
import com.dajeom.model.AnnualFunctionalLifeDomainContext;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import com.dajeom.type.NatalCrossFunctionalPatternType;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Annual Functional 기반
 * HEALTH_LIFESTYLE 통합 Rule.
 *
 * =================================================
 *
 * Source:
 *
 * ANNUAL_FUNCTIONAL_CONTEXT
 *
 * =================================================
 *
 * HEALTH_LIFESTYLE에서는
 * 특정 Function 하나를
 * 건강에 직접 대응시키지 않는다.
 *
 * 다섯 Function 전체:
 *
 * SELF_ASSERTION
 * EXPRESSION
 * RESOURCE_REALITY
 * RESPONSIBILITY
 * PROCESSING
 *
 * 의:
 *
 * - Annual 활성
 * - 원국 고정 Capacity 방향
 * - 현재 대운 결합 후 Resolved 활성
 * - CrossFunctional 부담 구조
 * - CrossFunctional 조절 구조
 * - Transformation
 * - Original Function Reduction
 * - Context Qualification
 *
 * 을 함께 본다.
 *
 * =================================================
 *
 * 매우 중요한 제한:
 *
 * 이 Rule은:
 *
 * - 질병
 * - 장기
 * - 병명
 * - 진단
 * - 발병
 * - 사고
 * - 입원
 * - 수술
 *
 * 을 예측하지 않는다.
 *
 * =================================================
 *
 * 따라서:
 *
 * 관살 강함
 * -> 질병
 *
 * 인성 강함
 * -> 회복
 *
 * 식상 억제
 * -> 특정 신체 증상
 *
 * 재다신약
 * -> 건강 악화
 *
 * 같은 변환은 금지한다.
 *
 * =================================================
 *
 * 여기서 HEALTH_LIFESTYLE의 의미는:
 *
 * - 생활 부하
 * - 기능적 과중
 * - 활동/처리 리듬의 압박
 * - 책임/현실 요구의 누적
 * - 서로 다른 기능 작동의 마찰
 *
 * 정도의 deterministic Context다.
 *
 * =================================================
 *
 * Capacity 판단:
 *
 * 각 Function의
 * sameDirectionAccumulationContact()
 *
 * 를 확인한다.
 *
 * 이는:
 *
 * strong-side
 * +
 * support-side Function
 *
 * 또는
 *
 * weak-side
 * +
 * pressure-side Function
 *
 * 이 실제 활성되어
 * 원국 Strength의 같은 방향이
 * 추가 누적되는 상황이다.
 *
 * =================================================
 *
 * 이 역시:
 *
 * 건강 악화
 * 질병 발생
 *
 * 을 뜻하지 않는다.
 *
 * 단지 생활 기능을 운용하는
 * Capacity 부담 방향이다.
 *
 * =================================================
 *
 * 명시적 부담 Cross Pattern:
 *
 * RESOURCE_OVERLOADS_CORE_SELF
 *
 * SELF_ASSERTION_COMPETES_FOR_RESOURCE
 *
 * EXPRESSION_CONFLICTS_RESPONSIBILITY
 *
 * PROCESSING_RESTRAINS_OUTPUT
 *
 * =================================================
 *
 * 이 Pattern들도
 * usableAsEffectiveAnnualPatternContact=true
 * 인 경우만 사용한다.
 *
 * =================================================
 *
 * RESPONSIBILITY_INTERNAL_MIXTURE:
 *
 * 책임 기능 내부 혼재이므로
 * 단순 PRESSURE가 아니라
 * MIXED로 우선 보존한다.
 *
 * =================================================
 *
 * 조절 Context:
 *
 * PROCESSING_REGULATES_EXPRESSION
 *
 * EXPRESSION_CONTROLS_PRESSURE
 *
 * 이 둘은 부담 구조를 조절하는
 * Functional Context가 될 수 있지만
 * 자동 SUPPORTIVE나 회복으로
 * 변환하지 않는다.
 *
 * =================================================
 *
 * RESPONSIBILITY_GENERATES_PROCESSING
 * PRESSURE_GENERATES_PROCESSING
 *
 * 역시 압력/책임이 처리 기능으로
 * 이어지는 흐름일 뿐
 * 회복력이나 건강 호조를
 * 자동 의미하지 않는다.
 *
 * =================================================
 *
 * SUPPORTIVE:
 *
 * 이 Rule 단독으로는 생성하지 않는다.
 *
 * 실제 HEALTH_LIFESTYLE 지지 방향은
 * 조후 / 오행 / 원국 구조 / 기타 독립 Evidence와
 * 이후 Domain Synthesis에서 판단해야 한다.
 */
@Component
public class HealthLifestyleAnnualFunctionalRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .ANNUAL_FUNCTIONAL_CONTEXT
            );

    /*
     * =================================================
     * Metadata
     * =================================================
     */

    @Override
    public LifeDomain domain() {

        return LifeDomain.HEALTH_LIFESTYLE;
    }

    @Override
    public String code() {

        return RULE_CODE;
    }

    @Override
    public Set<LifeDomainEvidenceSourceType>
    supportedSources() {

        return SUPPORTED_SOURCES;
    }

    /*
     * =================================================
     * Evaluate
     * =================================================
     */

    @Override
    public List<LifeDomainRuleMatch> evaluate(
            LifeDomainEvidenceBundle bundle
    ) {

        Objects.requireNonNull(
                bundle,
                "HealthLifestyleAnnualFunctionalRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.HEALTH_LIFESTYLE
        ) {

            throw new IllegalArgumentException(
                    "HealthLifestyleAnnualFunctionalRule은 HEALTH_LIFESTYLE Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                referenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .ANNUAL_FUNCTIONAL_CONTEXT
                );

        /*
         * Natal Bundle에는
         * Annual Functional Evidence가 없다.
         */

        if (
                referenceOptional.isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?> reference =
                referenceOptional
                        .orElseThrow();

        AnnualFunctionalLifeDomainContext context =
                reference
                        .requireSource(
                                AnnualFunctionalLifeDomainContext.class
                        );

        if (
                !context
                        .annualFunctionalAvailable()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 5 Function Capacity
         * =================================================
         */

        List<AnnualFunctionCapacityContext>
                functionContexts =
                allFunctionContexts(
                        context
                );

        validateIdentity(
                context,
                functionContexts
        );

        List<AnnualFunctionCapacityContext>
                annualActiveContexts =
                functionContexts
                        .stream()
                        .filter(
                                AnnualFunctionCapacityContext
                                        ::active
                        )
                        .toList();

        /*
         * =================================================
         * 현재 Segment Resolved Function
         * =================================================
         */

        List<NatalTenGodFunctionType>
                resolvedActiveFunctions =
                resolvedActiveFunctions(
                        context
                );

        /*
         * Annual 자체도 inactive이고
         * 현재 대운 결합 후 Function도
         * 전부 inactive라면
         * 이 Rule에서 만들 Match가 없다.
         */

        if (
                annualActiveContexts.isEmpty()
                        && resolvedActiveFunctions.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Cross
         * =================================================
         */

        List<AnnualCrossFunctionalSynthesis>
                contactedSyntheses =
                context
                        .currentContactedSyntheses();

        List<AnnualCrossFunctionalSynthesis>
                effectiveSyntheses =
                context
                        .currentEffectiveFullContacts();

        List<AnnualCrossFunctionalSynthesis>
                referenceSyntheses =
                context
                        .currentReferenceContacts();

        LifeDomainSignalStatus status =
                resolveStatus(
                        context,
                        functionContexts,
                        annualActiveContexts,
                        resolvedActiveFunctions,
                        effectiveSyntheses
                );

        List<String> reasons =
                buildReasons(
                        context,
                        functionContexts,
                        annualActiveContexts,
                        resolvedActiveFunctions,
                        contactedSyntheses,
                        effectiveSyntheses,
                        referenceSyntheses,
                        status
                );

        return List.of(
                new LifeDomainRuleMatch(
                        LifeDomain.HEALTH_LIFESTYLE,
                        RULE_CODE,
                        status,
                        summaryOf(
                                status
                        ),
                        List.of(
                                reference
                        ),
                        reasons
                )
        );
    }

    /*
     * =================================================
     * Status
     * =================================================
     */

    private LifeDomainSignalStatus resolveStatus(

            AnnualFunctionalLifeDomainContext context,

            List<AnnualFunctionCapacityContext>
                    functionContexts,

            List<AnnualFunctionCapacityContext>
                    annualActiveContexts,

            List<NatalTenGodFunctionType>
                    resolvedActiveFunctions,

            List<AnnualCrossFunctionalSynthesis>
                    effectiveSyntheses

    ) {

        /*
         * =================================================
         * 불완전 원국
         * =================================================
         *
         * HEALTH 영역에서 incomplete chart를
         * 강한 방향으로 판정하지 않는다.
         */

        if (
                !context
                        .completeChart()
        ) {

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        /*
         * =================================================
         * 현재 대운 Functional Segment
         * =================================================
         */

        if (
                context
                        .currentFunctionalSegmentAvailable()
        ) {

            /*
             * Annual에서 Function이 들어왔더라도
             * 현재 대운 결합 후
             * 모든 Function이 inactive면
             * 방향을 확정하지 않는다.
             */

            if (
                    resolvedActiveFunctions
                            .isEmpty()
            ) {

                return LifeDomainSignalStatus
                        .CONTEXTUAL;
            }

            /*
             * timing uncertainty가 있으면
             * HEALTH 방향을 강하게 확정하지 않는다.
             */

            if (
                    context
                            .currentFunctionalSegmentTimingUncertain()
            ) {

                return LifeDomainSignalStatus
                        .CONTEXTUAL;
            }

            /*
             * =================================================
             * Capacity 같은 방향 누적
             * =================================================
             */

            List<AnnualFunctionCapacityContext>
                    accumulationContexts =
                    annualActiveContexts
                            .stream()
                            .filter(
                                    AnnualFunctionCapacityContext
                                            ::sameDirectionAccumulationContact
                            )
                            .toList();

            /*
             * =================================================
             * Capacity 반대 방향 접촉
             * =================================================
             *
             * 이것을 SUPPORTIVE로 보지 않는다.
             *
             * 다만 같은 시기에
             * 누적 방향과 반대 방향이 모두 존재하면
             * 단일 방향으로 평탄화하지 않는다.
             */

            List<AnnualFunctionCapacityContext>
                    counterDirectionContexts =
                    annualActiveContexts
                            .stream()
                            .filter(
                                    AnnualFunctionCapacityContext
                                            ::counterDirectionContact
                            )
                            .toList();

            /*
             * =================================================
             * Cross Pattern Group
             * =================================================
             */

            List<AnnualCrossFunctionalSynthesis>
                    burdenPatterns =
                    effectiveSyntheses
                            .stream()
                            .filter(
                                    this::healthBurdenPattern
                            )
                            .toList();

            List<AnnualCrossFunctionalSynthesis>
                    regulationPatterns =
                    effectiveSyntheses
                            .stream()
                            .filter(
                                    this::healthRegulationPattern
                            )
                            .toList();

            List<AnnualCrossFunctionalSynthesis>
                    internalMixtures =
                    effectiveSyntheses
                            .stream()
                            .filter(
                                    this::responsibilityInternalMixture
                            )
                            .toList();

            boolean accumulationExists =
                    !accumulationContexts
                            .isEmpty();

            boolean counterDirectionExists =
                    !counterDirectionContexts
                            .isEmpty();

            boolean burdenPatternExists =
                    !burdenPatterns
                            .isEmpty();

            boolean regulationPatternExists =
                    !regulationPatterns
                            .isEmpty();

            boolean internalMixtureExists =
                    !internalMixtures
                            .isEmpty();

            boolean mixedContext =
                    context
                            .currentMixedContext()

                            || effectiveSyntheses
                            .stream()
                            .anyMatch(
                                    this::mixedContext
                            );

            /*
             * =================================================
             * RESPONSIBILITY_INTERNAL_MIXTURE
             * =================================================
             */

            if (
                    internalMixtureExists
            ) {

                return LifeDomainSignalStatus
                        .MIXED;
            }

            /*
             * =================================================
             * Burden + Regulation
             * =================================================
             *
             * 부담 구조와 조절 구조가
             * 동시에 유효하게 작동하면
             * 어느 한쪽을 삭제하지 않는다.
             */

            if (
                    burdenPatternExists
                            && regulationPatternExists
            ) {

                return LifeDomainSignalStatus
                        .MIXED;
            }

            /*
             * =================================================
             * Capacity 방향 혼재
             * =================================================
             */

            if (
                    accumulationExists
                            && counterDirectionExists
            ) {

                return LifeDomainSignalStatus
                        .MIXED;
            }

            /*
             * =================================================
             * Burden + Context 혼재
             * =================================================
             */

            if (
                    (
                            burdenPatternExists
                                    || accumulationExists
                    )
                            && mixedContext
            ) {

                return LifeDomainSignalStatus
                        .MIXED;
            }

            /*
             * =================================================
             * 명시적 부담 Cross Pattern
             * =================================================
             */

            if (
                    burdenPatternExists
            ) {

                boolean qualification =
                        burdenPatterns
                                .stream()
                                .anyMatch(
                                        this::directionQualificationExists
                                );

                if (
                        qualification
                ) {

                    return LifeDomainSignalStatus
                            .CONTEXTUAL;
                }

                return LifeDomainSignalStatus
                        .PRESSURE;
            }

            /*
             * =================================================
             * Capacity 같은 방향 누적
             * =================================================
             */

            if (
                    accumulationExists
            ) {

                boolean qualification =
                        accumulationContexts
                                .stream()
                                .anyMatch(
                                        AnnualFunctionCapacityContext
                                                ::qualificationRequired
                                );

                if (
                        qualification
                ) {

                    return LifeDomainSignalStatus
                            .CONTEXTUAL;
                }

                return LifeDomainSignalStatus
                        .PRESSURE;
            }

            /*
             * =================================================
             * 조절 / 생성 흐름
             * =================================================
             *
             * 조절 구조가 있다고
             * 건강 호조 / 회복 / SUPPORTIVE로
             * 자동 승격하지 않는다.
             */

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        /*
         * =================================================
         * Annual-only
         * =================================================
         *
         * 대운 시작 전 또는
         * 대운 Functional Segment unavailable.
         */

        List<AnnualFunctionCapacityContext>
                accumulationContexts =
                annualActiveContexts
                        .stream()
                        .filter(
                                AnnualFunctionCapacityContext
                                        ::sameDirectionAccumulationContact
                        )
                        .toList();

        List<AnnualFunctionCapacityContext>
                counterDirectionContexts =
                annualActiveContexts
                        .stream()
                        .filter(
                                AnnualFunctionCapacityContext
                                        ::counterDirectionContact
                        )
                        .toList();

        if (
                !accumulationContexts.isEmpty()
                        && !counterDirectionContexts.isEmpty()
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                !accumulationContexts
                        .isEmpty()
        ) {

            boolean qualification =
                    accumulationContexts
                            .stream()
                            .anyMatch(
                                    AnnualFunctionCapacityContext
                                            ::qualificationRequired
                            );

            if (
                    qualification
            ) {

                return LifeDomainSignalStatus
                        .CONTEXTUAL;
            }

            return LifeDomainSignalStatus
                    .PRESSURE;
        }

        return LifeDomainSignalStatus
                .CONTEXTUAL;
    }

    /*
     * =================================================
     * 5 Function
     * =================================================
     */

    private List<AnnualFunctionCapacityContext>
    allFunctionContexts(
            AnnualFunctionalLifeDomainContext context
    ) {

        List<AnnualFunctionCapacityContext> result =
                new ArrayList<>();

        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            result.add(
                    context
                            .requireAnnualFunctionContext(
                                    functionType
                            )
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 현재 Resolved Active
     * =================================================
     */

    private List<NatalTenGodFunctionType>
    resolvedActiveFunctions(
            AnnualFunctionalLifeDomainContext context
    ) {

        if (
                !context
                        .currentFunctionalSegmentAvailable()
        ) {

            return List.of();
        }

        List<NatalTenGodFunctionType> result =
                new ArrayList<>();

        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            if (
                    context
                            .currentResolvedFunctionActive(
                                    functionType
                            )
            ) {

                result.add(
                        functionType
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 명시적 부담 Pattern
     * =================================================
     *
     * 이 Pattern들을 질병으로 해석하지 않는다.
     *
     * 생활 기능을 운용하는 과정에서
     * 마찰 / 과중 / 경합 / 억제 Context가
     * 구조적으로 유효하게 접촉하는지만 본다.
     */

    private boolean healthBurdenPattern(
            AnnualCrossFunctionalSynthesis synthesis
    ) {

        Objects.requireNonNull(
                synthesis
        );

        if (
                !synthesis
                        .usableAsEffectiveAnnualPatternContact()
        ) {

            return false;
        }

        NatalCrossFunctionalPatternType patternType =
                synthesis
                        .patternType();

        return patternType
                == NatalCrossFunctionalPatternType
                .RESOURCE_OVERLOADS_CORE_SELF

                || patternType
                == NatalCrossFunctionalPatternType
                .SELF_ASSERTION_COMPETES_FOR_RESOURCE

                || patternType
                == NatalCrossFunctionalPatternType
                .EXPRESSION_CONFLICTS_RESPONSIBILITY

                || patternType
                == NatalCrossFunctionalPatternType
                .PROCESSING_RESTRAINS_OUTPUT;
    }

    /*
     * =================================================
     * 조절 Pattern
     * =================================================
     *
     * 자동 SUPPORTIVE 아님.
     *
     * burden 구조와 동시에 존재할 때
     * 서로 다른 Functional 작동으로
     * MIXED를 판단하기 위한 Context다.
     */

    private boolean healthRegulationPattern(
            AnnualCrossFunctionalSynthesis synthesis
    ) {

        Objects.requireNonNull(
                synthesis
        );

        if (
                !synthesis
                        .usableAsEffectiveAnnualPatternContact()
        ) {

            return false;
        }

        NatalCrossFunctionalPatternType patternType =
                synthesis
                        .patternType();

        return patternType
                == NatalCrossFunctionalPatternType
                .PROCESSING_REGULATES_EXPRESSION

                || patternType
                == NatalCrossFunctionalPatternType
                .EXPRESSION_CONTROLS_PRESSURE;
    }

    /*
     * =================================================
     * 책임 기능 내부 혼재
     * =================================================
     */

    private boolean responsibilityInternalMixture(
            AnnualCrossFunctionalSynthesis synthesis
    ) {

        Objects.requireNonNull(
                synthesis
        );

        if (
                !synthesis
                        .usableAsEffectiveAnnualPatternContact()
        ) {

            return false;
        }

        return synthesis
                .patternType()
                == NatalCrossFunctionalPatternType
                .RESPONSIBILITY_INTERNAL_MIXTURE;
    }

    /*
     * =================================================
     * Direction Qualification
     * =================================================
     */

    private boolean directionQualificationExists(
            AnnualCrossFunctionalSynthesis synthesis
    ) {

        Objects.requireNonNull(
                synthesis
        );

        return synthesis
                .qualifiedContextExists()

                || synthesis
                .weakenedContextExists()

                || synthesis
                .conflictedContextExists()

                || synthesis
                .mixedContextStatus();
    }

    /*
     * =================================================
     * Context 혼재
     * =================================================
     */

    private boolean mixedContext(
            AnnualCrossFunctionalSynthesis synthesis
    ) {

        Objects.requireNonNull(
                synthesis
        );

        if (
                synthesis
                        .mixedContextStatus()
        ) {

            return true;
        }

        boolean supportiveSide =
                synthesis
                        .supportedContextExists()

                        || synthesis
                        .qualifiedContextExists();

        boolean limitingSide =
                synthesis
                        .weakenedContextExists()

                        || synthesis
                        .conflictedContextExists();

        return supportiveSide
                && limitingSide;
    }

    /*
     * =================================================
     * Identity
     * =================================================
     */

    private void validateIdentity(

            AnnualFunctionalLifeDomainContext context,

            List<AnnualFunctionCapacityContext>
                    functionContexts

    ) {

        Objects.requireNonNull(
                context,
                "Annual Functional Context가 필요합니다."
        );

        Objects.requireNonNull(
                functionContexts,
                "HEALTH_LIFESTYLE Function Capacity 목록이 필요합니다."
        );

        if (
                functionContexts.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "HEALTH_LIFESTYLE Annual Functional Rule에는 모든 Function Capacity가 필요합니다. "
                            + "expected="
                            + NatalTenGodFunctionType.values().length
                            + ", actual="
                            + functionContexts.size()
            );
        }

        for (
                AnnualFunctionCapacityContext functionContext
                : functionContexts
        ) {

            Objects.requireNonNull(
                    functionContext,
                    "HEALTH_LIFESTYLE Function Capacity는 null일 수 없습니다."
            );

            if (
                    functionContext
                            .targetYear()
                            != context
                            .targetYear()
            ) {

                throw new IllegalArgumentException(
                        "Annual Functional Context와 Function Capacity의 연도가 일치하지 않습니다. "
                                + "contextYear="
                                + context.targetYear()
                                + ", function="
                                + functionContext.functionType()
                                + ", functionYear="
                                + functionContext.targetYear()
                );
            }

            if (
                    functionContext
                            .completeChart()
                            != context
                            .completeChart()
            ) {

                throw new IllegalArgumentException(
                        "Annual Functional Context와 Function Capacity의 completeChart가 일치하지 않습니다. "
                                + "function="
                                + functionContext.functionType()
                );
            }
        }
    }

    /*
     * =================================================
     * Reasons
     * =================================================
     */

    private List<String> buildReasons(

            AnnualFunctionalLifeDomainContext context,

            List<AnnualFunctionCapacityContext>
                    functionContexts,

            List<AnnualFunctionCapacityContext>
                    annualActiveContexts,

            List<NatalTenGodFunctionType>
                    resolvedActiveFunctions,

            List<AnnualCrossFunctionalSynthesis>
                    contactedSyntheses,

            List<AnnualCrossFunctionalSynthesis>
                    effectiveSyntheses,

            List<AnnualCrossFunctionalSynthesis>
                    referenceSyntheses,

            LifeDomainSignalStatus status

    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "HEALTH_LIFESTYLE Annual Functional은 특정 십성이나 Function을 질병·장기·병명에 직접 대응시키지 않고 다섯 Functional 축 전체의 Capacity와 CrossFunctional 부하를 생활 리듬 Context로만 읽습니다."
        );

        reasons.add(
                "ANNUAL_FUNCTIONAL_CONTEXT 하나에서 Annual Capacity와 현재 대운 Segment의 CrossFunctional 결과를 함께 사용하여 동일 파생 근거를 독립 Evidence처럼 중복 계산하지 않습니다."
        );

        /*
         * =================================================
         * Annual Function
         * =================================================
         */

        reasons.add(
                "Annual에서 활성된 Functional 축은 "
                        + annualActiveContexts
                        .stream()
                        .map(
                                AnnualFunctionCapacityContext
                                        ::functionType
                        )
                        .toList()
                        + "입니다."
        );

        for (
                AnnualFunctionCapacityContext functionContext
                : functionContexts
        ) {

            if (
                    !functionContext
                            .active()
            ) {

                continue;
            }

            reasons.add(
                    "Annual "
                            + functionContext.functionType()
                            + " 활성: directInputActive="
                            + functionContext.directInputActive()
                            + ", transformedTargetActive="
                            + functionContext.transformedTargetActive()
                            + ", capacitySide="
                            + functionContext.capacitySide()
                            + "입니다."
            );

            /*
             * =================================================
             * Same Direction
             * =================================================
             */

            if (
                    functionContext
                            .sameDirectionAccumulationContact()
            ) {

                reasons.add(
                        functionContext.functionType()
                                + "은 원국 고정 Strength 기준 같은 방향 누적 Contact에 해당하여 생활 기능 Capacity의 추가 부하 가능성을 보존합니다."
                );

                reasons.add(
                        "같은 방향 누적은 질병·발병·건강 악화를 의미하지 않습니다."
                );
            }

            /*
             * =================================================
             * Counter Direction
             * =================================================
             */

            if (
                    functionContext
                            .counterDirectionContact()
            ) {

                reasons.add(
                        functionContext.functionType()
                                + "은 원국 고정 Strength 기준 반대 방향 Contact에 해당하지만 이를 회복·건강 호조 또는 SUPPORTIVE로 자동 변환하지 않습니다."
                );
            }

            /*
             * =================================================
             * Transformation
             * =================================================
             */

            if (
                    functionContext
                            .transformedTargetActive()
            ) {

                reasons.add(
                        functionContext.functionType()
                                + "에는 Transformation Target 활성 경로가 존재하며 직접 입력과 별도 독립 건강 근거로 중복 계산하지 않습니다."
                );
            }

            /*
             * =================================================
             * Original Function
             * =================================================
             */

            if (
                    functionContext
                            .annualOriginalFunctionReductionExists()
            ) {

                reasons.add(
                        functionContext.functionType()
                                + "의 Annual 원기능 감소가 존재하지만 기능 소실·신체 기능 저하·질병 사건으로 변환하지 않습니다."
                );
            }

            if (
                    functionContext
                            .mixedAnnualOriginalFunctionCondition()
            ) {

                reasons.add(
                        functionContext.functionType()
                                + "의 Annual 원기능 상태가 혼재되어 단일 작동 방향으로 평탄화하지 않습니다."
                );
            }

            /*
             * =================================================
             * Restrictive Natal Relation
             * =================================================
             */

            if (
                    functionContext
                            .restrictiveNatalRelationContextExists()
            ) {

                reasons.add(
                        functionContext.functionType()
                                + " 입력에는 제한적 원국 Relation Context가 존재하여 Capacity 단독 판정을 제한합니다."
                );
            }
        }

        /*
         * =================================================
         * Fixed Core
         * =================================================
         */

        AnnualFunctionCapacityContext representative =
                functionContexts
                        .get(
                                0
                        );

        reasons.add(
                "일간 Strength는 세운마다 다시 계산하지 않고 원국 고정값을 사용합니다. natalStrengthLevel="
                        + representative.natalStrengthLevel()
                        + "입니다."
        );

        if (
                representative
                        .natalBalancedZone()
        ) {

            reasons.add(
                    "원국 Strength가 balanced zone이므로 support/pressure 방향만으로 HEALTH_LIFESTYLE 결과를 확정하지 않습니다."
            );
        }

        if (
                representative
                        .natalExtreme()
        ) {

            reasons.add(
                    "원국 Strength가 극단 영역이므로 일반 신강·신약 방향을 건강·생활 결과에 단순 적용하지 않습니다."
            );
        }

        if (
                representative
                        .followStructureReviewRequired()
        ) {

            reasons.add(
                    "원국에서 종격 가능성 추가 검토가 필요하여 일반 Capacity 방향을 단독 HEALTH_LIFESTYLE 판정 근거로 사용하지 않습니다."
            );
        }

        /*
         * =================================================
         * Current Segment
         * =================================================
         */

        if (
                context
                        .currentFunctionalSegmentAvailable()
        ) {

            int sequence =
                    context
                            .timelineDecadeSequenceNumber()
                            .orElseThrow();

            reasons.add(
                    "현재 16차 Timeline이 선택한 대운 Sequence "
                            + sequence
                            + "에 대응하는 Annual Functional Segment만 사용하며 다른 대운 Segment를 평균하거나 임의 선택하지 않습니다."
            );

            reasons.add(
                    "현재 대운 결합 후 최종 활성 Functional 축은 "
                            + resolvedActiveFunctions
                            + "입니다."
            );

            reasons.add(
                    "현재 CrossFunctional Contact는 "
                            + contactedSyntheses.size()
                            + "개이며, 그중 structurally effective Full Contact는 "
                            + effectiveSyntheses.size()
                            + "개입니다."
            );

            for (
                    AnnualCrossFunctionalSynthesis synthesis
                    : effectiveSyntheses
            ) {

                reasons.add(
                        "유효 HEALTH_LIFESTYLE 관련 Functional 구조: "
                                + synthesis
                                .patternType()
                                .koreanName()
                                + " [type="
                                + synthesis.patternType()
                                + ", newlyCompleted="
                                + synthesis.newlyCompleted()
                                + ", decadeContinuation="
                                + synthesis.decadeFullContinuation()
                                + ", transformationParticipant="
                                + synthesis.transformationTargetParticipantContact()
                                + ", originalFunctionReduction="
                                + synthesis.originalFunctionReductionExists()
                                + ", qualificationRequired="
                                + synthesis.qualificationRequired()
                                + "]"
                );

                if (
                        synthesis
                                .exactTenGodContextMatchExists()
                ) {

                    reasons.add(
                            synthesis.patternType().koreanName()
                                    + "의 EXACT TenGod Context Match는 동일 Functional 구조의 Context 재확인일 뿐 별도 독립 HEALTH Evidence 또는 Pattern 승격 근거로 계산하지 않습니다."
                    );
                }

                appendPatternReason(
                        reasons,
                        synthesis
                );
            }

        } else {

            reasons.add(
                    "현재 16차 Timeline에서 사용할 수 있는 정확한 대운 Functional Segment가 없어 Annual 자체 5 Function Capacity까지만 사용합니다."
            );
        }

        /*
         * =================================================
         * REFERENCE
         * =================================================
         */

        if (
                !referenceSyntheses
                        .isEmpty()
        ) {

            reasons.add(
                    "REFERENCE Cross Pattern Contact가 "
                            + referenceSyntheses.size()
                            + "개 존재하지만 HEALTH_LIFESTYLE 방향 판정 근거로 승격하지 않습니다."
            );
        }

        /*
         * =================================================
         * Segment Context
         * =================================================
         */

        if (
                context
                        .currentMixedContext()
        ) {

            reasons.add(
                    "현재 Annual-Decade Segment의 14-6 Context에 서로 다른 상태가 함께 존재하여 생활 기능의 작동 방향을 하나로 평탄화하지 않습니다."
            );
        }

        if (
                context
                        .currentFunctionalSegmentTimingUncertain()
        ) {

            reasons.add(
                    "현재 Functional Segment에는 timing uncertainty가 존재하여 HEALTH_LIFESTYLE 방향을 강하게 확정하지 않습니다."
            );
        }

        /*
         * =================================================
         * Final
         * =================================================
         */

        switch (
                status
        ) {

            case PRESSURE ->
                    reasons.add(
                            "원국 Capacity와 같은 방향의 Functional 누적 또는 구조적으로 유효한 과중·경합·충돌·억제 Pattern이 확인되어 생활 기능의 부하 방향을 PRESSURE로 보존합니다. 이는 질병 또는 발병 판정이 아닙니다."
                    );

            case MIXED ->
                    reasons.add(
                            "생활 기능에서 누적과 반대 방향 Contact, 또는 부담 구조와 조절 구조가 함께 존재하여 MIXED로 보존합니다."
                    );

            case CONTEXTUAL ->
                    reasons.add(
                            "Functional 활성과 구조적 접촉은 확인되지만 현재 근거만으로 건강 상태나 실제 질병을 추정하지 않고 생활 리듬·Capacity Context로만 유지합니다."
                    );

            case SUPPORTIVE ->
                    reasons.add(
                            "현재 Rule은 반대 방향 Capacity Contact나 조절 Pattern만으로 건강 호조·회복 또는 SUPPORTIVE를 생성하지 않습니다."
                    );
        }

        return List.copyOf(
                reasons
        );
    }

    /*
     * =================================================
     * Pattern 설명
     * =================================================
     */

    private void appendPatternReason(

            List<String> reasons,

            AnnualCrossFunctionalSynthesis synthesis

    ) {

        NatalCrossFunctionalPatternType patternType =
                synthesis
                        .patternType();

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .RESOURCE_OVERLOADS_CORE_SELF
        ) {

            reasons.add(
                    "RESOURCE_OVERLOADS_CORE_SELF는 현실적 자원·조건 요구가 자기 기반에 비해 과중한 Functional 구조로 보존하며 신체 질환이나 건강 악화 사건으로 변환하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .SELF_ASSERTION_COMPETES_FOR_RESOURCE
        ) {

            reasons.add(
                    "SELF_ASSERTION_COMPETES_FOR_RESOURCE는 자기 주장·동류 기능과 현실 자원 기능의 경합 구조로 보존하며 신체 증상으로 변환하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .EXPRESSION_CONFLICTS_RESPONSIBILITY
        ) {

            reasons.add(
                    "EXPRESSION_CONFLICTS_RESPONSIBILITY는 표현·실행 기능과 책임·규칙 기능 사이의 마찰 구조로 보존하며 사고·질환·스트레스성 질병 같은 사건을 자동 생성하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .PROCESSING_RESTRAINS_OUTPUT
        ) {

            reasons.add(
                    "PROCESSING_RESTRAINS_OUTPUT은 처리·수용 기능이 표현·산출 기능을 억제하는 구조적 부담 Context로 보존하며 특정 건강 증상으로 변환하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .RESPONSIBILITY_INTERNAL_MIXTURE
        ) {

            reasons.add(
                    "RESPONSIBILITY_INTERNAL_MIXTURE는 책임 기능 내부의 서로 다른 작동 모드가 함께 존재하는 구조이므로 단순 PRESSURE 하나로 평탄화하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .PROCESSING_REGULATES_EXPRESSION
        ) {

            reasons.add(
                    "PROCESSING_REGULATES_EXPRESSION은 처리·수용 기능이 표현·산출 기능을 조절하는 구조로 보존하지만 건강 회복이나 SUPPORTIVE로 자동 승격하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .EXPRESSION_CONTROLS_PRESSURE
        ) {

            reasons.add(
                    "EXPRESSION_CONTROLS_PRESSURE는 표현·실행 기능이 압력 기능을 제어하는 구조로 보존하지만 생활 안정·건강 호조로 자동 변환하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .RESPONSIBILITY_GENERATES_PROCESSING
        ) {

            reasons.add(
                    "RESPONSIBILITY_GENERATES_PROCESSING은 책임 기능이 처리·수용 기능으로 이어지는 흐름이며 회복력이나 건강 개선을 자동 의미하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .PRESSURE_GENERATES_PROCESSING
        ) {

            reasons.add(
                    "PRESSURE_GENERATES_PROCESSING은 압력 기능이 처리·수용 기능으로 이어지는 흐름이며 이를 스트레스 적응이나 건강 회복으로 자동 해석하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .RESOURCE_GENERATES_RESPONSIBILITY
        ) {

            reasons.add(
                    "RESOURCE_GENERATES_RESPONSIBILITY는 현실 자원 요구가 책임 기능으로 이어지는 구조적 흐름으로 보존하며 건강 결과를 직접 만들지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .RESOURCE_CONTROLS_PROCESSING
        ) {

            reasons.add(
                    "RESOURCE_CONTROLS_PROCESSING은 현실 자원 기능과 처리·수용 기능 사이의 제어 관계로 보존하며 특정 생활 습관 또는 신체 문제로 변환하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * Summary
     * =================================================
     */

    private String summaryOf(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case PRESSURE ->
                    "현재 세운의 여러 Functional 축에서 원국 Capacity와 같은 방향의 누적 또는 유효한 과중·마찰 구조가 확인되어 생활 기능의 부하 방향이 존재합니다.";

            case MIXED ->
                    "현재 세운의 생활 Functional에 부하 방향과 조절·반대 방향의 작동이 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 세운의 Functional 활성과 구조적 접촉은 확인되지만 건강 상태나 질병을 직접 추정하지 않고 생활 리듬 Context로만 보존합니다.";

            case SUPPORTIVE ->
                    "현재 세운의 생활 Functional에 지지 방향이 확인됩니다.";
        };
    }

    /*
     * =================================================
     * Evidence
     * =================================================
     */

    private Optional<LifeDomainEvidenceReference<?>>
    optionalSingleEvidence(

            LifeDomainEvidenceBundle bundle,

            LifeDomainEvidenceSourceType sourceType

    ) {

        Objects.requireNonNull(
                bundle
        );

        Objects.requireNonNull(
                sourceType
        );

        List<LifeDomainEvidenceReference<?>>
                evidences =
                bundle
                        .evidencesOf(
                                sourceType
                        );

        if (
                evidences.isEmpty()
        ) {

            return Optional.empty();
        }

        if (
                evidences.size()
                        != 1
        ) {

            throw new IllegalStateException(
                    "HealthLifestyleAnnualFunctionalRule에는 최대 하나의 "
                            + sourceType
                            + " Evidence가 허용됩니다. actual="
                            + evidences.size()
            );
        }

        return Optional.of(
                evidences.get(
                        0
                )
        );
    }
}