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
 * HEALTH_LIFESTYLE Rule.
 *
 * =================================================
 *
 * 특정 Function 하나를:
 *
 * - 신체 장기
 * - 질병
 * - 증상
 *
 * 에 대응시키지 않는다.
 *
 * =================================================
 *
 * 다섯 Function 전체의 월간 접촉을:
 *
 * - 생활 부하
 * - 기능적 과중
 * - 조절 Context
 *
 * 로만 읽는다.
 *
 * =================================================
 *
 * 현재 Monthly Functional Layer에는
 * 별도 Monthly CrossFunctionalAnalysis가 없다.
 *
 * 따라서:
 *
 * Function 간 충돌
 * 생성
 * 조절
 *
 * 관계를 새로 만들지 않는다.
 *
 * =================================================
 *
 * SUPPORTIVE:
 *
 * 이 Rule 단독으로 생성하지 않는다.
 *
 * 반대 방향 Function 접촉을
 * 건강 호조나 회복으로 변환하지 않는다.
 */
@Component
public class HealthLifestyleMonthlyFunctionalRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .MONTHLY_FUNCTIONAL_CONTEXT
            );

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

    @Override
    public List<LifeDomainRuleMatch> evaluate(
            LifeDomainEvidenceBundle bundle
    ) {

        Objects.requireNonNull(
                bundle,
                "HealthLifestyleMonthlyFunctionalRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.HEALTH_LIFESTYLE
        ) {

            throw new IllegalArgumentException(
                    "HealthLifestyleMonthlyFunctionalRule은 HEALTH_LIFESTYLE Bundle만 평가할 수 있습니다."
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

        List<MonthlyFunctionalActivationEffect>
                effects =
                allCurrentEffects(
                        context
                );

        validateIdentity(
                context,
                effects
        );

        List<MonthlyFunctionalActivationEffect>
                contacted =
                effects
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
                        LifeDomain.HEALTH_LIFESTYLE,
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
         * =================================================
         * 실제 Annual Baseline 활성 Function만
         * 방향 후보.
         * =================================================
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
         * =================================================
         * 같은 Function 내부 월운 혼재
         * =================================================
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
         * 같은 방향 과중 Function을
         * 월운이 SUPPORTED로 지지
         * =================================================
         */

        boolean burdenReinforcement =
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
         * 명시적으로 SUPPORTED
         * =================================================
         *
         * 건강 SUPPORTIVE가 아니다.
         *
         * 부담 방향과 반대 방향이
         * 동시에 명시적으로 존재하는 경우
         * 한 방향으로 평탄화하지 않기 위한 조건.
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
                burdenReinforcement
                        && counterDirectionSupported
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                burdenReinforcement
        ) {

            return LifeDomainSignalStatus
                    .PRESSURE;
        }

        return LifeDomainSignalStatus
                .CONTEXTUAL;
    }

    private List<MonthlyFunctionalActivationEffect>
    allCurrentEffects(
            MonthlyFunctionalLifeDomainContext context
    ) {

        List<MonthlyFunctionalActivationEffect> result =
                new ArrayList<>();

        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            result.add(
                    resolveCurrentEffect(
                            context,
                            functionType
                    )
            );
        }

        return List.copyOf(
                result
        );
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

            List<MonthlyFunctionalActivationEffect>
                    effects

    ) {

        if (
                effects.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "HEALTH_LIFESTYLE Monthly Functional Rule에는 모든 Function Effect가 필요합니다. expected="
                            + NatalTenGodFunctionType.values().length
                            + ", actual="
                            + effects.size()
            );
        }

        for (
                MonthlyFunctionalActivationEffect effect
                : effects
        ) {

            Objects.requireNonNull(
                    effect,
                    "HEALTH_LIFESTYLE Monthly Functional Effect는 null일 수 없습니다."
            );

            if (
                    effect
                            .monthlyLuckAnalysis()
                            != context
                            .monthlyLuckAnalysis()
            ) {

                throw new IllegalArgumentException(
                        "HEALTH_LIFESTYLE Monthly Functional Effect가 현재 MonthlyLuckAnalysis Identity를 보존하지 않았습니다. function="
                                + effect.functionType()
                );
            }

            if (
                    effect.completeChart()
                            != context.completeChart()
            ) {

                throw new IllegalArgumentException(
                        "HEALTH_LIFESTYLE Monthly Functional Effect와 Context의 completeChart가 일치하지 않습니다. function="
                                + effect.functionType()
                );
            }
        }
    }

    private List<String> buildReasons(

            MonthlyFunctionalLifeDomainContext context,

            List<MonthlyFunctionalActivationEffect>
                    contacted,

            LifeDomainSignalStatus status

    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "HEALTH_LIFESTYLE Monthly Functional은 특정 Function 하나를 질병·장기·증상에 대응시키지 않고 다섯 Function의 월간 접촉을 생활 기능 Context로만 사용합니다."
        );

        reasons.add(
                "현재 월운은 사주연도 "
                        + context.sajuYear()
                        + "년, monthIndex="
                        + context.monthIndex()
                        + "입니다."
        );

        reasons.add(
                "실제 월운 접촉이 존재하는 Function은 "
                        + contacted.size()
                        + "개입니다. Function 개수는 건강 점수나 위험 확률로 사용하지 않습니다."
        );

        for (
                MonthlyFunctionalActivationEffect effect
                : contacted
        ) {

            reasons.add(
                    "Function "
                            + effect.functionType()
                            + " [activationStatus="
                            + effect.activationStatus()
                            + ", annualBaselineActive="
                            + effect.annualBaselineActive()
                            + ", sameDirectionAccumulation="
                            + sameDirectionAccumulation(
                            effect
                    )
                            + ", counterDirection="
                            + counterDirectionContact(
                            effect
                    )
                            + ", baselineQualification="
                            + baselineQualificationRequired(
                            effect
                    )
                            + "]"
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
                "원국 Strength와 같은 방향으로 누적되는 이미 활성된 Function을 월운이 SUPPORTED로 지지할 때만 생활 기능의 과중 지속을 PRESSURE 후보로 사용합니다."
        );

        reasons.add(
                "반대 방향 Function의 SUPPORTED 접촉을 건강 호조·회복력 증가로 자동 변환하지 않습니다."
        );

        reasons.add(
                "WEAKENED 또는 CONFLICTED가 부담 완화인지 별도 마찰인지 이 계층만으로 확정하지 않아 강한 건강 방향으로 변환하지 않습니다."
        );

        reasons.add(
                "현재 Monthly Functional Layer에는 Function 간 CrossFunctional 계산이 없으므로 서로 다른 Function의 충돌·생성·조절 관계를 새로 만들지 않습니다."
        );

        reasons.add(
                "이 Rule은 질병·병명·장기·발병·사고·입원·수술을 예측하지 않습니다."
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
                    "현재 월운이 이미 활성된 생활 기능 중 원국 동일방향 부담에 해당하는 축을 지지하여 기능적 과중이 이어지는 조건입니다.";

            case MIXED ->
                    "현재 월운의 생활 기능 접촉에 서로 다른 방향 또는 지지·제한의 혼재가 있어 생활 리듬을 한 방향으로 단순화하지 않습니다.";

            case CONTEXTUAL ->
                    "현재 월운에서 생활 기능의 접촉 변화가 존재하지만 건강 상태나 질병 결과를 단독으로 확정하지 않습니다.";

            case SUPPORTIVE ->
                    "현재 Monthly Functional Rule은 Function 접촉만으로 HEALTH_LIFESTYLE SUPPORTIVE를 생성하지 않습니다.";
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
                    "HealthLifestyleMonthlyFunctionalRule에는 최대 하나의 "
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