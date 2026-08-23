package com.dajeom.rule;

import com.dajeom.model.AnnualResolvedFunctionEffect;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.MonthlyFunctionalActivationEffect;
import com.dajeom.model.MonthlyFunctionalLifeDomainContext;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import com.dajeom.type.MonthlyFunctionalActivationStatus;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * MONTHLY_FUNCTIONAL_CONTEXT 기반
 * LEARNING_GROWTH Rule.
 *
 * =================================================
 *
 * Annual Functional과 동일하게:
 *
 * EXPRESSION
 * PROCESSING
 *
 * 두 축을 사용한다.
 *
 * =================================================
 *
 * EXPRESSION:
 *
 * - 표현
 * - 산출
 * - 실행
 *
 * PROCESSING:
 *
 * - 수용
 * - 정리
 * - 처리
 * - 학습 기반
 *
 * =================================================
 *
 * 현재 Monthly Functional Layer에는
 * 별도 Monthly CrossFunctionalAnalysis가 없다.
 *
 * 따라서:
 *
 * PROCESSING_REGULATES_EXPRESSION
 *
 * PROCESSING_RESTRAINS_OUTPUT
 *
 * 같은 Function 간 관계를
 * 이 Rule에서 새로 만들지 않는다.
 *
 * =================================================
 *
 * 이 Rule은 월운이 고정 Annual Baseline의
 * 각 Function을:
 *
 * - 지지
 * - 제한
 * - 조건부 접촉
 *
 * 하는지만 읽는다.
 *
 * =================================================
 *
 * SUPPORTIVE:
 *
 * 이 Rule 단독으로 생성하지 않는다.
 */
