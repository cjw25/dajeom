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
 * LEARNING_GROWTH 통합 Rule.
 *
 * =================================================
 *
 * Source:
 *
 * ANNUAL_FUNCTIONAL_CONTEXT
 *
 * =================================================
 *
 * 이 Domain은 하나의 Function이 아니라
 * 두 Functional 축을 함께 본다.
 *
 * EXPRESSION
 *
 * - 표현
 * - 산출
 * - 실행
 * - 외부로 드러내는 기능
 *
 * PROCESSING
 *
 * - 수용
 * - 정리
 * - 처리
 * - 학습 기반 기능
 *
 * =================================================
 *
 * 하나의 ANNUAL_FUNCTIONAL_CONTEXT 안에서:
 *
 * 1.
 * Annual EXPRESSION Capacity
 *
 * 2.
 * Annual PROCESSING Capacity
 *
 * 3.
 * 현재 Timeline 대운 Segment의
 *    Resolved EXPRESSION
 *
 * 4.
 * 현재 Timeline 대운 Segment의
 *    Resolved PROCESSING
 *
 * 5.
 * EXPRESSION / PROCESSING 관련
 *    CrossFunctional Pattern
 *
 * 6.
 * 14-6 Context Qualification
 *
 * 을 하나의 Match에서 합성한다.
 *
 * =================================================
 *
 * 절대 하지 않는 변환:
 *
 * 인성 활성
 * -> 공부 잘됨
 *
 * 식상 활성
 * -> 성과 좋음
 *
 * PROCESSING_GENERATION 계열
 * -> 시험 합격
 *
 * PROCESSING_RESTRAINS_OUTPUT
 * -> 공부 실패
 *
 * 같은 사건 추론.
 *
 * =================================================
 *
 * 핵심 Pattern 구분:
 *
 * -------------------------------------------------
 * PROCESSING_REGULATES_EXPRESSION
 * -------------------------------------------------
 *
 * 처리·수용 기능이
 * 표현·산출 기능을 조절하는 구조.
 *
 * 구조적 조절 Context로 보존한다.
 *
 * 자동 SUPPORTIVE로 만들지 않는다.
 *
 * -------------------------------------------------
 * PROCESSING_RESTRAINS_OUTPUT
 * -------------------------------------------------
 *
 * 처리·수용 기능이
 * 표현·산출 기능을 억제하는 구조.
 *
 * structurally effective Full Contact라면
 * LEARNING_GROWTH Pressure 후보가 될 수 있다.
 *
 * 단 실제:
 *
 * 학업 실패
 * 표현 실패
 * 시험 실패
 *
 * 같은 사건은 만들지 않는다.
 *
 * -------------------------------------------------
 * RESPONSIBILITY_GENERATES_PROCESSING
 * PRESSURE_GENERATES_PROCESSING
 * -------------------------------------------------
 *
 * 책임 / 압력 기능이
 * PROCESSING으로 이어지는 구조.
 *
 * 학습·처리 기능 유입 Context로 보존한다.
 *
 * 자동 SUPPORTIVE / 합격으로 만들지 않는다.
 *
 * =================================================
 *
 * Capacity 방향:
 *
 * EXPRESSION = pressure-side
 * PROCESSING = support-side
 *
 * 원국 고정 Strength를 기준으로
 * sameDirectionAccumulationContact가 있으면
 * 기능 누적 부담 후보가 된다.
 *
 * 그러나:
 *
 * - balanced
 * - extreme
 * - follow structure review
 * - transformation target
 * - original function reduction
 * - restrictive relation
 *
 * 등이 있으면 단독 방향 판정을 제한한다.
 *
 * =================================================
 *
 * SUPPORTIVE:
 *
 * 이 Rule 단독으로는 생성하지 않는다.
 *
 * 학습·성장 영역의 실제 지지 방향은
 * UsefulGod / Structure / 독립 Domain Evidence와
 * 이후 합성해야 한다.
 */
