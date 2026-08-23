package com.dajeom.rule;

import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.MonthlyLuckContextAnalysis;
import com.dajeom.model.MonthlyLuckContextSignal;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 직업·사회 영역의 월운 Context Rule.
 *
 * 반드시 MonthlyLuckContextAnalysis.signalsOf(...)
 * API가 반환하는 monthlySignals만 사용한다.
 *
 * DIRECT_OFFICER
 * SEVEN_KILLINGS
 *
 * 관련 MonthlyLuckContextSignal을 선택한다.
 *
 * =================================================
 *
 * Signal 1개
 * ->
 * LifeDomainRuleMatch 1개
 *
 * 로 독립 보존한다.
 *
 * 서로 다른 Signal 또는 Axis를 합쳐
 * 새로운 상태나 의미를 만들지 않는다.
 *
 * 실제:
 *
 * - 원국 × 세운 × 월운 관계
 * - 대운 × 월운 관계
 * - 합화
 * - 다층 관계
 *
 * 등이 upstream에서 계산되어
 * 하나의 MonthlyLuckContextSignal로 존재한다면
 * 해당 관계 계산은 그대로 유지한다.
 */
@Component
public class CareerMonthlyContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "CAREER_MONTHLY_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .MONTHLY_CONTEXT
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
                "CareerMonthlyContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerMonthlyContextRule은 CAREER Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                monthlyReferenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .MONTHLY_CONTEXT
                );

        if (
                monthlyReferenceOptional
                        .isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?>
                monthlyReference =
                monthlyReferenceOptional
                        .orElseThrow();

        MonthlyLuckContextAnalysis monthlyContext =
                monthlyReference
                        .requireSource(
                                MonthlyLuckContextAnalysis.class
                        );

        List<MonthlyLuckContextSignal>
                directOfficerSignals =
                monthlyContext
                        .signalsOf(
                                StructureTenGodType
                                        .DIRECT_OFFICER
                        );

        List<MonthlyLuckContextSignal>
                sevenKillingsSignals =
                monthlyContext
                        .signalsOf(
                                StructureTenGodType
                                        .SEVEN_KILLINGS
                        );

        List<MonthlyLuckContextSignal>
                officerSignals =
                mergeIdentityUnique(
                        directOfficerSignals,
                        sevenKillingsSignals
                );

        if (
                officerSignals
                        .isEmpty()
        ) {

            return List.of();
        }

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        for (
                MonthlyLuckContextSignal signal
                : officerSignals
        ) {

            Objects.requireNonNull(
                    signal,
                    "MonthlyLuckContextAnalysis의 Monthly Signal은 null일 수 없습니다."
            );

            LifeDomainSignalStatus status =
                    resolveStatus(
                            signal
                    );

            List<String> reasons =
                    baseReasons(
                            monthlyContext
                    );

            reasons.add(
                    "Monthly Signal ["
                            + signal.code()
                            + "] "
                            + signal.summary()
                            + " / axis="
                            + signal.axis().name()
                            + " / status="
                            + signal.status().name()
            );

            reasons.add(
                    "이 Signal에 실제 태깅된 직업·사회 참조 관살 유형은 "
                            + matchedOfficerTypes(
                            signal
                    )
                            + "입니다."
            );

            for (
                    String signalReason
                    : signal.reasons()
            ) {

                reasons.add(
                        "Monthly Signal ["
                                + signal.code()
                                + "] 근거: "
                                + signalReason
                );
            }

            reasons.add(
                    "이 Match는 하나의 MonthlyLuckContextSignal만 보존하며 다른 Monthly Signal 또는 Axis와 상태를 합산하지 않습니다."
            );

            reasons.add(
                    "15차가 이미 계산한 십성·용신·격국·관계·합화·다층 관계를 이 Rule에서 다시 조합하지 않습니다."
            );

            reasons.add(
                    "정관·칠살 관련 월운 Context를 취업·승진·퇴사·이직·직위 획득·직장 갈등 같은 실제 사건으로 직접 환산하지 않습니다."
            );

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.CAREER,
                            RULE_CODE,
                            status,
                            summaryOf(
                                    signal
                            ),
                            List.of(
                                    monthlyReference
                            ),
                            List.copyOf(
                                    reasons
                            )
                    )
            );
        }

        return List.copyOf(
                matches
        );
    }

    private List<String> baseReasons(
            MonthlyLuckContextAnalysis monthlyContext
    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "15-7 월운 Context의 사주 연도는 "
                        + monthlyContext.sajuYear()
                        + "년입니다."
        );

        reasons.add(
                "현재 월운 monthOrder는 "
                        + monthlyContext.monthOrder()
                        + ", monthIndex는 "
                        + monthlyContext.monthIndex()
                        + "입니다."
        );

        reasons.add(
                "현재 월운 간지는 "
                        + monthlyContext.monthlyPillar()
                        + "입니다."
        );

        reasons.add(
                "상위 세운 간지는 "
                        + monthlyContext.annualPillar()
                        + "입니다."
        );

        if (
                !monthlyContext
                        .decadeContextAvailable()
        ) {

            reasons.add(
                    "현재 월운에서는 대운 Segment Context를 사용할 수 없지만 대운 Sequence와 무관하게 확정 가능한 monthlySignals는 그대로 보존합니다."
            );

            if (
                    monthlyContext
                            .decadeContextUnavailableReason()
                            != null

                            && !monthlyContext
                            .decadeContextUnavailableReason()
                            .isBlank()
            ) {

                reasons.add(
                        "월운의 대운 Context 사용 불가 사유: "
                                + monthlyContext
                                .decadeContextUnavailableReason()
                );
            }
        }

        if (
                monthlyContext
                        .decadeTimingUncertain()
        ) {

            reasons.add(
                    "현재 월운 안에 대운 교운시점 불확실성이 존재하지만 가능한 Monthly-Decade Context 중 하나를 이 Rule에서 임의 선택하지 않습니다."
            );
        }

        reasons.add(
                "이 Rule은 MonthlyLuckContextAnalysis.monthlySignals만 사용하고 decadeContexts.segmentSignals를 포함하는 allSignals()는 사용하지 않습니다."
        );

        reasons.add(
                "활성 대운의 직업·사회 Context는 별도 DECADE_CONTEXT Rule이 담당하므로 월운 Rule에서 중복 집계하지 않습니다."
        );

        return reasons;
    }

    private LifeDomainSignalStatus resolveStatus(
            MonthlyLuckContextSignal signal
    ) {

        if (
                signal
                        .conflicted()
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                signal
                        .weakened()
        ) {

            return LifeDomainSignalStatus
                    .PRESSURE;
        }

        if (
                signal
                        .supported()
        ) {

            return LifeDomainSignalStatus
                    .SUPPORTIVE;
        }

        if (
                signal
                        .qualified()
        ) {

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        throw new IllegalStateException(
                "지원하지 않는 MonthlyLuckContextSignal 상태입니다: "
                        + signal.status()
        );
    }

    private String summaryOf(
            MonthlyLuckContextSignal signal
    ) {

        return "현재 월운의 직업·사회 관련 Monthly Context Signal ["
                + signal.code()
                + "]을 독립적으로 보존합니다: "
                + signal.summary();
    }

    private List<StructureTenGodType>
    matchedOfficerTypes(
            MonthlyLuckContextSignal signal
    ) {

        List<StructureTenGodType> types =
                new ArrayList<>();

        if (
                signal.involves(
                        StructureTenGodType
                                .DIRECT_OFFICER
                )
        ) {

            types.add(
                    StructureTenGodType
                            .DIRECT_OFFICER
            );
        }

        if (
                signal.involves(
                        StructureTenGodType
                                .SEVEN_KILLINGS
                )
        ) {

            types.add(
                    StructureTenGodType
                            .SEVEN_KILLINGS
            );
        }

        return List.copyOf(
                types
        );
    }

    @SafeVarargs
    private final List<MonthlyLuckContextSignal>
    mergeIdentityUnique(
            List<MonthlyLuckContextSignal>... signalLists
    ) {

        Set<MonthlyLuckContextSignal> unique =
                Collections
                        .newSetFromMap(
                                new IdentityHashMap<>()
                        );

        List<MonthlyLuckContextSignal> ordered =
                new ArrayList<>();

        for (
                List<MonthlyLuckContextSignal> signals
                : signalLists
        ) {

            if (
                    signals == null
            ) {

                continue;
            }

            for (
                    MonthlyLuckContextSignal signal
                    : signals
            ) {

                if (
                        unique.add(
                                signal
                        )
                ) {

                    ordered.add(
                            signal
                    );
                }
            }
        }

        return List.copyOf(
                ordered
        );
    }

    private Optional<LifeDomainEvidenceReference<?>>
    optionalSingleEvidence(

            LifeDomainEvidenceBundle bundle,

            LifeDomainEvidenceSourceType sourceType

    ) {

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
                    "CareerMonthlyContextRule에는 최대 하나의 "
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