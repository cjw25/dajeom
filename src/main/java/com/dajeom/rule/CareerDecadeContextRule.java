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
 * 직업·사회 영역의 대운 Context Rule.
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
 * DIRECT_OFFICER
 * SEVEN_KILLINGS
 *
 * =================================================
 *
 * 의미:
 *
 * 정관:
 *
 * - 역할
 * - 책임
 * - 규범
 * - 제도적 Context
 *
 * 칠살:
 *
 * - 압력
 * - 책임 부담
 * - 통제 요구
 * - 긴장 Context
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
 * 정관 대운
 * -> 취업한다
 *
 * 관살 SUPPORTED
 * -> 승진한다
 *
 * 관살 WEAKENED
 * -> 퇴사한다
 *
 * 관살 CONFLICTED
 * -> 직장 문제가 생긴다
 *
 * 칠살 대운
 * -> 이직한다
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

        /*
         * =================================================
         * Natal / 비활성 Segment
         * =================================================
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
         * 정관 / 칠살 Signal
         * =================================================
         *
         * signalsOf(StructureTenGodType)는
         * 해당 십성이 연결된 모든 Context Signal을
         * 반환한다.
         *
         * 따라서 단순 TEN_GOD Axis뿐 아니라
         * USEFUL_GOD / STRUCTURE / RELATION /
         * TRANSFORMATION 등의 Signal도
         * tenGodTypes에 관살이 포함되어 있다면
         * 함께 보존될 수 있다.
         */

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

        /*
         * =================================================
         * 상태 집계
         * =================================================
         */

        SignalCounts counts =
                countStatuses(
                        officerSignals
                );

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
                "정관 관련 Decade Context Signal은 "
                        + directOfficerSignals.size()
                        + "개입니다."
        );

        reasons.add(
                "칠살 관련 Decade Context Signal은 "
                        + sevenKillingsSignals.size()
                        + "개입니다."
        );

        reasons.add(
                "Object Identity 중복 제거 후 관살 관련 대운 Signal은 "
                        + officerSignals.size()
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
         * 원 Signal 근거 보존
         * =================================================
         */

        for (
                DecadeLuckContextSignal signal
                : officerSignals
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
                "13차가 이미 판정한 대운 교차 Context를 직업·사회 생활영역 상태로 변환할 뿐, 용신·격국·십성·관계·합화를 18차에서 다시 계산하지 않습니다."
        );

        reasons.add(
                "관살 관련 대운 Context를 취업·승진·퇴사·이직·직위 획득·직장 갈등 같은 실제 사건으로 직접 환산하지 않습니다."
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
     * CONFLICTED가 하나라도 존재하면
     * 상충 구조를 없애지 않고 MIXED.
     *
     * 지원 방향:
     *
     * SUPPORTED / QUALIFIED
     *
     * 부담 방향:
     *
     * WEAKENED
     *
     * 두 방향이 동시에 존재해도 MIXED.
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
                    "현재 대운에서 관살 관련 Context가 직업·사회 역할 기능을 지지하는 방향으로 나타납니다.";

            case PRESSURE ->
                    "현재 대운에서 관살 관련 Context에 역할·책임의 약화 또는 부담 방향이 존재합니다.";

            case MIXED ->
                    "현재 대운의 관살 관련 Context에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 대운에서 관살 관련 Context가 존재하지만 직업·사회 방향을 단독으로 확정할 수준은 아닙니다.";
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
     * 현재 Segment에 ACTIVE 대운 없음.
     *
     * 정상.
     *
     * 1개:
     *
     * 정상적인 ACTIVE 대운 Context.
     *
     * 2개 이상:
     *
     * 동일 Segment Bundle 구성 오류.
     *
     * 가능한 대운 중 하나를
     * Rule이 임의 선택하지 않는다.
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