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
 * 직업·사회 영역의 세운 Context Rule.
 *
 * AnnualLuckContextSignal은
 * 14-6에서 이미 계산된
 * deterministic Evidence 단위다.
 *
 * 이 Rule은
 *
 * DIRECT_OFFICER
 * SEVEN_KILLINGS
 *
 * 관련 Signal을 선택한다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * Signal 1개
 * ->
 * LifeDomainRuleMatch 1개
 *
 * 로 독립 보존한다.
 *
 * 서로 다른 Signal의:
 *
 * - 상태
 * - 개수
 * - Axis
 * - 십성 유형
 *
 * 을 합쳐 새로운:
 *
 * SUPPORTIVE
 * PRESSURE
 * MIXED
 * CONTEXTUAL
 *
 * 상태를 만들지 않는다.
 *
 * =================================================
 *
 * 14차에서 실제 계산된:
 *
 * - 세운 십성
 * - 원국 × 세운 관계
 * - 격국
 * - 용신
 * - 궁위
 * - 관계
 * - 합화
 * - 대운 Segment Context
 *
 * 는 그대로 보존한다.
 *
 * 이 Rule에서 독립 Signal끼리 다시 연결하여
 * 새로운 관계 의미를 생성하지 않는다.
 */
@Component
public class CareerAnnualContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "CAREER_ANNUAL_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .ANNUAL_CONTEXT
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
                "CareerAnnualContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerAnnualContextRule은 CAREER Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                annualReferenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .ANNUAL_CONTEXT
                );

        /*
         * Natal Bundle.
         */

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
                directOfficerSignals =
                annualContext
                        .signalsOf(
                                StructureTenGodType
                                        .DIRECT_OFFICER
                        );

        List<AnnualLuckContextSignal>
                sevenKillingsSignals =
                annualContext
                        .signalsOf(
                                StructureTenGodType
                                        .SEVEN_KILLINGS
                        );

        /*
         * 동일 upstream Signal 객체가
         * 정관/칠살 조회에 중복 포함되는 경우만 제거한다.
         *
         * 이것은 상태 합성이 아니다.
         */

        List<AnnualLuckContextSignal>
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

        /*
         * =================================================
         * Signal 1개 -> Match 1개
         * =================================================
         */

        for (
                AnnualLuckContextSignal signal
                : officerSignals
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
                    "14차가 이미 계산한 십성·격국·용신·궁위·관계·합화 Context를 이 Rule에서 다시 계산하거나 서로 연결해 새 의미를 만들지 않습니다."
            );

            reasons.add(
                    "관살 관련 세운 Context를 취업·승진·퇴사·이직·직장 갈등 같은 실제 사건으로 직접 환산하지 않습니다."
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

    /*
     * 단일 Annual Signal의 상태만
     * LifeDomain 상태로 1:1 변환한다.
     */

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

        return "현재 세운의 직업·사회 관련 Annual Context Signal ["
                + signal.code()
                + "]을 독립적으로 보존합니다: "
                + signal.summary();
    }

    private List<StructureTenGodType>
    matchedOfficerTypes(
            AnnualLuckContextSignal signal
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

    /*
     * 동일 객체 중복만 제거하며
     * 서로 다른 Signal을 하나의 상태로 합치지 않는다.
     */

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
                    "CareerAnnualContextRule에는 최대 하나의 "
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