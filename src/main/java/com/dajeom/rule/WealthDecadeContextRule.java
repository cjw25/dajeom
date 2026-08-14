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
 * 정밀화 18차.
 *
 * 재물 영역의 대운 Context Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 13-6 DecadeLuckPeriodContextAnalysis
 *
 * =================================================
 *
 * 대상 십성:
 *
 * DIRECT_WEALTH
 * INDIRECT_WEALTH
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 13차에서 이미 계산된:
 *
 * - 대운 십성
 * - 원국 × 대운 관계
 * - 용신·희신
 * - 격국
 * - 궁위
 * - 관계
 * - 합화·기능변화
 *
 * 등의 교차 Context를
 * 18차에서 다시 계산하지 않는다.
 *
 * DecadeLuckContextSignal에 이미 들어 있는
 * 상태와 deterministic 근거만
 * 재물 생활영역 Context로 변환한다.
 *
 * =================================================
 *
 * 상태 변환:
 *
 * SUPPORTED
 * -> SUPPORTIVE
 *
 * QUALIFIED만 존재
 * -> CONTEXTUAL
 *
 * WEAKENED만 존재
 * -> PRESSURE
 *
 * CONFLICTED 존재
 * -> MIXED
 *
 * 지원 계열과 약화 계열 동시 존재
 * -> MIXED
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * 재성 대운
 * -> 돈을 번다
 *
 * 재성 SUPPORTED
 * -> 재산이 증가한다
 *
 * 재성 WEAKENED
 * -> 손실이 발생한다
 *
 * 재성 CONFLICTED
 * -> 투자에 실패한다
 *
 * 같은 실제 사건 결과 확정.
 *
 * =================================================
 *
 * DECADE_CONTEXT가 없는 Bundle:
 *
 * - Natal Bundle
 * - 비활성 대운 Segment
 *
 * 에서는 아무 Match도 생성하지 않는다.
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

        /*
         * =================================================
         * Natal / 비활성 Segment
         * =================================================
         *
         * DECADE_CONTEXT가 없는 것은 정상.
         */

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

        /*
         * =================================================
         * 재성 관련 대운 Signal
         * =================================================
         *
         * signalsOf(StructureTenGodType)는
         * TEN_GOD Axis만 조회하는 것이 아니라
         *
         * Signal.tenGodTypes에 해당 십성이
         * 포함된 모든 Axis Signal을 반환한다.
         *
         * 따라서:
         *
         * - TEN_GOD
         * - USEFUL_GOD
         * - STRUCTURE
         * - RELATION
         * - TRANSFORMATION
         *
         * 중 재성에 직접 연결된 Signal이 있다면
         * 모두 보존될 수 있다.
         *
         * 같은 Signal이 정재/편재 양쪽 조회에서
         * 중복될 가능성에 대비해
         * Object Identity 기준으로 합친다.
         */

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
                wealthSignals.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 13-6 상태 집계
         * =================================================
         */

        SignalCounts counts =
                countStatuses(
                        wealthSignals
                );

        /*
         * =================================================
         * 18차 생활영역 상태
         * =================================================
         */

        LifeDomainSignalStatus status =
                resolveStatus(
                        counts
                );

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

        reasons.add(
                "정재 관련 Decade Context Signal은 "
                        + directWealthSignals.size()
                        + "개입니다."
        );

        reasons.add(
                "편재 관련 Decade Context Signal은 "
                        + indirectWealthSignals.size()
                        + "개입니다."
        );

        reasons.add(
                "Object Identity 중복 제거 후 재성 관련 대운 Signal은 "
                        + wealthSignals.size()
                        + "개입니다."
        );

        reasons.add(
                "SUPPORTED="
                        + counts.supported()
                        + ", QUALIFIED="
                        + counts.qualified()
                        + ", WEAKENED="
                        + counts.weakened()
                        + ", CONFLICTED="
                        + counts.conflicted()
                        + "입니다."
        );

        /*
         * =================================================
         * 원 Signal deterministic 근거 보존
         * =================================================
         */

        for (
                DecadeLuckContextSignal signal
                : wealthSignals
        ) {

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
        }

        reasons.add(
                "13차가 이미 판정한 대운 교차 Context를 재물 생활영역 상태로 변환할 뿐, 용신·격국·십성·관계·합화를 18차에서 다시 계산하지 않습니다."
        );

        reasons.add(
                "대운의 재성 Context를 실제 소득·재산 증가·투자 수익·금전 손실 사건으로 직접 환산하지 않습니다."
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
                                decadeReference
                        ),
                        List.copyOf(
                                reasons
                        )
                )
        );
    }

    /*
     * =================================================
     * 상태 집계
     * =================================================
     */

    private SignalCounts countStatuses(
            List<DecadeLuckContextSignal> signals
    ) {

        long supported =
                signals
                        .stream()
                        .filter(
                                DecadeLuckContextSignal
                                        ::supported
                        )
                        .count();

        long qualified =
                signals
                        .stream()
                        .filter(
                                DecadeLuckContextSignal
                                        ::qualified
                        )
                        .count();

        long weakened =
                signals
                        .stream()
                        .filter(
                                DecadeLuckContextSignal
                                        ::weakened
                        )
                        .count();

        long conflicted =
                signals
                        .stream()
                        .filter(
                                DecadeLuckContextSignal
                                        ::conflicted
                        )
                        .count();

        return new SignalCounts(
                supported,
                qualified,
                weakened,
                conflicted
        );
    }

    /*
     * =================================================
     * 13-6 -> 18차 상태 변환
     * =================================================
     *
     * 하나의 점수나 최종 길흉으로
     * 축약하지 않는다.
     *
     * CONFLICTED 존재
     * -> MIXED
     *
     * SUPPORTED / QUALIFIED
     * +
     * WEAKENED
     * -> MIXED
     *
     * WEAKENED만 존재
     * -> PRESSURE
     *
     * SUPPORTED 존재
     * -> SUPPORTIVE
     *
     * QUALIFIED만 존재
     * -> CONTEXTUAL
     */

    private LifeDomainSignalStatus resolveStatus(
            SignalCounts counts
    ) {

        boolean supportiveSide =
                counts.supported() > 0
                        || counts.qualified() > 0;

        boolean pressureSide =
                counts.weakened() > 0;

        if (
                counts.conflicted() > 0
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                supportiveSide
                        && pressureSide
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                pressureSide
        ) {

            return LifeDomainSignalStatus
                    .PRESSURE;
        }

        if (
                counts.supported() > 0
        ) {

            return LifeDomainSignalStatus
                    .SUPPORTIVE;
        }

        return LifeDomainSignalStatus
                .CONTEXTUAL;
    }

    /*
     * =================================================
     * Summary
     * =================================================
     */

    private String summaryOf(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 대운에서 재성 관련 Context가 재물 기능을 지지하는 방향으로 나타납니다.";

            case PRESSURE ->
                    "현재 대운에서 재성 관련 Context에 약화·부담 방향이 존재합니다.";

            case MIXED ->
                    "현재 대운의 재성 관련 Context에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 대운에서 재성 관련 Context가 존재하지만 재물 방향을 단독으로 확정할 수준은 아닙니다.";
        };
    }

    /*
     * =================================================
     * Object Identity 중복 제거
     * =================================================
     */

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

        for (
                List<DecadeLuckContextSignal> signals
                : signalLists
        ) {

            if (
                    signals == null
            ) {

                continue;
            }

            unique.addAll(
                    signals
            );
        }

        return List.copyOf(
                unique
        );
    }

    /*
     * =================================================
     * Temporal Evidence 조회
     * =================================================
     *
     * 0개:
     *
     * Natal Bundle 또는
     * 현재 Segment에 ACTIVE 대운 Context 없음.
     *
     * 정상.
     *
     * 1개:
     *
     * 정상적인 ACTIVE 대운 Context.
     *
     * 2개 이상:
     *
     * 동일 Domain Segment Bundle의
     * Evidence 구성 오류.
     *
     * 교운 불확실성을 해결하기 위해
     * 여기서 임의로 하나를 선택하지 않는다.
     */

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

    /*
     * =================================================
     * 내부 상태 집계
     * =================================================
     */

    private record SignalCounts(

            long supported,

            long qualified,

            long weakened,

            long conflicted

    ) {
    }
}