@Component
public class LearningGrowthMonthlyFunctionalRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "LEARNING_GROWTH_MONTHLY_FUNCTIONAL";

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
                            .MONTHLY_FUNCTIONAL_CONTEXT
            );

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

    @Override
    public List<LifeDomainRuleMatch> evaluate(
            LifeDomainEvidenceBundle bundle
    ) {

        Objects.requireNonNull(
                bundle,
                "LearningGrowthMonthlyFunctionalRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.LEARNING_GROWTH
        ) {

            throw new IllegalArgumentException(
                    "LearningGrowthMonthlyFunctionalRule은 LEARNING_GROWTH Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                referenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .MONTHLY_FUNCTIONAL_CONTEXT
                );

        if (
                referenceOptional.isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?> reference =
                referenceOptional
                        .orElseThrow();

        MonthlyFunctionalLifeDomainContext context =
                reference
                        .requireSource(
                                MonthlyFunctionalLifeDomainContext.class
                        );

        MonthlyFunctionalActivationEffect expression =
                resolveCurrentEffect(
                        context,
                        EXPRESSION_FUNCTION
                );

        MonthlyFunctionalActivationEffect processing =
                resolveCurrentEffect(
                        context,
                        PROCESSING_FUNCTION
                );

        validateIdentity(
                context,
                expression,
                EXPRESSION_FUNCTION
        );

        validateIdentity(
                context,
                processing,
                PROCESSING_FUNCTION
        );

        List<MonthlyFunctionalActivationEffect>
                contacted =
                List.of(
                                expression,
                                processing
                        )
                        .stream()
                        .filter(
                                MonthlyFunctionalActivationEffect
                                        ::monthlyContactExists
                        )
                        .toList();

        if (
                contacted.isEmpty()
        ) {

            return List.of();
        }

        LifeDomainSignalStatus status =
                resolveStatus(
                        context,
                        contacted
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
                        buildReasons(
                                context,
                                expression,
                                processing,
                                contacted,
                                status
                        )
                )
        );
    }

    private LifeDomainSignalStatus resolveStatus(

            MonthlyFunctionalLifeDomainContext context,

            List<MonthlyFunctionalActivationEffect>
                    contacted

    ) {

        if (
                !context
                        .completeChart()
        ) {

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        if (
                context
                        .currentActivationSegmentAvailable()

                        && context
                        .currentActivationSegmentTimingUncertain()
        ) {

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        /*
         * Annual Baseline에서 실제 활성된 Function만
         * 방향 후보로 사용한다.
         *
         * 비활성 Function에 월운이 접촉해도
         * Annual 활성으로 승격하지 않는다.
         */

        List<MonthlyFunctionalActivationEffect>
                activeBaselineContacts =
                contacted
                        .stream()
                        .filter(
                                MonthlyFunctionalActivationEffect
                                        ::annualBaselineActive
                        )
                        .toList();

        if (
                activeBaselineContacts
                        .isEmpty()
        ) {

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        /*
         * 같은 Function 안에
         * 지지 / 제한이 명시적으로 공존.
         */

        boolean mixedMonthlyFunction =
                activeBaselineContacts
                        .stream()
                        .anyMatch(
                                MonthlyFunctionalActivationEffect
                                        ::mixedMonthlyContact
                        );

        if (
                mixedMonthlyFunction
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        /*
         * =================================================
         * 동일 방향 부담 유지
         * =================================================
         */

        boolean pressureReinforcement =
                activeBaselineContacts
                        .stream()
                        .anyMatch(
                                effect ->
                                        sameDirectionAccumulation(
                                                effect
                                        )

                                                && effect
                                                .activationStatus()
                                                == MonthlyFunctionalActivationStatus
                                                .SUPPORTED

                                                && !baselineQualificationRequired(
                                                effect
                                        )
                        );

        /*
         * =================================================
         * 반대 방향 Function도
         * 같은 월에 실제 SUPPORTED
         * =================================================
         *
         * 이것을 SUPPORTIVE로 변환하지 않는다.
         *
         * 부담 방향과 반대 방향이
         * 동시에 명시적으로 지지되는 경우에만
         * MIXED로 보존한다.
         */

        boolean counterDirectionSupported =
                activeBaselineContacts
                        .stream()
                        .anyMatch(
                                effect ->
                                        counterDirectionContact(
                                                effect
                                        )

                                                && effect
                                                .activationStatus()
                                                == MonthlyFunctionalActivationStatus
                                                .SUPPORTED

                                                && !baselineQualificationRequired(
                                                effect
                                        )
                        );

        if (
                pressureReinforcement
                        && counterDirectionSupported
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                pressureReinforcement
        ) {

            return LifeDomainSignalStatus
                    .PRESSURE;
        }

        return LifeDomainSignalStatus
                .CONTEXTUAL;
    }

    private MonthlyFunctionalActivationEffect
    resolveCurrentEffect(

            MonthlyFunctionalLifeDomainContext context,

            NatalTenGodFunctionType functionType

    ) {

        return context
                .currentEffectOf(
                        functionType
                )
                .orElseGet(
                        () ->
                                context
                                        .requireAnnualEffect(
                                                functionType
                                        )
                );
    }

    private boolean sameDirectionAccumulation(
            MonthlyFunctionalActivationEffect effect
    ) {

        if (
                !effect
                        .annualBaselineActive()
        ) {

            return false;
        }

        return effect
                .segmentResolvedEffectOptional()
                .map(
                        AnnualResolvedFunctionEffect
                                ::sameDirectionAccumulationReviewRequired
                )
                .orElseGet(
                        () ->
                                effect
                                        .annualFunctionContext()
                                        .sameDirectionAccumulationContact()
                );
    }

    private boolean counterDirectionContact(
            MonthlyFunctionalActivationEffect effect
    ) {

        if (
                !effect
                        .annualBaselineActive()
        ) {

            return false;
        }

        return effect
                .segmentResolvedEffectOptional()
                .map(
                        AnnualResolvedFunctionEffect
                                ::counterDirectionContact
                )
                .orElseGet(
                        () ->
                                effect
                                        .annualFunctionContext()
                                        .counterDirectionContact()
                );
    }

    private boolean baselineQualificationRequired(
            MonthlyFunctionalActivationEffect effect
    ) {

        if (
                !effect
                        .annualBaselineActive()
        ) {

            return false;
        }

        return effect
                .segmentResolvedEffectOptional()
                .map(
                        AnnualResolvedFunctionEffect
                                ::qualificationRequired
                )
                .orElseGet(
                        () ->
                                effect
                                        .annualFunctionContext()
                                        .qualificationRequired()
                );
    }

    private void validateIdentity(

            MonthlyFunctionalLifeDomainContext context,

            MonthlyFunctionalActivationEffect effect,

            NatalTenGodFunctionType expectedFunction

    ) {

        if (
                effect
                        .functionType()
                        != expectedFunction
        ) {

            throw new IllegalArgumentException(
                    "LEARNING_GROWTH Monthly Functional Rule의 Function Identity가 일치하지 않습니다. expected="
                            + expectedFunction
                            + ", actual="
                            + effect.functionType()
            );
        }

        if (
                effect
                        .monthlyLuckAnalysis()
                        != context
                        .monthlyLuckAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "LEARNING_GROWTH Monthly Functional Effect가 현재 MonthlyLuckAnalysis Identity를 보존하지 않았습니다."
            );
        }

        if (
                effect.completeChart()
                        != context.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "LEARNING_GROWTH Monthly Functional Effect와 Context의 completeChart가 일치하지 않습니다."
            );
        }
    }

    private List<String> buildReasons(

            MonthlyFunctionalLifeDomainContext context,

            MonthlyFunctionalActivationEffect expression,

            MonthlyFunctionalActivationEffect processing,

            List<MonthlyFunctionalActivationEffect>
                    contacted,

            LifeDomainSignalStatus status

    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "LEARNING_GROWTH Monthly Functional은 EXPRESSION과 PROCESSING 두 Function을 같은 MONTHLY_FUNCTIONAL_CONTEXT 안에서 읽습니다."
        );

        reasons.add(
                "현재 월운은 사주연도 "
                        + context.sajuYear()
                        + "년, monthIndex="
                        + context.monthIndex()
                        + "입니다."
        );

        reasons.add(
                "EXPRESSION은 표현·산출·실행 기능, PROCESSING은 수용·정리·처리·학습 기반 기능으로만 사용합니다."
        );

        reasons.add(
                describeEffect(
                        "EXPRESSION",
                        expression
                )
        );

        reasons.add(
                describeEffect(
                        "PROCESSING",
                        processing
                )
        );

        reasons.add(
                "실제 월운 접촉이 존재하는 학습·성장 Function은 "
                        + contacted.size()
                        + "개입니다. 개수 자체를 강도 점수로 사용하지 않습니다."
        );

        for (
                MonthlyFunctionalActivationEffect effect
                : contacted
        ) {

            reasons.add(
                    effect.functionType()
                            + "의 Annual/현재 Segment 같은 방향 누적 부담 여부="
                            + sameDirectionAccumulation(
                            effect
                    )
                            + ", 반대 방향 접촉 여부="
                            + counterDirectionContact(
                            effect
                    )
                            + ", baselineQualification="
                            + baselineQualificationRequired(
                            effect
                    )
                            + "입니다."
            );

            if (
                    effect
                            .monthlyContactAgainstInactiveAnnualBaseline()
            ) {

                reasons.add(
                        effect.functionType()
                                + "는 Annual Baseline에서 비활성인데 월운이 접촉한 경우이므로 Annual Function 활성으로 승격하지 않습니다."
                );
            }
        }

        reasons.add(
                "same-direction 부담 Function을 월운이 SUPPORTED로 지지할 때만 학습·성장 영역의 기능적 부담 지속을 PRESSURE 후보로 사용합니다."
        );

        reasons.add(
                "반대 방향 Function의 SUPPORTED 접촉을 학업 호조나 시험 합격으로 자동 변환하지 않습니다."
        );

        reasons.add(
                "WEAKENED 또는 CONFLICTED가 부담 완화인지 추가 마찰인지 이 계층만으로 확정하지 않아 강한 방향으로 변환하지 않습니다."
        );

        reasons.add(
                "현재 Monthly Functional Layer에는 Function 간 CrossFunctional 계산이 없으므로 PROCESSING이 EXPRESSION을 조절·억제한다는 관계를 새로 만들지 않습니다."
        );

        reasons.add(
                "학습·표현 Function 접촉을 시험 합격·자격 취득·성과 성공 같은 실제 사건으로 변환하지 않습니다."
        );

        reasons.add(
                "최종 LifeDomainSignalStatus는 "
                        + status
                        + "입니다."
        );

        return List.copyOf(
                reasons
        );
    }

    private String describeEffect(

            String label,

            MonthlyFunctionalActivationEffect effect

    ) {

        return label
                + " 월운 ActivationStatus="
                + effect.activationStatus()
                + ", annualBaselineActive="
                + effect.annualBaselineActive()
                + ", monthlyDirectInput="
                + effect.monthlyDirectInput()
                + ", structuredContextExists="
                + effect.structuredContextExists()
                + ", sourceSignalCount="
                + effect.sourceSignals().size()
                + "입니다.";
    }

    private String summaryOf(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case PRESSURE ->
                    "현재 월운이 이미 활성된 학습·표현 기능 중 원국 동일방향 부담에 해당하는 축을 지지하여 기능적 부담이 이어지는 조건입니다.";

            case MIXED ->
                    "현재 월운의 학습·표현 기능 접촉에 서로 다른 방향 또는 지지·제한의 혼재가 있어 한 방향으로 단순화하지 않습니다.";

            case CONTEXTUAL ->
                    "현재 월운이 학습·표현 기능에 접촉하지만 실제 공부 성과·시험 결과·자격 취득을 단독으로 확정하지 않습니다.";

            case SUPPORTIVE ->
                    "현재 Monthly Functional Rule은 학습·표현 Function 접촉만으로 LEARNING_GROWTH SUPPORTIVE를 생성하지 않습니다.";
        };
    }

    private Optional<LifeDomainEvidenceReference<?>>
    optionalSingleEvidence(

            LifeDomainEvidenceBundle bundle,

            LifeDomainEvidenceSourceType sourceType

    ) {

        List<LifeDomainEvidenceReference<?>> evidences =
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
                    "LearningGrowthMonthlyFunctionalRule에는 최대 하나의 "
                            + sourceType
                            + " Evidence가 허용됩니다. actual="
                            + evidences.size()
            );
        }

        return Optional.of(
                evidences.get(0)
        );
    }
}