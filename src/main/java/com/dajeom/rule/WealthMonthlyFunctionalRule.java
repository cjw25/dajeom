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
 * MONTHLY_FUNCTIONAL_CONTEXT 기반 WEALTH Rule.
 *
 * 월운은 Annual Functional Baseline을
 * 다시 계산하거나 변경하지 않는다.
 *
 * RESOURCE_REALITY에 실제 월운 접촉이 있을 때만
 * 현재 월의 기능적 조절을 읽는다.
 *
 * =================================================
 *
 * SUPPORTIVE
 *
 * 이 Rule 단독으로는 생성하지 않는다.
 *
 * Annual Baseline의 반대 방향 접촉이 존재하더라도
 * 그것을 재물 호조로 바꾸지 않는다.
 *
 * =================================================
 *
 * PRESSURE
 *
 * 다음이 모두 성립할 때만 생성한다.
 *
 * - RESOURCE_REALITY가 Annual / 현재 Segment
 *   Baseline에서 실제 활성
 *
 * - 그 활성 Function이 원국 Strength의
 *   같은 방향 누적 부담에 해당
 *
 * - 이번 월운 ActivationStatus가 SUPPORTED
 *
 * - 해당 Baseline에 추가 qualification이 없음
 *
 * =================================================
 *
 * WEAKENED / CONFLICTED
 *
 * 부담 완화인지 추가 마찰인지
 * 이 계층만으로 확정하지 않는다.
 *
 * 따라서 CONTEXTUAL로 제한한다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * RESOURCE_REALITY 월운 접촉
 * -> 돈이 들어온다
 *
 * SUPPORTED
 * -> 매출이 오른다
 *
 * WEAKENED
 * -> 손실이 난다
 *
 * 같은 사건 추론.
 */
@Component
public class WealthMonthlyFunctionalRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "WEALTH_MONTHLY_FUNCTIONAL";

    private static final NatalTenGodFunctionType
            WEALTH_FUNCTION =
            NatalTenGodFunctionType
                    .RESOURCE_REALITY;

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .MONTHLY_FUNCTIONAL_CONTEXT
            );

    @Override
    public LifeDomain domain() {

        return LifeDomain.WEALTH;
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
                "WealthMonthlyFunctionalRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthMonthlyFunctionalRule은 WEALTH Bundle만 평가할 수 있습니다."
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
                        WEALTH_FUNCTION
                );

        validateIdentity(
                context,
                effect
        );

        /*
         * =================================================
         * 실제 월운 접촉이 없으면
         * 파생 Match를 만들지 않는다.
         * =================================================
         */

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

        List<String> reasons =
                buildReasons(
                        context,
                        effect,
                        status
                );

        return List.of(
                new LifeDomainRuleMatch(
                        LifeDomain.WEALTH,
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

    private LifeDomainSignalStatus resolveStatus(

            MonthlyFunctionalLifeDomainContext context,

            MonthlyFunctionalActivationEffect effect

    ) {

        /*
         * =================================================
         * 원국 불완전
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
         * 현재 실제 대운 Segment 경계 불확실
         * =================================================
         */

        if (
                effect.segmentScoped()

                        && context
                        .currentActivationSegmentTimingUncertain()
        ) {

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        /*
         * =================================================
         * Annual Baseline 비활성
         * =================================================
         *
         * 월운 접촉만으로 Annual Function을
         * 활성 상태로 승격하지 않는다.
         */

        if (
                !effect
                        .annualBaselineActive()
        ) {

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        boolean sameDirectionBurden =
                sameDirectionAccumulation(
                        effect
                );

        /*
         * =================================================
         * 같은 Function 안에서
         * 지지 + 제한이 실제 공존.
         * =================================================
         */

        if (
                effect
                        .mixedMonthlyContact()
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        /*
         * =================================================
         * 기존 부담 방향 Function을
         * 월운이 명시적으로 지지
         * =================================================
         */

        if (
                sameDirectionBurden

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

        /*
         * 반대 방향 Baseline을 지지해도
         * 자동 SUPPORTIVE 아님.
         *
         * WEAKENED / CONFLICTED도
         * 부담 완화인지 추가 마찰인지
         * 여기서는 확정하지 않는다.
         */

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
                        != WEALTH_FUNCTION
        ) {

            throw new IllegalArgumentException(
                    "WEALTH Monthly Functional Rule에 RESOURCE_REALITY가 아닌 Function이 전달되었습니다: "
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
                    "WEALTH Monthly Functional Effect가 현재 MonthlyLuckAnalysis Identity를 보존하지 않았습니다."
            );
        }

        if (
                effect.completeChart()
                        != context.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "WEALTH Monthly Functional Effect와 Context의 completeChart가 일치하지 않습니다."
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
                "MONTHLY_FUNCTIONAL_CONTEXT에서 RESOURCE_REALITY Function에 실제 월운 접촉이 확인된 경우만 평가합니다."
        );

        reasons.add(
                "현재 월운은 사주연도 "
                        + context.sajuYear()
                        + "년, monthIndex="
                        + context.monthIndex()
                        + "입니다."
        );

        reasons.add(
                "RESOURCE_REALITY의 월운 ActivationStatus는 "
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
                    "Annual Baseline에서 비활성인 RESOURCE_REALITY에 월운이 접촉했지만 이를 Annual Function 활성으로 승격하지 않습니다."
            );
        }

        if (
                baselineQualificationRequired(
                        effect
                )
        ) {

            reasons.add(
                    "현재 RESOURCE_REALITY Baseline에 qualification 조건이 존재하므로 강한 방향 판정을 제한합니다."
            );
        }

        if (
                effect
                        .supportiveMonthlyContact()
        ) {

            reasons.add(
                    "월운 Context가 RESOURCE_REALITY Function의 작동을 지지하는 방향으로 접촉합니다. 이것을 수입·매출·재산 증가로 변환하지 않습니다."
            );
        }

        if (
                effect
                        .restrictiveMonthlyContact()
        ) {

            reasons.add(
                    "월운 Context에 RESOURCE_REALITY Function을 제한·약화·충돌시키는 방향이 존재합니다. 이것을 금전 손실 사건으로 변환하지 않습니다."
            );
        }

        reasons.add(
                "WEAKENED 또는 CONFLICTED가 같은 방향 누적 부담을 줄이는지 별도 마찰을 만드는지는 이 Function Activation 계층만으로 확정하지 않아 CONTEXTUAL로 제한합니다."
        );

        reasons.add(
                "이 Rule은 Monthly Functional Activation에서 계산되지 않은 Function 간 관계나 사건을 새로 만들지 않습니다."
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
                    "현재 월운이 이미 활성된 현실 자원 기능의 원국 동일방향 부담을 지지하여 재물 영역의 기능적 부담이 이어지는 조건입니다.";

            case MIXED ->
                    "현재 월운에서 현실 자원 기능에 지지와 제한 방향이 함께 접촉하여 단일 방향으로 정리하지 않습니다.";

            case CONTEXTUAL ->
                    "현재 월운이 현실 자원 기능에 접촉하지만 재물의 유불리나 실제 금전 결과를 단독으로 확정하지 않습니다.";

            case SUPPORTIVE ->
                    "현재 Monthly Functional Rule은 RESOURCE_REALITY 접촉만으로 재물 SUPPORTIVE를 생성하지 않습니다.";
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
                    "WealthMonthlyFunctionalRule에는 최대 하나의 "
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