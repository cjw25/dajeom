package com.dajeom.rule;

import com.dajeom.model.DecadeLuckContextSignal;
import com.dajeom.model.DecadeLuckPeriodContextAnalysis;
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
 * 재물 영역의 대운 Context Rule.
 *
 * DIRECT_WEALTH
 * INDIRECT_WEALTH
 *
 * 관련 DecadeLuckContextSignal을 선택한다.
 *
 * =================================================
 *
 * Signal 1개
 * ->
 * LifeDomainRuleMatch 1개
 *
 * 로 독립 보존한다.
 *
 * 서로 다른 Signal 또는 Axis의 공존만으로
 * 새 재물 상태를 생성하지 않는다.
 *
 * upstream에서 실제 계산한:
 *
 * - 대운 십성
 * - 원국 × 대운 관계
 * - 용신·희신
 * - 격국
 * - 궁위
 * - 관계
 * - 합화·기능변화
 *
 * 는 그대로 유지한다.
 */
@Component
public class WealthDecadeContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "WEALTH_DECADE_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .DECADE_CONTEXT
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
                "WealthDecadeContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthDecadeContextRule은 WEALTH Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                decadeReferenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .DECADE_CONTEXT
                );

        if (
                decadeReferenceOptional
                        .isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?>
                decadeReference =
                decadeReferenceOptional
                        .orElseThrow();

        DecadeLuckPeriodContextAnalysis decadeContext =
                decadeReference
                        .requireSource(
                                DecadeLuckPeriodContextAnalysis.class
                        );

        List<DecadeLuckContextSignal>
                directWealthSignals =
                decadeContext
                        .signalsOf(
                                StructureTenGodType
                                        .DIRECT_WEALTH
                        );

        List<DecadeLuckContextSignal>
                indirectWealthSignals =
                decadeContext
                        .signalsOf(
                                StructureTenGodType
                                        .INDIRECT_WEALTH
                        );

        List<DecadeLuckContextSignal>
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
                DecadeLuckContextSignal signal
                : wealthSignals
        ) {

            Objects.requireNonNull(
                    signal,
                    "DecadeLuckPeriodContextAnalysis의 Decade Signal은 null일 수 없습니다."
            );

            LifeDomainSignalStatus status =
                    resolveStatus(
                            signal
                    );

            List<String> reasons =
                    baseReasons(
                            decadeContext
                    );

            reasons.add(
                    "Decade Signal ["
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
                        "Decade Signal ["
                                + signal.code()
                                + "] 근거: "
                                + signalReason
                );
            }

            reasons.add(
                    "이 Match는 하나의 DecadeLuckContextSignal만 보존하며 다른 Decade Signal과 상태를 합산하지 않습니다."
            );

            reasons.add(
                    "13차가 이미 계산한 대운 십성·원국 관계·용신·격국·궁위·합화·기능변화를 이 Rule에서 다시 조합하지 않습니다."
            );

            reasons.add(
                    "재성 관련 대운 Context를 실제 소득·재산 증가·투자 수익·금전 손실 같은 사건으로 직접 환산하지 않습니다."
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
                                    decadeReference
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
            DecadeLuckPeriodContextAnalysis decadeContext
    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "13-6 대운 Context의 Sequence는 "
                        + decadeContext.sequenceNumber()
                        + "입니다."
        );

        reasons.add(
                "현재 대운 간지는 "
                        + decadeContext.luckPillar()
                        + "입니다."
        );

        return reasons;
    }

    private LifeDomainSignalStatus resolveStatus(
            DecadeLuckContextSignal signal
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
                "지원하지 않는 DecadeLuckContextSignal 상태입니다: "
                        + signal.status()
        );
    }

    private String summaryOf(
            DecadeLuckContextSignal signal
    ) {

        return "현재 대운의 재물 관련 Decade Context Signal ["
                + signal.code()
                + "]을 독립적으로 보존합니다: "
                + signal.summary();
    }

    private List<StructureTenGodType>
    matchedWealthTypes(
            DecadeLuckContextSignal signal
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
    private final List<DecadeLuckContextSignal>
    mergeIdentityUnique(
            List<DecadeLuckContextSignal>... signalLists
    ) {

        Set<DecadeLuckContextSignal> unique =
                Collections
                        .newSetFromMap(
                                new IdentityHashMap<>()
                        );

        List<DecadeLuckContextSignal> ordered =
                new ArrayList<>();

        for (
                List<DecadeLuckContextSignal> signals
                : signalLists
        ) {

            if (
                    signals == null
            ) {

                continue;
            }

            for (
                    DecadeLuckContextSignal signal
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
                    "WealthDecadeContextRule에는 최대 하나의 "
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