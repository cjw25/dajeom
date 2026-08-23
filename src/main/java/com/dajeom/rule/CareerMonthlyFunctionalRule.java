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
 * MONTHLY_FUNCTIONAL_CONTEXT 기반 CAREER Rule.
 *
 * RESPONSIBILITY Function에 대한 월운 접촉을
 * 고정된 Annual Functional Baseline 위에서만 해석한다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * 관성 월운
 * -> 취업
 *
 * SUPPORTED
 * -> 승진
 *
 * WEAKENED
 * -> 퇴사
 *
 * 같은 사건 변환.
 *
 * =================================================
 *
 * 현재 Monthly Functional Layer에는
 * 별도 Monthly CrossFunctionalAnalysis가 없다.
 *
 * 따라서:
 *
 * EXPRESSION_CONFLICTS_RESPONSIBILITY
 *
 * 같은 월간 상호작용을
 * 이 Rule에서 새로 만들지 않는다.
 *
 * =================================================
 *
 * SUPPORTIVE:
 *
 * 이 Rule 단독으로 생성하지 않는다.
 */
@Component
public class CareerMonthlyFunctionalRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "CAREER_MONTHLY_FUNCTIONAL";

    private static final NatalTenGodFunctionType
            CAREER_FUNCTION =
            NatalTenGodFunctionType
                    .RESPONSIBILITY;

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .MONTHLY_FUNCTIONAL_CONTEXT
            );

    @Override
    public LifeDomain domain() {

        return LifeDomain.CAREER;
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
                "CareerMonthlyFunctionalRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerMonthlyFunctionalRule은 CAREER Bundle만 평가할 수 있습니다."
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

        MonthlyFunctionalActivationEffect effect =
                resolveCurrentEffect(
                        context,
                        CAREER_FUNCTION
                );

        validateIdentity(
                context,
                effect
        );

        if (
                !effect
                        .monthlyContactExists()
        ) {

            return List.of();
        }

        LifeDomainSignalStatus status =
                resolveStatus(
                        context,
                        effect
                );

        return List.of(
                new LifeDomainRuleMatch(
                        LifeDomain.CAREER,
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
                                effect,
                                status
                        )
                )
        );
    }

    private LifeDomainSignalStatus resolveStatus(

            MonthlyFunctionalLifeDomainContext context,

            MonthlyFunctionalActivationEffect effect

    ) {

        if (
                !context
                        .completeChart()
        ) {

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        if (
                effect.segmentScoped()

                        && context
                        .currentActivationSegmentTimingUncertain()
        ) {

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        if (
                !effect
                        .annualBaselineActive()
        ) {

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        if (
                effect
                        .mixedMonthlyContact()
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                sameDirectionAccumulation(
                        effect
                )

                        && effect
                        .activationStatus()
                        == MonthlyFunctionalActivationStatus
                        .SUPPORTED
        ) {

            if (
                    baselineQualificationRequired(
                            effect
                    )
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

            MonthlyFunctionalActivationEffect effect

    ) {

        if (
                effect
                        .functionType()
                        != CAREER_FUNCTION
        ) {

            throw new IllegalArgumentException(
                    "CAREER Monthly Functional Rule에 RESPONSIBILITY가 아닌 Function이 전달되었습니다: "
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
                    "CAREER Monthly Functional Effect가 현재 MonthlyLuckAnalysis Identity를 보존하지 않았습니다."
            );
        }

        if (
                effect.completeChart()
                        != context.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "CAREER Monthly Functional Effect와 Context의 completeChart가 일치하지 않습니다."
            );
        }
    }

    private List<String> buildReasons(

            MonthlyFunctionalLifeDomainContext context,

            MonthlyFunctionalActivationEffect effect,

            LifeDomainSignalStatus status

    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "MONTHLY_FUNCTIONAL_CONTEXT에서 RESPONSIBILITY Function에 실제 월운 접촉이 확인된 경우만 평가합니다."
        );

        reasons.add(
                "현재 월운은 사주연도 "
                        + context.sajuYear()
                        + "년, monthIndex="
                        + context.monthIndex()
                        + "입니다."
        );

        reasons.add(
                "RESPONSIBILITY의 월운 ActivationStatus는 "
                        + effect.activationStatus()
                        + "이고, Annual Baseline 활성 상태는 "
                        + effect.annualBaselineActive()
                        + "입니다."
        );

        reasons.add(
                "monthlyDirectInput="
                        + effect.monthlyDirectInput()
                        + ", structuredContextExists="
                        + effect.structuredContextExists()
                        + ", sourceSignalCount="
                        + effect.sourceSignals().size()
                        + "입니다."
        );

        reasons.add(
                "Annual/현재 Segment Baseline의 같은 방향 누적 부담 여부는 "
                        + sameDirectionAccumulation(
                        effect
                )
                        + ", 반대 방향 접촉 여부는 "
                        + counterDirectionContact(
                        effect
                )
                        + "입니다."
        );

        if (
                effect
                        .monthlyContactAgainstInactiveAnnualBaseline()
        ) {

            reasons.add(
                    "Annual Baseline에서 비활성인 RESPONSIBILITY에 월운이 접촉했지만 이를 Annual 책임 기능 활성으로 승격하지 않습니다."
            );
        }

        if (
                baselineQualificationRequired(
                        effect
                )
        ) {

            reasons.add(
                    "현재 RESPONSIBILITY Baseline에 qualification 조건이 존재하므로 강한 직업 방향 판정을 제한합니다."
            );
        }

        if (
                effect
                        .supportiveMonthlyContact()
        ) {

            reasons.add(
                    "월운 Context가 RESPONSIBILITY Function의 작동을 지지하는 방향으로 접촉합니다. 이를 취업·승진·직업 안정으로 변환하지 않습니다."
            );
        }

        if (
                effect
                        .restrictiveMonthlyContact()
        ) {

            reasons.add(
                    "월운 Context에 RESPONSIBILITY Function을 제한·약화·충돌시키는 방향이 존재합니다. 이를 해고·퇴사·직장 갈등 사건으로 변환하지 않습니다."
            );
        }

        reasons.add(
                "현재 Monthly Functional Layer에는 Function 간 CrossFunctional 판정이 없으므로 표현 기능과 책임 기능의 충돌 같은 관계를 이 Rule에서 새로 만들지 않습니다."
        );

        reasons.add(
                "SUPPORTIVE는 이 Rule 단독으로 생성하지 않으며 반대 방향 Function 접촉을 직업 호재로 자동 변환하지 않습니다."
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

    private String summaryOf(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case PRESSURE ->
                    "현재 월운이 이미 활성된 책임·역할 기능의 원국 동일방향 부담을 지지하여 직업·사회 역할의 기능적 부담이 이어지는 조건입니다.";

            case MIXED ->
                    "현재 월운에서 책임·역할 기능에 지지와 제한 방향이 함께 접촉하여 단일 방향으로 정리하지 않습니다.";

            case CONTEXTUAL ->
                    "현재 월운이 책임·역할 기능에 접촉하지만 취업·승진·이직 같은 실제 직업 결과를 단독으로 확정하지 않습니다.";

            case SUPPORTIVE ->
                    "현재 Monthly Functional Rule은 RESPONSIBILITY 접촉만으로 직업 SUPPORTIVE를 생성하지 않습니다.";
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
                    "CareerMonthlyFunctionalRule에는 최대 하나의 "
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