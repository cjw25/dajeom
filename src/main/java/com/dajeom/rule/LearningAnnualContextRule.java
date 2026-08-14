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
 * 정밀화 18차.
 *
 * 학습·성장 영역의 세운 Context Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 14-6 AnnualLuckContextAnalysis
 *
 * =================================================
 *
 * 학습·성장 영역에서 읽는 세운 십성:
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
 * 인성:
 *
 * 학습
 * 흡수
 * 정리
 * 내재화
 *
 * 측면의 Context.
 *
 * =================================================
 *
 * 식상:
 *
 * 표현
 * 산출
 * 응용
 * 외부 발현
 *
 * 측면의 Context.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 14차에서 이미 계산된:
 *
 * - 세운 십성
 * - 원국 × 세운
 * - 격국
 * - 용신
 * - 궁위
 * - 관계
 * - 합화
 * - 가능한 경우 대운 Segment Context
 *
 * 를 18차에서 다시 계산하지 않는다.
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
 * 인성 세운
 * -> 시험 합격
 *
 * 식상 세운
 * -> 성과 발생
 *
 * 인성 지원
 * -> 자격증 취득
 *
 * 식상 약화
 * -> 학업 실패
 *
 * 같은 실제 사건 결과 확정.
 *
 * =================================================
 *
 * Natal Bundle에는 ANNUAL_CONTEXT가 없으므로
 * 아무 Match도 생성하지 않는다.
 *
 * Timeline Segment Bundle에서만 작동한다.
 */
