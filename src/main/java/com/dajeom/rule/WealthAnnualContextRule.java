package com.dajeom.rule;

import com.dajeom.model.AnnualLuckContextAnalysis;
import com.dajeom.model.AnnualLuckContextSignal;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
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
 * 재물 영역의 세운 Context Rule.
 *
 * DIRECT_WEALTH
 * INDIRECT_WEALTH
 *
 * 관련 AnnualLuckContextSignal을 선택한다.
 *
 * =================================================
 *
 * Signal 1개
 * ->
 * LifeDomainRuleMatch 1개
 *
 * 로 독립 보존한다.
 *
 * 정재·편재 Signal 또는
 * 서로 다른 Axis를 합쳐
 * 새 상태를 만들지 않는다.
 *
 * 실제 upstream 관계 계산은 유지한다.
 */
@Component
public class WealthAnnualContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "WEALTH_ANNUAL_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .ANNUAL_CONTEXT
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
                "WealthAnnualContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthAnnualContextRule은 WEALTH Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                annualReferenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .ANNUAL_CONTEXT
                );

        if (
                annualReferenceOptional
                        .isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?>
                annualReference =
                annualReferenceOptional
                        .orElseThrow();

        AnnualLuckContextAnalysis annualContext =
                annualReference
                        .requireSource(
                                AnnualLuckContextAnalysis.class
                        );

        List<AnnualLuckContextSignal>
                directWealthSignals =
                annualContext
                        .signalsOf(
                                StructureTenGodType
                                        .DIRECT_WEALTH
                        );

        List<AnnualLuckContextSignal>
                indirectWealthSignals =
                annualContext
                        .signalsOf(
                                StructureTenGodType
                                        .INDIRECT_WEALTH
                        );

        List<AnnualLuckContextSignal>
                wealthSignals =
                mergeIdentityUnique(
                        directWealthSignals,
                        indirectWealthSignals
                );

        if (
                wealthSignals
                        .isEmpty()
        ) {

            return List.of();
        }

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        for (
                AnnualLuckContextSignal signal
                : wealthSignals
        ) {

            Objects.requireNonNull(
                    signal,
                    "AnnualLuckContextAnalysis의 Annual Signal은 null일 수 없습니다."
            );

            LifeDomainSignalStatus status =
                    resolveStatus(
                            signal
                    );

            List<String> reasons =
                    baseReasons(
                            annualContext
                    );

            reasons.add(
                    "Annual Signal ["
                            + signal.code()
                            + "] "
                            + signal.summary()
                            + " / axis="
                            + signal.axis().name()
                            + " / status="
                            + signal.status().name()
            );

            reasons.add(
                    "이 Signal에 실제 태깅된 재물영역 참조 재성 유형은 "
                            + matchedWealthTypes(
                            signal
                    )
                            + "입니다."
            );

            for (
                    String signalReason
                    : signal.reasons()
            ) {

                reasons.add(
                        "Annual Signal ["
                                + signal.code()
                                + "] 근거: "
                                + signalReason
                );
            }

            reasons.add(
                    "이 Match는 하나의 AnnualLuckContextSignal만 보존하며 다른 Annual Signal과 상태를 합산하지 않습니다."
            );

            reasons.add(
                    "14차가 이미 계산한 십성·원국 관계·격국·용신·궁위·합화 Context를 이 Rule에서 다시 조합하지 않습니다."
            );

            reasons.add(
                    "재성 관련 세운 Context를 실제 소득·재산 증가·투자 수익·금전 손실 같은 사건으로 직접 환산하지 않습니다."
            );

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.WEALTH,
                            RULE_CODE,
                            status,
                            summaryOf(
                                    signal
                            ),
                            List.of(
                                    annualReference
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
            AnnualLuckContextAnalysis annualContext
    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "14-6 AnnualLuckContextAnalysis의 현재 세운 연도는 "
                        + annualContext.targetYear()
                        + "년입니다."
        );

        if (
                !annualContext
                        .decadeContextAvailable()
        ) {

            reasons.add(
                    "현재 세운은 대운 Segment Context를 사용할 수 없지만 14차 정책에 따라 세운 자체 annualSignals는 그대로 보존합니다."
            );
        }

        if (
                annualContext
                        .decadeTimingUncertain()
        ) {

            reasons.add(
                    "현재 세운 기간에 대운 교운시점 불확실성이 존재하므로 가능한 대운 Context 중 하나를 임의로 확정하지 않습니다."
            );
        }

        return reasons;
    }

    private LifeDomainSignalStatus resolveStatus(
            AnnualLuckContextSignal signal
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
                "지원하지 않는 AnnualLuckContextSignal 상태입니다: "
                        + signal.status()
        );
    }

    private String summaryOf(
            AnnualLuckContextSignal signal
    ) {

        return "현재 세운의 재물 관련 Annual Context Signal ["
                + signal.code()
                + "]을 독립적으로 보존합니다: "
                + signal.summary();
    }

    private List<StructureTenGodType>
    matchedWealthTypes(
            AnnualLuckContextSignal signal
    ) {

        List<StructureTenGodType> types =
                new ArrayList<>();

        if (
                signal.involves(
                        StructureTenGodType
                                .DIRECT_WEALTH
                )
        ) {

            types.add(
                    StructureTenGodType
                            .DIRECT_WEALTH
            );
        }

        if (
                signal.involves(
                        StructureTenGodType
                                .INDIRECT_WEALTH
                )
        ) {

            types.add(
                    StructureTenGodType
                            .INDIRECT_WEALTH
            );
        }

        return List.copyOf(
                types
        );
    }

    @SafeVarargs
    private final List<AnnualLuckContextSignal>
    mergeIdentityUnique(
            List<AnnualLuckContextSignal>... signalLists
    ) {

        Set<AnnualLuckContextSignal> unique =
                Collections
                        .newSetFromMap(
                                new IdentityHashMap<>()
                        );

        List<AnnualLuckContextSignal> ordered =
                new ArrayList<>();

        for (
                List<AnnualLuckContextSignal> signals
                : signalLists
        ) {

            if (
                    signals == null
            ) {

                continue;
            }

            for (
                    AnnualLuckContextSignal signal
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
                    "WealthAnnualContextRule에는 최대 하나의 "
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