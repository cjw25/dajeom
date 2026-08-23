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
 * 직업·사회 영역의 대운 Context Rule.
 *
 * DecadeLuckContextSignal은
 * 13-6에서 이미 계산된
 * deterministic Evidence 단위다.
 *
 * DIRECT_OFFICER / SEVEN_KILLINGS 관련 Signal을
 * 선택한다.
 *
 * =================================================
 *
 * Signal 1개
 * ->
 * LifeDomainRuleMatch 1개
 *
 * 로 독립 보존한다.
 *
 * 서로 다른 Signal의 상태를 합산해
 * 새 MIXED / SUPPORTIVE / PRESSURE를 만들지 않는다.
 *
 * 실제 대운 관계 계산은
 * upstream 결과 그대로 유지한다.
 */
@Component
public class CareerDecadeContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "CAREER_DECADE_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .DECADE_CONTEXT
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
                "CareerDecadeContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerDecadeContextRule은 CAREER Bundle만 평가할 수 있습니다."
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
                directOfficerSignals =
                decadeContext
                        .signalsOf(
                                StructureTenGodType
                                        .DIRECT_OFFICER
                        );

        List<DecadeLuckContextSignal>
                sevenKillingsSignals =
                decadeContext
                        .signalsOf(
                                StructureTenGodType
                                        .SEVEN_KILLINGS
                        );

        List<DecadeLuckContextSignal>
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
                DecadeLuckContextSignal signal
                : officerSignals
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
                    "관살 관련 대운 Context를 취업·승진·퇴사·이직·직위 획득·직장 갈등 같은 실제 사건으로 직접 환산하지 않습니다."
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

        return "현재 대운의 직업·사회 관련 Decade Context Signal ["
                + signal.code()
                + "]을 독립적으로 보존합니다: "
                + signal.summary();
    }

    private List<StructureTenGodType>
    matchedOfficerTypes(
            DecadeLuckContextSignal signal
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
                    "CareerDecadeContextRule에는 최대 하나의 "
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