@Component
public class LearningAnnualContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "LEARNING_ANNUAL_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .ANNUAL_CONTEXT
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
                "LearningAnnualContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.LEARNING_GROWTH
        ) {

            throw new IllegalArgumentException(
                    "LearningAnnualContextRule은 LEARNING_GROWTH Bundle만 평가할 수 있습니다."
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
         * =================================================
         * Natal Bundle
         * =================================================
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

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * 인성 Annual Context
         * =================================================
         */

        List<AnnualLuckContextSignal>
                directResourceSignals =
                annualContext
                        .signalsOf(
                                StructureTenGodType
                                        .DIRECT_RESOURCE
                        );

        List<AnnualLuckContextSignal>
                indirectResourceSignals =
                annualContext
                        .signalsOf(
                                StructureTenGodType
                                        .INDIRECT_RESOURCE
                        );

        List<AnnualLuckContextSignal>
                resourceSignals =
                mergeIdentityUnique(
                        directResourceSignals,
                        indirectResourceSignals
                );

        if (
                !resourceSignals
                        .isEmpty()
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
                            annualContext
                    );

            reasons.add(
                    "정인 관련 Annual Context Signal은 "
                            + directResourceSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "편인 관련 Annual Context Signal은 "
                            + indirectResourceSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "Object Identity 중복 제거 후 인성 관련 Signal은 "
                            + resourceSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    statusSummary(
                            counts
                    )
            );

            reasons.add(
                    "인성 관련 세운 Context는 학습·흡수·정리·내재화 측면의 작동으로 해석합니다."
            );

            reasons.add(
                    "인성 관련 세운 Context를 시험 합격·학력 상승·자격 취득 같은 실제 성과로 직접 환산하지 않습니다."
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
                                    annualReference
                            ),
                            reasons
                    )
            );
        }

        /*
         * =================================================
         * 2.
         * 식상 Annual Context
         * =================================================
         */

        List<AnnualLuckContextSignal>
                eatingGodSignals =
                annualContext
                        .signalsOf(
                                StructureTenGodType
                                        .EATING_GOD
                        );

        List<AnnualLuckContextSignal>
                hurtingOfficerSignals =
                annualContext
                        .signalsOf(
                                StructureTenGodType
                                        .HURTING_OFFICER
                        );

        List<AnnualLuckContextSignal>
                outputSignals =
                mergeIdentityUnique(
                        eatingGodSignals,
                        hurtingOfficerSignals
                );

        if (
                !outputSignals
                        .isEmpty()
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
                            annualContext
                    );

            reasons.add(
                    "식신 관련 Annual Context Signal은 "
                            + eatingGodSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "상관 관련 Annual Context Signal은 "
                            + hurtingOfficerSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "Object Identity 중복 제거 후 식상 관련 Signal은 "
                            + outputSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    statusSummary(
                            counts
                    )
            );

            reasons.add(
                    "식상 관련 세운 Context는 표현·산출·응용·외부 발현 측면의 작동으로 해석합니다."
            );

            reasons.add(
                    "식상 관련 세운 Context를 시험 성과·창작 성공·업무 성과 같은 실제 결과로 직접 환산하지 않습니다."
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
                                    annualReference
                            ),
                            reasons
                    )
            );
        }

        /*
         * =================================================
         * 3.
         * 인성 + 식상 동시 작동
         * =================================================
         *
         * 흡수·내재화 축과
         * 표현·산출 축이
         * 같은 세운에서 동시에 관여.
         *
         * 둘 중 하나를 대표축으로 선택하지 않는다.
         *
         * 지원과 압박이 섞이면 MIXED.
         */

        if (
                !resourceSignals.isEmpty()
                        && !outputSignals.isEmpty()
        ) {

            List<AnnualLuckContextSignal>
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
                            annualContext
                    );

            reasons.add(
                    "인성 Context와 식상 Context가 같은 세운에서 동시에 존재합니다."
            );

            reasons.add(
                    "인성 Signal은 "
                            + resourceSignals.size()
                            + "개, 식상 Signal은 "
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

            reasons.add(
                    "학습·흡수·내재화 축과 표현·산출·응용 축을 어느 하나로 축약하지 않습니다."
            );

            reasons.add(
                    "두 축의 동시 작동을 학습 성취나 결과 발생으로 자동 승격하지 않습니다."
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
                                    annualReference
                            ),
                            reasons
                    )
            );
        }

        return List.copyOf(
                matches
        );
    }

    /*
     * =================================================
     * 공통 세운 Reason
     * =================================================
     */

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

        reasons.add(
                "14차가 이미 계산한 십성·원국 관계·격국·용신·궁위·합화 Context를 18차에서 다시 계산하지 않습니다."
        );

        /*
         * =================================================
         * 대운 Context 사용 불가
         * =================================================
         *
         * annualSignals는 그대로 유효.
         */

        if (
                !annualContext
                        .decadeContextAvailable()
        ) {

            reasons.add(
                    "대운 Segment Context를 사용할 수 없는 상태이지만 14차 정책에 따라 세운 자체 annualSignals는 그대로 보존하여 사용합니다."
            );
        }

        /*
         * =================================================
         * 대운 교운 불확실성
         * =================================================
         *
         * 가능한 대운을 하나로 선택하지 않는다.
         */

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
     * =================================================
     * 상태 집계
     * =================================================
     */

    private SignalCounts countStatuses(
            List<AnnualLuckContextSignal> signals
    ) {

        long supported =
                signals
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .status()
                                                .supported()
                        )
                        .count();

        long qualified =
                signals
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .status()
                                                .qualified()
                        )
                        .count();

        long weakened =
                signals
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .status()
                                                .weakened()
                        )
                        .count();

        long conflicted =
                signals
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .status()
                                                .conflicted()
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
     * 18차 상태 변환
     * =================================================
     *
     * CONFLICTED 존재
     * -> MIXED
     *
     * SUPPORT/QUALIFIED와 WEAKENED 공존
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

        boolean weakenedSide =
                counts.weakened() > 0;

        if (
                counts.conflicted() > 0
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                supportiveSide
                        && weakenedSide
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                weakenedSide
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
                    "현재 세운에서 인성 관련 Context가 학습·흡수·내재화 기능을 지지하는 방향으로 나타납니다.";

            case PRESSURE ->
                    "현재 세운에서 인성 관련 Context에 학습·내재화 기능의 약화 또는 부담 방향이 존재합니다.";

            case MIXED ->
                    "현재 세운의 인성 관련 Context에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 세운에서 인성 관련 Context가 존재하지만 학습·성장 방향을 단독으로 확정할 수준은 아닙니다.";
        };
    }

    private String summaryOfOutput(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 세운에서 식상 관련 Context가 표현·산출·응용 기능을 지지하는 방향으로 나타납니다.";

            case PRESSURE ->
                    "현재 세운에서 식상 관련 Context에 표현·산출 기능의 약화 또는 소모 방향이 존재합니다.";

            case MIXED ->
                    "현재 세운의 식상 관련 Context에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 세운에서 식상 관련 Context가 존재하지만 표현·성장 방향을 단독으로 확정할 수준은 아닙니다.";
        };
    }

    private String summaryOfCombined(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 세운에서 학습·내재화와 표현·산출 두 축이 함께 지지 방향을 형성합니다.";

            case PRESSURE ->
                    "현재 세운에서 학습·내재화와 표현·산출 두 축에 함께 부담 방향이 존재합니다.";

            case MIXED ->
                    "현재 세운에서 학습·내재화와 표현·산출 축에 지원·약화·상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 세운에서 학습·내재화와 표현·산출 두 축이 함께 관여하지만 단독 성과 방향으로 확정할 수준은 아닙니다.";
        };
    }

    /*
     * =================================================
     * Object Identity 중복 제거
     * =================================================
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

        for (
                List<AnnualLuckContextSignal> signals
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
     * Natal Bundle
     * -> 0개 정상.
     *
     * Segment Bundle
     * -> 1개 정상.
     *
     * 2개 이상
     * -> Evidence 구성 오류.
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
                    "LearningAnnualContextRule에는 최대 하나의 "
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