@Component
public class LearningGrowthAnnualFunctionalRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "LEARNING_GROWTH_ANNUAL_FUNCTIONAL";

    private static final NatalTenGodFunctionType
            EXPRESSION_FUNCTION =
            NatalTenGodFunctionType
                    .EXPRESSION;

    private static final NatalTenGodFunctionType
            PROCESSING_FUNCTION =
            NatalTenGodFunctionType
                    .PROCESSING;

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

        return LifeDomain.LEARNING_GROWTH;
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
                "LearningGrowthAnnualFunctionalRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.LEARNING_GROWTH
        ) {

            throw new IllegalArgumentException(
                    "LearningGrowthAnnualFunctionalRule은 LEARNING_GROWTH Bundle만 평가할 수 있습니다."
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
         * Annual Capacity
         * =================================================
         */

        AnnualFunctionCapacityContext expressionContext =
                context
                        .requireAnnualFunctionContext(
                                EXPRESSION_FUNCTION
                        );

        AnnualFunctionCapacityContext processingContext =
                context
                        .requireAnnualFunctionContext(
                                PROCESSING_FUNCTION
                        );

        validateIdentity(
                context,
                expressionContext,
                processingContext
        );

        boolean annualExpressionActive =
                expressionContext
                        .active();

        boolean annualProcessingActive =
                processingContext
                        .active();

        /*
         * =================================================
         * 현재 대운 결합 후 Resolved Function
         * =================================================
         */

        boolean resolvedExpressionActive =
                context
                        .currentResolvedFunctionActive(
                                EXPRESSION_FUNCTION
                        );

        boolean resolvedProcessingActive =
                context
                        .currentResolvedFunctionActive(
                                PROCESSING_FUNCTION
                        );

        /*
         * Annual 자체 입력은 없어도
         * Transformation Target 등을 통해
         * 현재 Segment에서 활성될 수 있다.
         */

        if (
                !annualExpressionActive
                        && !annualProcessingActive
                        && !resolvedExpressionActive
                        && !resolvedProcessingActive
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 관련 Cross Pattern
         * =================================================
         */

        List<AnnualCrossFunctionalSynthesis>
                learningContacts =
                learningRelated(
                        context
                                .currentContactedSyntheses()
                );

        List<AnnualCrossFunctionalSynthesis>
                effectiveLearningContacts =
                learningRelated(
                        context
                                .currentEffectiveFullContacts()
                );

        List<AnnualCrossFunctionalSynthesis>
                referenceLearningContacts =
                learningRelated(
                        context
                                .currentReferenceContacts()
                );

        LifeDomainSignalStatus status =
                resolveStatus(
                        context,
                        expressionContext,
                        processingContext,
                        annualExpressionActive,
                        annualProcessingActive,
                        resolvedExpressionActive,
                        resolvedProcessingActive,
                        effectiveLearningContacts
                );

        List<String> reasons =
                buildReasons(
                        context,
                        expressionContext,
                        processingContext,
                        annualExpressionActive,
                        annualProcessingActive,
                        resolvedExpressionActive,
                        resolvedProcessingActive,
                        learningContacts,
                        effectiveLearningContacts,
                        referenceLearningContacts,
                        status
                );

        return List.of(
                new LifeDomainRuleMatch(
                        LifeDomain.LEARNING_GROWTH,
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

            AnnualFunctionCapacityContext expressionContext,

            AnnualFunctionCapacityContext processingContext,

            boolean annualExpressionActive,

            boolean annualProcessingActive,

            boolean resolvedExpressionActive,

            boolean resolvedProcessingActive,

            List<AnnualCrossFunctionalSynthesis>
                    effectiveLearningContacts

    ) {

        /*
         * =================================================
         * 불완전 원국
         * =================================================
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
         * 현재 Annual-Decade Functional Segment
         * =================================================
         */

        if (
                context
                        .currentFunctionalSegmentAvailable()
        ) {

            /*
             * Annual에서는 들어왔지만
             * 대운 결합 후 두 기능이 모두 inactive.
             */

            if (
                    !resolvedExpressionActive
                            && !resolvedProcessingActive
            ) {

                return LifeDomainSignalStatus
                        .CONTEXTUAL;
            }

            /*
             * Timing uncertainty.
             */

            if (
                    context
                            .currentFunctionalSegmentTimingUncertain()
            ) {

                return LifeDomainSignalStatus
                        .CONTEXTUAL;
            }

            List<AnnualCrossFunctionalSynthesis>
                    regulationPatterns =
                    effectiveLearningContacts
                            .stream()
                            .filter(
                                    this::processingRegulatesExpression
                            )
                            .toList();

            List<AnnualCrossFunctionalSynthesis>
                    restraintPatterns =
                    effectiveLearningContacts
                            .stream()
                            .filter(
                                    this::processingRestrainsOutput
                            )
                            .toList();

            boolean regulationExists =
                    !regulationPatterns
                            .isEmpty();

            boolean restraintExists =
                    !restraintPatterns
                            .isEmpty();

            /*
             * =================================================
             * Capacity same-direction accumulation
             * =================================================
             */

            boolean expressionCapacityPressure =
                    annualExpressionActive
                            && expressionContext
                            .sameDirectionAccumulationContact();

            boolean processingCapacityPressure =
                    annualProcessingActive
                            && processingContext
                            .sameDirectionAccumulationContact();

            boolean capacityPressure =
                    expressionCapacityPressure
                            || processingCapacityPressure;

            /*
             * 반대 방향 Capacity Contact도
             * 동시에 존재하는지 보존한다.
             */

            boolean expressionCounterDirection =
                    annualExpressionActive
                            && expressionContext
                            .counterDirectionContact();

            boolean processingCounterDirection =
                    annualProcessingActive
                            && processingContext
                            .counterDirectionContact();

            boolean counterDirectionExists =
                    expressionCounterDirection
                            || processingCounterDirection;

            boolean mixedContext =
                    context
                            .currentMixedContext()

                            || effectiveLearningContacts
                            .stream()
                            .anyMatch(
                                    this::mixedContext
                            );

            /*
             * =================================================
             * PROCESSING restraint + regulation 동시
             * =================================================
             *
             * 같은 PROCESSING → EXPRESSION 계열이라도
             * 조절과 억제를 하나로 합치지 않는다.
             */

            if (
                    restraintExists
                            && regulationExists
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
                    capacityPressure
                            && counterDirectionExists
            ) {

                return LifeDomainSignalStatus
                        .MIXED;
            }

            /*
             * =================================================
             * Pressure + Context 혼재
             * =================================================
             */

            if (
                    (
                            restraintExists
                                    || capacityPressure
                    )
                            && mixedContext
            ) {

                return LifeDomainSignalStatus
                        .MIXED;
            }

            /*
             * =================================================
             * PROCESSING_RESTRAINS_OUTPUT
             * =================================================
             */

            if (
                    restraintExists
            ) {

                boolean qualified =
                        restraintPatterns
                                .stream()
                                .anyMatch(
                                        this::directionQualificationExists
                                );

                if (
                        qualified
                ) {

                    return LifeDomainSignalStatus
                            .CONTEXTUAL;
                }

                return LifeDomainSignalStatus
                        .PRESSURE;
            }

            /*
             * =================================================
             * Capacity Pressure
             * =================================================
             */

            if (
                    capacityPressure
            ) {

                boolean qualificationRequired =
                        (
                                expressionCapacityPressure
                                        && expressionContext
                                        .qualificationRequired()
                        )

                                || (
                                processingCapacityPressure
                                        && processingContext
                                        .qualificationRequired()
                        )

                                || context
                                .currentSegmentQualificationRequired();

                if (
                        qualificationRequired
                ) {

                    return LifeDomainSignalStatus
                            .CONTEXTUAL;
                }

                return LifeDomainSignalStatus
                        .PRESSURE;
            }

            /*
             * =================================================
             * PROCESSING_REGULATES_EXPRESSION
             * =================================================
             *
             * 조절 구조가 있어도
             * 자동 SUPPORTIVE로 만들지 않는다.
             */

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        /*
         * =================================================
         * Annual-only
         * =================================================
         */

        boolean annualExpressionPressure =
                annualExpressionActive
                        && expressionContext
                        .sameDirectionAccumulationContact();

        boolean annualProcessingPressure =
                annualProcessingActive
                        && processingContext
                        .sameDirectionAccumulationContact();

        boolean annualPressure =
                annualExpressionPressure
                        || annualProcessingPressure;

        boolean annualCounterDirection =
                (
                        annualExpressionActive
                                && expressionContext
                                .counterDirectionContact()
                )

                        || (
                        annualProcessingActive
                                && processingContext
                                .counterDirectionContact()
                );

        if (
                annualPressure
                        && annualCounterDirection
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                annualPressure
        ) {

            boolean qualification =
                    (
                            annualExpressionPressure
                                    && expressionContext
                                    .qualificationRequired()
                    )

                            || (
                            annualProcessingPressure
                                    && processingContext
                                    .qualificationRequired()
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
     * Learning 관련 Pattern
     * =================================================
     */

    private List<AnnualCrossFunctionalSynthesis>
    learningRelated(

            List<AnnualCrossFunctionalSynthesis>
                    syntheses

    ) {

        if (
                syntheses == null
                        || syntheses.isEmpty()
        ) {

            return List.of();
        }

        return syntheses
                .stream()
                .filter(
                        synthesis ->
                                involvesLearningFunction(
                                        synthesis
                                                .patternType()
                                )
                )
                .toList();
    }

    private boolean involvesLearningFunction(
            NatalCrossFunctionalPatternType patternType
    ) {

        Objects.requireNonNull(
                patternType,
                "CrossFunctional Pattern Type이 필요합니다."
        );

        if (
                learningFunction(
                        patternType
                                .primaryFunction()
                )
        ) {

            return true;
        }

        return patternType
                .secondaryFunction()
                .map(
                        this::learningFunction
                )
                .orElse(
                        false
                );
    }

    private boolean learningFunction(
            NatalTenGodFunctionType functionType
    ) {

        return functionType
                == EXPRESSION_FUNCTION

                || functionType
                == PROCESSING_FUNCTION;
    }

    /*
     * =================================================
     * PROCESSING_REGULATES_EXPRESSION
     * =================================================
     */

    private boolean processingRegulatesExpression(
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
                .PROCESSING_REGULATES_EXPRESSION;
    }

    /*
     * =================================================
     * PROCESSING_RESTRAINS_OUTPUT
     * =================================================
     */

    private boolean processingRestrainsOutput(
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
                .PROCESSING_RESTRAINS_OUTPUT;
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

            AnnualFunctionCapacityContext expressionContext,

            AnnualFunctionCapacityContext processingContext

    ) {

        Objects.requireNonNull(
                context
        );

        Objects.requireNonNull(
                expressionContext
        );

        Objects.requireNonNull(
                processingContext
        );

        if (
                expressionContext
                        .functionType()
                        != EXPRESSION_FUNCTION
        ) {

            throw new IllegalArgumentException(
                    "LEARNING_GROWTH Rule에 EXPRESSION이 아닌 Function이 전달되었습니다: "
                            + expressionContext.functionType()
            );
        }

        if (
                processingContext
                        .functionType()
                        != PROCESSING_FUNCTION
        ) {

            throw new IllegalArgumentException(
                    "LEARNING_GROWTH Rule에 PROCESSING이 아닌 Function이 전달되었습니다: "
                            + processingContext.functionType()
            );
        }

        if (
                expressionContext
                        .targetYear()
                        != context.targetYear()

                        || processingContext
                        .targetYear()
                        != context.targetYear()
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Context와 LEARNING_GROWTH Function Capacity의 연도가 일치하지 않습니다. "
                            + "contextYear="
                            + context.targetYear()
                            + ", expressionYear="
                            + expressionContext.targetYear()
                            + ", processingYear="
                            + processingContext.targetYear()
            );
        }

        if (
                expressionContext
                        .completeChart()
                        != context.completeChart()

                        || processingContext
                        .completeChart()
                        != context.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Context와 LEARNING_GROWTH Function Capacity의 completeChart가 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * Reasons
     * =================================================
     */

    private List<String> buildReasons(

            AnnualFunctionalLifeDomainContext context,

            AnnualFunctionCapacityContext expressionContext,

            AnnualFunctionCapacityContext processingContext,

            boolean annualExpressionActive,

            boolean annualProcessingActive,

            boolean resolvedExpressionActive,

            boolean resolvedProcessingActive,

            List<AnnualCrossFunctionalSynthesis>
                    learningContacts,

            List<AnnualCrossFunctionalSynthesis>
                    effectiveLearningContacts,

            List<AnnualCrossFunctionalSynthesis>
                    referenceLearningContacts,

            LifeDomainSignalStatus status

    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "ANNUAL_FUNCTIONAL_CONTEXT 하나에서 EXPRESSION과 PROCESSING Capacity 및 현재 대운 Segment의 CrossFunctional 결과를 함께 읽어 같은 파생 근거를 독립 Evidence처럼 중복 계산하지 않습니다."
        );

        reasons.add(
                "LEARNING_GROWTH는 EXPRESSION과 PROCESSING 두 Functional 축을 함께 사용하며 단일 십성 도착만으로 학습·성과 결과를 확정하지 않습니다."
        );

        /*
         * =================================================
         * Annual Activation
         * =================================================
         */

        reasons.add(
                "Annual EXPRESSION 활성 상태는 "
                        + annualExpressionActive
                        + "입니다. directInputActive="
                        + expressionContext.directInputActive()
                        + ", transformedTargetActive="
                        + expressionContext.transformedTargetActive()
                        + "입니다."
        );

        reasons.add(
                "Annual PROCESSING 활성 상태는 "
                        + annualProcessingActive
                        + "입니다. directInputActive="
                        + processingContext.directInputActive()
                        + ", transformedTargetActive="
                        + processingContext.transformedTargetActive()
                        + "입니다."
        );

        reasons.add(
                "일간 Strength는 세운에서 재계산하지 않고 원국 고정값을 사용합니다. natalStrengthLevel="
                        + expressionContext.natalStrengthLevel()
                        + "입니다."
        );

        /*
         * =================================================
         * EXPRESSION Capacity
         * =================================================
         */

        if (
                expressionContext
                        .sameDirectionAccumulationContact()
        ) {

            reasons.add(
                    "EXPRESSION 활성은 원국 Capacity 기준 같은 방향 누적 Contact에 해당하여 표현·산출 기능의 부담 가능성을 보존합니다."
            );
        }

        if (
                expressionContext
                        .counterDirectionContact()
        ) {

            reasons.add(
                    "EXPRESSION 활성은 원국 Capacity 기준 반대 방향 Contact에 해당하지만 이를 학습 성과의 SUPPORTIVE로 자동 승격하지 않습니다."
            );
        }

        if (
                expressionContext
                        .annualOriginalFunctionReductionExists()
        ) {

            reasons.add(
                    "Annual EXPRESSION 원기능 감소가 존재하며 표현 능력 상실이나 성과 실패 사건으로 변환하지 않습니다."
            );
        }

        if (
                expressionContext
                        .transformedTargetActive()
        ) {

            reasons.add(
                    "EXPRESSION에는 Transformation Target 활성 경로가 존재하며 직접 입력과 별도의 독립 성과 근거로 중복 계산하지 않습니다."
            );
        }

        /*
         * =================================================
         * PROCESSING Capacity
         * =================================================
         */

        if (
                processingContext
                        .sameDirectionAccumulationContact()
        ) {

            reasons.add(
                    "PROCESSING 활성은 원국 Capacity 기준 같은 방향 누적 Contact에 해당하여 처리·수용 기능의 누적 부담 가능성을 보존합니다."
            );
        }

        if (
                processingContext
                        .counterDirectionContact()
        ) {

            reasons.add(
                    "PROCESSING 활성은 원국 Capacity 기준 반대 방향 Contact에 해당하지만 이를 학습운 호조나 SUPPORTIVE로 자동 승격하지 않습니다."
            );
        }

        if (
                processingContext
                        .annualOriginalFunctionReductionExists()
        ) {

            reasons.add(
                    "Annual PROCESSING 원기능 감소가 존재하며 학습 능력 저하나 실패 사건으로 변환하지 않습니다."
            );
        }

        if (
                processingContext
                        .transformedTargetActive()
        ) {

            reasons.add(
                    "PROCESSING에는 Transformation Target 활성 경로가 존재하며 직접 입력과 별도의 독립 학습 근거로 중복 계산하지 않습니다."
            );
        }

        /*
         * =================================================
         * Fixed Core Qualification
         * =================================================
         */

        if (
                expressionContext.natalBalancedZone()
                        || processingContext.natalBalancedZone()
        ) {

            reasons.add(
                    "원국 Strength가 balanced zone이므로 support/pressure 방향만으로 LEARNING_GROWTH 결과를 확정하지 않습니다."
            );
        }

        if (
                expressionContext.natalExtreme()
                        || processingContext.natalExtreme()
        ) {

            reasons.add(
                    "원국 Strength가 극단 영역이므로 일반 신강·신약 방향만으로 표현·처리 기능의 결과를 확정하지 않습니다."
            );
        }

        if (
                expressionContext.followStructureReviewRequired()
                        || processingContext.followStructureReviewRequired()
        ) {

            reasons.add(
                    "원국에서 종격 가능성 추가 검토가 필요하여 일반 Capacity 방향을 단독 학습·성장 판정 근거로 사용하지 않습니다."
            );
        }

        /*
         * =================================================
         * Current Functional Segment
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
                            + "에 대응하는 Annual Functional Segment만 사용합니다."
            );

            reasons.add(
                    "대운 결합 후 최종 EXPRESSION 활성 상태는 "
                            + resolvedExpressionActive
                            + ", PROCESSING 활성 상태는 "
                            + resolvedProcessingActive
                            + "입니다."
            );

            reasons.add(
                    "EXPRESSION 또는 PROCESSING이 참여하는 현재 Cross Contact는 "
                            + learningContacts.size()
                            + "개이고, 그중 structurally effective Full Contact는 "
                            + effectiveLearningContacts.size()
                            + "개입니다."
            );

            for (
                    AnnualCrossFunctionalSynthesis synthesis
                    : effectiveLearningContacts
            ) {

                reasons.add(
                        "유효 LEARNING_GROWTH 관련 Cross Pattern: "
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
                                    + "의 EXACT TenGod Context Match는 같은 Functional Pattern의 Context 재확인일 뿐 별도 독립 Evidence 또는 Pattern 승격 근거로 계산하지 않습니다."
                    );
                }

                appendPatternReason(
                        reasons,
                        synthesis
                );
            }

        } else {

            reasons.add(
                    "현재 16차 Timeline에서 사용할 수 있는 정확한 대운 Functional Segment가 없어 Annual 자체 EXPRESSION / PROCESSING Capacity까지만 사용합니다."
            );
        }

        /*
         * =================================================
         * REFERENCE
         * =================================================
         */

        if (
                !referenceLearningContacts
                        .isEmpty()
        ) {

            reasons.add(
                    "EXPRESSION / PROCESSING 관련 REFERENCE Cross Pattern Contact가 "
                            + referenceLearningContacts.size()
                            + "개 존재하지만 LEARNING_GROWTH 방향 근거로 승격하지 않습니다."
            );
        }

        /*
         * =================================================
         * Context
         * =================================================
         */

        if (
                context
                        .currentMixedContext()
        ) {

            reasons.add(
                    "현재 Annual-Decade Segment의 14-6 Context에 서로 다른 상태가 함께 존재하여 학습·성장 방향을 하나로 평탄화하지 않습니다."
            );
        }

        if (
                context
                        .currentSegmentQualificationRequired()
        ) {

            reasons.add(
                    "현재 Segment의 하나 이상의 CrossFunctional Synthesis가 추가 Qualification을 요구합니다."
            );
        }

        if (
                context
                        .currentFunctionalSegmentTimingUncertain()
        ) {

            reasons.add(
                    "현재 Functional Segment에는 timing uncertainty가 존재하여 학습·성장 방향을 강하게 확정하지 않습니다."
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
                            "PROCESSING_RESTRAINS_OUTPUT 또는 원국 Capacity의 같은 방향 누적에서 표현·처리 기능의 부담 방향이 확인되어 PRESSURE로 보존합니다. 학업 실패나 성과 실패 사건 판정은 아닙니다."
                    );

            case MIXED ->
                    reasons.add(
                            "표현·처리 기능에서 조절과 억제, 또는 같은 방향 누적과 반대 방향 Contact가 함께 존재하여 MIXED로 보존합니다."
                    );

            case CONTEXTUAL ->
                    reasons.add(
                            "EXPRESSION / PROCESSING 활성 또는 구조적 접촉은 확인되지만 현재 공식 근거만으로 학습·성장 결과의 유리·불리를 확정하지 않아 CONTEXTUAL로 유지합니다."
                    );

            case SUPPORTIVE ->
                    reasons.add(
                            "현재 Rule은 EXPRESSION / PROCESSING의 단순 활성이나 조절 구조만으로 SUPPORTIVE를 생성하지 않습니다."
                    );
        }

        return List.copyOf(
                reasons
        );
    }

    /*
     * =================================================
     * Pattern Reason
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
                        .PROCESSING_REGULATES_EXPRESSION
        ) {

            reasons.add(
                    "PROCESSING_REGULATES_EXPRESSION은 처리·수용 기능이 표현·산출 기능을 조절하는 구조로 보존하며 자동 학습 호재 또는 SUPPORTIVE로 승격하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .PROCESSING_RESTRAINS_OUTPUT
        ) {

            reasons.add(
                    "PROCESSING_RESTRAINS_OUTPUT은 처리·수용 기능이 표현·산출 기능을 억제하는 구조적 Pressure Context로 보존하며 학업 실패·표현 실패 같은 사건으로 변환하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .RESPONSIBILITY_GENERATES_PROCESSING
        ) {

            reasons.add(
                    "RESPONSIBILITY_GENERATES_PROCESSING은 책임 기능이 처리·수용 기능으로 이어지는 구조적 흐름으로 보존하며 학업 성취나 자격 취득을 자동 확정하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .PRESSURE_GENERATES_PROCESSING
        ) {

            reasons.add(
                    "PRESSURE_GENERATES_PROCESSING은 압력 기능이 처리·수용 기능으로 이어지는 흐름으로 보존하며 시험 합격이나 성과 향상으로 자동 변환하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .EXPRESSION_CONFLICTS_RESPONSIBILITY
        ) {

            reasons.add(
                    "EXPRESSION_CONFLICTS_RESPONSIBILITY는 표현·실행 기능과 책임·규칙 기능의 충돌 Context로 보존하며 학습 성과의 단순 호악으로 변환하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .EXPRESSION_CONTROLS_PRESSURE
        ) {

            reasons.add(
                    "EXPRESSION_CONTROLS_PRESSURE는 표현·실행 기능이 압력 기능을 제어하는 구조로 보존하지만 자동 성과 향상이나 SUPPORTIVE로 승격하지 않습니다."
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
                    "현재 세운의 표현·산출 및 처리·수용 기능에서 원국 Capacity 또는 유효 CrossFunctional 구조에 따른 부담 방향이 확인됩니다.";

            case MIXED ->
                    "현재 세운의 표현·산출 및 처리·수용 기능에 서로 다른 구조적 작동 방향이 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 세운의 표현·산출 또는 처리·수용 기능이 활성되지만 학습·성장 결과의 방향을 단독으로 확정하지 않습니다.";

            case SUPPORTIVE ->
                    "현재 세운의 표현·산출 및 처리·수용 기능에 지지 방향이 확인됩니다.";
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
                    "LearningGrowthAnnualFunctionalRule에는 최대 하나의 "
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