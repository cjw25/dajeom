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
 * MONTHLY_FUNCTIONAL_CONTEXT 기반 RELATIONSHIP Rule.
 *
 * =================================================
 *
 * 현재 Relationship Evidence에는 Gender가 없으므로
 *
 * RESOURCE_REALITY
 *
 * 또는
 *
 * RESPONSIBILITY
 *
 * 중 하나를 배우자성으로 선택하지 않는다.
 *
 * =================================================
 *
 * 두 Function을 각각 독립적인
 * 관계 기능축으로 평가한다.
 *
 * RESOURCE_REALITY:
 *
 * - 현실적 주고받음
 * - 자원
 * - 생활적 요구
 *
 * RESPONSIBILITY:
 *
 * - 역할
 * - 책임
 * - 기준
 * - 기대
 * - 통제 요구
 *
 * =================================================
 *
 * 중요한 제한:
 *
 * 두 Function이 동시에 접촉했다는 이유만으로
 * 새로운 관계·인과·사건을 합성하지 않는다.
 *
 * 현재 Monthly Functional Layer에는
 * 별도 Monthly CrossFunctionalAnalysis가 없으므로
 * Function 사이의 생성·충돌 관계도 만들지 않는다.
 */
@Component
public class RelationshipMonthlyFunctionalRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "RELATIONSHIP_MONTHLY_FUNCTIONAL";

    private static final NatalTenGodFunctionType
            RESOURCE_FUNCTION =
            NatalTenGodFunctionType
                    .RESOURCE_REALITY;

    private static final NatalTenGodFunctionType
            RESPONSIBILITY_FUNCTION =
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

        return LifeDomain.RELATIONSHIP;
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
                "RelationshipMonthlyFunctionalRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "RelationshipMonthlyFunctionalRule은 RELATIONSHIP Bundle만 평가할 수 있습니다."
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

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * 두 Function을 별도로 보존한다.
         *
         * RESOURCE + RESPONSIBILITY가 모두 존재한다고 해서
         * 별도의 합성 의미를 만들지 않는다.
         */

        appendFunctionMatch(
                matches,
                reference,
                context,
                RESOURCE_FUNCTION
        );

        appendFunctionMatch(
                matches,
                reference,
                context,
                RESPONSIBILITY_FUNCTION
        );

        return List.copyOf(
                matches
        );
    }

    private void appendFunctionMatch(

            List<LifeDomainRuleMatch> target,

            LifeDomainEvidenceReference<?> reference,

            MonthlyFunctionalLifeDomainContext context,

            NatalTenGodFunctionType functionType

    ) {

        MonthlyFunctionalActivationEffect effect =
                resolveCurrentEffect(
                        context,
                        functionType
                );

        validateIdentity(
                context,
                effect,
                functionType
        );

        if (
                !effect
                        .monthlyContactExists()
        ) {

            return;
        }

        LifeDomainSignalStatus status =
                resolveStatus(
                        context,
                        effect
                );

        target.add(
                new LifeDomainRuleMatch(
                        LifeDomain.RELATIONSHIP,
                        RULE_CODE,
                        status,
                        summaryOf(
                                functionType,
                                status
                        ),
                        List.of(
                                reference
                        ),
                        buildReasons(
                                context,
                                effect,
                                functionType,
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

        /*
         * RESOURCE_REALITY와 RESPONSIBILITY는
         * 둘 다 pressure-side Family.
         *
         * weak-side에서 이미 활성된
         * 동일 방향 부담 Function을
         * 이번 월이 SUPPORTED로 지지할 때만
         * Pressure를 유지한다.
         */

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

            MonthlyFunctionalActivationEffect effect,

            NatalTenGodFunctionType expectedFunction

    ) {

        if (
                effect
                        .functionType()
                        != expectedFunction
        ) {

            throw new IllegalArgumentException(
                    "RELATIONSHIP Monthly Functional Rule의 Function Identity가 일치하지 않습니다. expected="
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
                    "RELATIONSHIP Monthly Functional Effect가 현재 MonthlyLuckAnalysis Identity를 보존하지 않았습니다."
            );
        }

        if (
                effect.completeChart()
                        != context.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "RELATIONSHIP Monthly Functional Effect와 Context의 completeChart가 일치하지 않습니다."
            );
        }
    }

    private List<String> buildReasons(

            MonthlyFunctionalLifeDomainContext context,

            MonthlyFunctionalActivationEffect effect,

            NatalTenGodFunctionType functionType,

            LifeDomainSignalStatus status

    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "MONTHLY_FUNCTIONAL_CONTEXT에서 "
                        + functionType
                        + " Function에 실제 월운 접촉이 확인되어 관계 기능축으로 평가합니다."
        );

        reasons.add(
                "현재 월운은 사주연도 "
                        + context.sajuYear()
                        + "년, monthIndex="
                        + context.monthIndex()
                        + "입니다."
        );

        if (
                functionType
                        == RESOURCE_FUNCTION
        ) {

            reasons.add(
                    "RESOURCE_REALITY는 관계에서 현실적 주고받음·자원·생활적 요구를 다루는 기능 Context로만 사용합니다."
            );

        } else {

            reasons.add(
                    "RESPONSIBILITY는 관계에서 역할·책임·기준·기대·통제 요구를 다루는 기능 Context로만 사용합니다."
            );
        }

        reasons.add(
                "월운 ActivationStatus="
                        + effect.activationStatus()
                        + ", annualBaselineActive="
                        + effect.annualBaselineActive()
                        + ", monthlyDirectInput="
                        + effect.monthlyDirectInput()
                        + ", structuredContextExists="
                        + effect.structuredContextExists()
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
                    "Annual Baseline에서 비활성인 관계 Function에 월운이 접촉했지만 이를 Annual Function 활성 또는 관계 사건으로 승격하지 않습니다."
            );
        }

        if (
                baselineQualificationRequired(
                        effect
                )
        ) {

            reasons.add(
                    "현재 Function Baseline에 qualification 조건이 존재하므로 강한 관계 방향 판정을 제한합니다."
            );
        }

        if (
                effect
                        .supportiveMonthlyContact()
        ) {

            reasons.add(
                    "월운 Context가 현재 관계 Function의 작동을 지지하는 방향으로 접촉합니다. 이를 배우자 등장·연애 시작·결혼으로 변환하지 않습니다."
            );
        }

        if (
                effect
                        .restrictiveMonthlyContact()
        ) {

            reasons.add(
                    "월운 Context에 현재 관계 Function을 제한·약화·충돌시키는 방향이 존재합니다. 이를 이별·갈등·관계 단절 사건으로 변환하지 않습니다."
            );
        }

        reasons.add(
                "RESOURCE_REALITY와 RESPONSIBILITY가 모두 접촉하더라도 이 Rule은 두 Function 사이의 새로운 관계·인과·순서를 만들지 않습니다."
        );

        reasons.add(
                "현재 Monthly Functional Layer에는 별도 Monthly CrossFunctionalAnalysis가 없으므로 월운 Function 간 충돌이나 생성 관계를 새로 추론하지 않습니다."
        );

        reasons.add(
                "현재 18차 Evidence에는 Gender가 없으므로 재성/관살 중 하나를 배우자성으로 임의 선택하지 않습니다."
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

            NatalTenGodFunctionType functionType,

            LifeDomainSignalStatus status

    ) {

        String axis =
                functionType
                        == RESOURCE_FUNCTION

                        ? "관계의 현실적 주고받음·자원 기능"

                        : "관계의 역할·책임·기준 기능";

        return switch (
                status
                ) {

            case PRESSURE ->
                    "현재 월운이 이미 활성된 "
                            + axis
                            + "의 원국 동일방향 부담을 지지하여 관계 영역의 기능적 부담이 이어지는 조건입니다.";

            case MIXED ->
                    "현재 월운에서 "
                            + axis
                            + "에 지지와 제한 방향이 함께 접촉하여 단일 관계 방향으로 정리하지 않습니다.";

            case CONTEXTUAL ->
                    "현재 월운이 "
                            + axis
                            + "에 접촉하지만 실제 만남·연애·결혼·이별 결과를 단독으로 확정하지 않습니다.";

            case SUPPORTIVE ->
                    "현재 Monthly Functional Rule은 "
                            + axis
                            + " 접촉만으로 관계 SUPPORTIVE를 생성하지 않습니다.";
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
                    "RelationshipMonthlyFunctionalRule에는 최대 하나의 "
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