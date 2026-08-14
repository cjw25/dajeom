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
 * 학습·성장 영역의 대운 Context Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 13-6 DecadeLuckPeriodContextAnalysis
 *
 * =================================================
 *
 * 학습·성장 영역에서 읽는 대운 십성:
 *
 * 인성:
 *
 * DIRECT_RESOURCE
 * INDIRECT_RESOURCE
 *
 * 식상:
 *
 * EATING_GOD
 * HURTING_OFFICER
 *
 * =================================================
 *
 * 인성 Context:
 *
 * - 학습
 * - 흡수
 * - 정리
 * - 내재화
 *
 * =================================================
 *
 * 식상 Context:
 *
 * - 표현
 * - 산출
 * - 응용
 * - 외부 발현
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
 * DecadeLuckContextSignal에 이미 존재하는
 * deterministic 상태와 근거만
 * 학습·성장 Domain으로 변환한다.
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
 * 인성 대운
 * -> 공부를 잘한다
 *
 * 인성 SUPPORTED
 * -> 시험에 합격한다
 *
 * 식상 SUPPORTED
 * -> 성과가 발생한다
 *
 * 식상 WEAKENED
 * -> 학업에 실패한다
 *
 * 인성·식상 동시 활성
 * -> 자격증을 취득한다
 *
 * 같은 실제 사건·성과 확정.
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
public class LearningDecadeContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "LEARNING_DECADE_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .DECADE_CONTEXT
            );

    @Override
    public LifeDomain domain() {

        return LifeDomain.LEARNING_GROWTH;
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
                "LearningDecadeContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.LEARNING_GROWTH
        ) {

            throw new IllegalArgumentException(
                    "LearningDecadeContextRule은 LEARNING_GROWTH Bundle만 평가할 수 있습니다."
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

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * 인성 대운 Context
         * =================================================
         *
         * 정인 + 편인 관련 모든 13-6 Signal.
         *
         * 특정 Axis로 제한하지 않는다.
         *
         * Signal.tenGodTypes에 인성이 태깅된:
         *
         * - USEFUL_GOD
         * - STRUCTURE
         * - TEN_GOD
         * - RELATION
         * - TRANSFORMATION
         *
         * 등의 교차 Context가 있다면
         * 모두 보존될 수 있다.
         */

        List<DecadeLuckContextSignal>
                directResourceSignals =
                decadeContext
                        .signalsOf(
                                StructureTenGodType
                                        .DIRECT_RESOURCE
                        );

        List<DecadeLuckContextSignal>
                indirectResourceSignals =
                decadeContext
                        .signalsOf(
                                StructureTenGodType
                                        .INDIRECT_RESOURCE
                        );

        List<DecadeLuckContextSignal>
                resourceSignals =
                mergeIdentityUnique(
                        directResourceSignals,
                        indirectResourceSignals
                );

        if (
                !resourceSignals.isEmpty()
        ) {

            SignalCounts counts =
                    countStatuses(
                            resourceSignals
                    );

            LifeDomainSignalStatus status =
                    resolveStatus(
                            counts
                    );

            List<String> reasons =
                    baseReasons(
                            decadeContext
                    );

            reasons.add(
                    "정인 관련 Decade Context Signal은 "
                            + directResourceSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "편인 관련 Decade Context Signal은 "
                            + indirectResourceSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "Object Identity 중복 제거 후 인성 관련 대운 Signal은 "
                            + resourceSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    statusSummary(
                            counts
                    )
            );

            appendSignalReasons(
                    reasons,
                    resourceSignals
            );

            reasons.add(
                    "인성 관련 대운 Context를 학습·흡수·정리·내재화 기능의 시간적 Context로 사용합니다."
            );

            reasons.add(
                    "인성 관련 대운 Context를 시험 합격·학력 상승·자격 취득 같은 실제 성과로 직접 환산하지 않습니다."
            );

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            status,
                            summaryOfResource(
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
         * 2.
         * 식상 대운 Context
         * =================================================
         *
         * 식신 + 상관 관련 모든 13-6 Signal.
         */

        List<DecadeLuckContextSignal>
                eatingGodSignals =
                decadeContext
                        .signalsOf(
                                StructureTenGodType
                                        .EATING_GOD
                        );

        List<DecadeLuckContextSignal>
                hurtingOfficerSignals =
                decadeContext
                        .signalsOf(
                                StructureTenGodType
                                        .HURTING_OFFICER
                        );

        List<DecadeLuckContextSignal>
                outputSignals =
                mergeIdentityUnique(
                        eatingGodSignals,
                        hurtingOfficerSignals
                );

        if (
                !outputSignals.isEmpty()
        ) {

            SignalCounts counts =
                    countStatuses(
                            outputSignals
                    );

            LifeDomainSignalStatus status =
                    resolveStatus(
                            counts
                    );

            List<String> reasons =
                    baseReasons(
                            decadeContext
                    );

            reasons.add(
                    "식신 관련 Decade Context Signal은 "
                            + eatingGodSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "상관 관련 Decade Context Signal은 "
                            + hurtingOfficerSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "Object Identity 중복 제거 후 식상 관련 대운 Signal은 "
                            + outputSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    statusSummary(
                            counts
                    )
            );

            appendSignalReasons(
                    reasons,
                    outputSignals
            );

            reasons.add(
                    "식상 관련 대운 Context를 표현·산출·응용·외부 발현 기능의 시간적 Context로 사용합니다."
            );

            reasons.add(
                    "식상 관련 대운 Context를 시험 성과·창작 성공·업무 성과 같은 실제 결과로 직접 환산하지 않습니다."
            );

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            status,
                            summaryOfOutput(
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
         * 3.
         * 인성 + 식상 동시 작동
         * =================================================
         *
         * 학습·내재화 축과
         * 표현·산출 축이
         * 같은 대운에서 함께 관여.
         *
         * 어느 한 축을 대표로 고르지 않는다.
         *
         * 동시 관여 자체를
         * 성공이나 성과 확률로 변환하지 않는다.
         */

        if (
                !resourceSignals.isEmpty()
                        && !outputSignals.isEmpty()
        ) {

            List<DecadeLuckContextSignal>
                    combinedSignals =
                    mergeIdentityUnique(
                            resourceSignals,
                            outputSignals
                    );

            SignalCounts counts =
                    countStatuses(
                            combinedSignals
                    );

            LifeDomainSignalStatus status =
                    resolveStatus(
                            counts
                    );

            List<String> reasons =
                    baseReasons(
                            decadeContext
                    );

            reasons.add(
                    "인성 Context와 식상 Context가 같은 대운에서 동시에 존재합니다."
            );

            reasons.add(
                    "인성 관련 Signal은 "
                            + resourceSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "식상 관련 Signal은 "
                            + outputSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "Object Identity 중복 제거 후 두 축을 합친 Signal은 "
                            + combinedSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    statusSummary(
                            counts
                    )
            );

            appendSignalReasons(
                    reasons,
                    combinedSignals
            );

            reasons.add(
                    "학습·흡수·내재화 축과 표현·산출·응용 축을 어느 하나의 대표 구조로 축약하지 않습니다."
            );

            reasons.add(
                    "두 축의 동시 작동을 학업 성취·자격 취득·성과 발생 같은 실제 결과로 자동 승격하지 않습니다."
            );

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            status,
                            summaryOfCombined(
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

        return List.copyOf(
                matches
        );
    }

    /*
     * =================================================
     * 공통 대운 Reason
     * =================================================
     */

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

        reasons.add(
                "13차에서 이미 계산한 대운 십성·원국 관계·용신·격국·궁위·합화 Context를 18차에서 다시 계산하지 않습니다."
        );

        return reasons;
    }

    /*
     * =================================================
     * 원 Signal 근거 전달
     * =================================================
     *
     * 13-6 Signal의:
     *
     * - code
     * - summary
     * - axis
     * - status
     * - reasons
     *
     * 를 18차 Match까지 그대로 전달한다.
     *
     * 이후 AI Grounding에서
     * 실제 deterministic 근거를 추적할 수 있다.
     */

    private void appendSignalReasons(

            List<String> target,

            List<DecadeLuckContextSignal> signals

    ) {

        for (
                DecadeLuckContextSignal signal
                : signals
        ) {

            target.add(
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
                    String reason
                    : signal.reasons()
            ) {

                target.add(
                        "Decade Signal ["
                                + signal.code()
                                + "] 근거: "
                                + reason
                );
            }
        }
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

    private String statusSummary(
            SignalCounts counts
    ) {

        return "SUPPORTED="
                + counts.supported()
                + ", QUALIFIED="
                + counts.qualified()
                + ", WEAKENED="
                + counts.weakened()
                + ", CONFLICTED="
                + counts.conflicted()
                + "입니다.";
    }

    /*
     * =================================================
     * Summary
     * =================================================
     */

    private String summaryOfResource(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 대운에서 인성 관련 Context가 학습·흡수·내재화 기능을 지지하는 방향으로 나타납니다.";

            case PRESSURE ->
                    "현재 대운에서 인성 관련 Context에 학습·내재화 기능의 약화 또는 부담 방향이 존재합니다.";

            case MIXED ->
                    "현재 대운의 인성 관련 Context에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 대운에서 인성 관련 Context가 존재하지만 학습·성장 방향을 단독으로 확정할 수준은 아닙니다.";
        };
    }

    private String summaryOfOutput(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 대운에서 식상 관련 Context가 표현·산출·응용 기능을 지지하는 방향으로 나타납니다.";

            case PRESSURE ->
                    "현재 대운에서 식상 관련 Context에 표현·산출 기능의 약화 또는 소모 방향이 존재합니다.";

            case MIXED ->
                    "현재 대운의 식상 관련 Context에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 대운에서 식상 관련 Context가 존재하지만 표현·성장 방향을 단독으로 확정할 수준은 아닙니다.";
        };
    }

    private String summaryOfCombined(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 대운에서 학습·내재화와 표현·산출 두 축이 함께 지지 방향을 형성합니다.";

            case PRESSURE ->
                    "현재 대운에서 학습·내재화와 표현·산출 두 축에 함께 부담 방향이 존재합니다.";

            case MIXED ->
                    "현재 대운에서 학습·내재화와 표현·산출 축에 지원·약화·상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 대운에서 학습·내재화와 표현·산출 두 축이 함께 관여하지만 단독 성과 방향으로 확정할 수준은 아닙니다.";
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
     * ACTIVE 대운 Context가 없는 Segment.
     *
     * 정상.
     *
     * 1개:
     *
     * 현재 Segment의 대운 Context.
     *
     * 2개 이상:
     *
     * Evidence 구성 오류.
     *
     * 교운 불확실성이 있다고 하더라도
     * 이 Rule에서 가능한 대운 하나를
     * 임의 선택하지 않는다.
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
                    "LearningDecadeContextRule에는 최대 하나의 "
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