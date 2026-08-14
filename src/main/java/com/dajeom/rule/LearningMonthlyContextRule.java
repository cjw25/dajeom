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
 * 정밀화 18차.
 *
 * 학습·성장 영역의 월운 Context Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 15-7 MonthlyLuckContextAnalysis
 *
 * =================================================
 *
 * 학습·성장 영역에서 읽는 기능:
 *
 * 1.
 * 인성 계열
 *
 * DIRECT_RESOURCE
 * INDIRECT_RESOURCE
 *
 * 내부 명리 의미:
 *
 * - 받아들이기
 * - 배우기
 * - 이해하기
 * - 정리하기
 * - 내 것으로 만들기
 *
 * =================================================
 *
 * 2.
 * 식상 계열
 *
 * EATING_GOD
 * HURTING_OFFICER
 *
 * 내부 명리 의미:
 *
 * - 표현하기
 * - 만들어내기
 * - 응용하기
 * - 밖으로 꺼내기
 * - 결과물로 연결하기
 *
 * =================================================
 *
 * 3.
 * 인성 + 식상 동시 관여
 *
 * 받아들이는 기능과
 * 밖으로 표현하는 기능이
 * 같은 월운에서 함께 움직이는지 보존한다.
 *
 * =================================================
 *
 * 시간층 경계:
 *
 * MonthlyLuckContextAnalysis.signalsOf(...)
 *
 * 만 사용한다.
 *
 * 이 API는 monthlySignals만 조회한다.
 *
 * allSignals()는:
 *
 * monthlySignals
 * +
 * decadeContexts.segmentSignals
 *
 * 이므로 사용하지 않는다.
 *
 * 대운 Context는 별도 DECADE_CONTEXT Rule,
 * 세운 Context는 별도 ANNUAL_CONTEXT Rule이
 * 각각 담당한다.
 *
 * =================================================
 *
 * 15차에서 이미 계산된:
 *
 * - 월운 십성
 * - 원국 × 월운 관계
 * - 세운 × 월운 관계
 * - 대운 × 월운 관계
 * - 용신
 * - 격국
 * - 궁위
 * - 합화·기능변화
 * - 다층 관계
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
 * 인성 월운
 * -> 공부를 잘한다
 *
 * 인성 SUPPORTED
 * -> 시험에 합격한다
 *
 * 식상 월운
 * -> 성과가 난다
 *
 * 식상 WEAKENED
 * -> 공부가 안 된다
 *
 * 인성 + 식상
 * -> 자격증을 취득한다
 *
 * 같은 실제 결과 확정.
 *
 * =================================================
 *
 * 최종 사용자 풀이에서는
 * 정인·편인·식신·상관 같은 용어보다:
 *
 * - 배우고 이해하는 속도
 * - 정보를 정리하는 방식
 * - 생각을 실제 행동으로 옮기는 정도
 * - 표현과 결과물 생성
 * - 준비와 실행 사이의 균형
 *
 * 같은 생활언어로 변환할 수 있도록
 * deterministic 근거를 보존한다.
 */
@Component
public class LearningMonthlyContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "LEARNING_MONTHLY_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .MONTHLY_CONTEXT
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
                "LearningMonthlyContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.LEARNING_GROWTH
        ) {

            throw new IllegalArgumentException(
                    "LearningMonthlyContextRule은 LEARNING_GROWTH Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                monthlyReferenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .MONTHLY_CONTEXT
                );

        /*
         * =================================================
         * Natal Bundle
         * =================================================
         */

        if (
                monthlyReferenceOptional.isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?>
                monthlyReference =
                monthlyReferenceOptional.orElseThrow();

        MonthlyLuckContextAnalysis monthlyContext =
                monthlyReference
                        .requireSource(
                                MonthlyLuckContextAnalysis.class
                        );

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * 인성 계열
         * =================================================
         */

        List<MonthlyLuckContextSignal>
                directResourceSignals =
                monthlyContext
                        .signalsOf(
                                StructureTenGodType
                                        .DIRECT_RESOURCE
                        );

        List<MonthlyLuckContextSignal>
                indirectResourceSignals =
                monthlyContext
                        .signalsOf(
                                StructureTenGodType
                                        .INDIRECT_RESOURCE
                        );

        List<MonthlyLuckContextSignal>
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
                            monthlyContext
                    );

            reasons.add(
                    "정인 관련 Monthly Context Signal은 "
                            + directResourceSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "편인 관련 Monthly Context Signal은 "
                            + indirectResourceSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "Object Identity 중복 제거 후 인성 계열 Monthly Context Signal은 "
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
                    "인성 계열은 학습·이해·정리·흡수·내재화 기능을 해석하기 위한 근거로 사용합니다."
            );

            reasons.add(
                    "인성 계열 월운 Context를 시험 합격·학력 상승·자격 취득 같은 실제 결과로 직접 환산하지 않습니다."
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
                                    monthlyReference
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
         * 식상 계열
         * =================================================
         */

        List<MonthlyLuckContextSignal>
                eatingGodSignals =
                monthlyContext
                        .signalsOf(
                                StructureTenGodType
                                        .EATING_GOD
                        );

        List<MonthlyLuckContextSignal>
                hurtingOfficerSignals =
                monthlyContext
                        .signalsOf(
                                StructureTenGodType
                                        .HURTING_OFFICER
                        );

        List<MonthlyLuckContextSignal>
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
                            monthlyContext
                    );

            reasons.add(
                    "식신 관련 Monthly Context Signal은 "
                            + eatingGodSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "상관 관련 Monthly Context Signal은 "
                            + hurtingOfficerSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "Object Identity 중복 제거 후 식상 계열 Monthly Context Signal은 "
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
                    "식상 계열은 표현·산출·응용·행동·결과물 생성 기능을 해석하기 위한 근거로 사용합니다."
            );

            reasons.add(
                    "식상 계열 월운 Context를 시험 성과·창작 성공·업무 성과 같은 실제 결과로 직접 환산하지 않습니다."
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
                                    monthlyReference
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
         * 인성 + 식상 동시 관여
         * =================================================
         *
         * 배우고 받아들이는 기능과
         * 표현하고 만들어내는 기능이
         * 같은 월운에서 동시에 관여하는지
         * 별도 Match로 보존한다.
         *
         * 어느 한쪽으로 축약하지 않는다.
         */

        if (
                !resourceSignals.isEmpty()
                        && !outputSignals.isEmpty()
        ) {

            List<MonthlyLuckContextSignal>
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
                            monthlyContext
                    );

            reasons.add(
                    "학습·내재화 기능과 표현·산출 기능이 같은 월운에서 함께 관여합니다."
            );

            reasons.add(
                    "인성 계열 Signal은 "
                            + resourceSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "식상 계열 Signal은 "
                            + outputSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "Object Identity 중복 제거 후 두 기능을 합친 Signal은 "
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
                    "받아들이고 정리하는 기능과 밖으로 표현하고 실행하는 기능을 각각 보존하며 하나의 대표 기능으로 축약하지 않습니다."
            );

            reasons.add(
                    "두 기능의 동시 관여를 공부 성과·시험 합격·자격 취득·결과물 성공 같은 실제 사건으로 자동 승격하지 않습니다."
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

    /*
     * =================================================
     * 월운 기본 근거
     * =================================================
     */

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
                "대운과 세운의 학습·성장 Context는 각각 DECADE_CONTEXT와 ANNUAL_CONTEXT Rule에서 별도로 보존합니다."
        );

        return reasons;
    }

    /*
     * =================================================
     * 원 Signal deterministic 근거 보존
     * =================================================
     */

    private void appendSignalReasons(

            List<String> target,

            List<MonthlyLuckContextSignal> signals

    ) {

        for (
                MonthlyLuckContextSignal signal
                : signals
        ) {

            target.add(
                    "Monthly Signal ["
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
                        "Monthly Signal ["
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
            List<MonthlyLuckContextSignal> signals
    ) {

        long supported =
                signals
                        .stream()
                        .filter(
                                MonthlyLuckContextSignal
                                        ::supported
                        )
                        .count();

        long qualified =
                signals
                        .stream()
                        .filter(
                                MonthlyLuckContextSignal
                                        ::qualified
                        )
                        .count();

        long weakened =
                signals
                        .stream()
                        .filter(
                                MonthlyLuckContextSignal
                                        ::weakened
                        )
                        .count();

        long conflicted =
                signals
                        .stream()
                        .filter(
                                MonthlyLuckContextSignal
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
     * 15-7 -> 18차 상태
     * =================================================
     *
     * 상태는 길흉 점수가 아니다.
     *
     * 서로 다른 근거가 동시에 존재하면
     * 삭제하거나 평균 내지 않는다.
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
     * 내부 의미 Summary
     * =================================================
     *
     * 최종 사용자용 문장이 아니다.
     */

    private String summaryOfResource(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 월운에서 배우고 이해하고 정리하는 기능을 지지하는 근거가 존재합니다.";

            case PRESSURE ->
                    "현재 월운에서 배우고 이해하고 정리하는 기능에 부담 또는 약화 근거가 존재합니다.";

            case MIXED ->
                    "현재 월운에서 배우고 이해하고 정리하는 기능에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 월운에서 배우고 이해하고 정리하는 기능이 관여하지만 실제 학습 성과를 단독으로 확정할 수준은 아닙니다.";
        };
    }

    private String summaryOfOutput(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 월운에서 생각을 표현하고 결과물로 만들어내는 기능을 지지하는 근거가 존재합니다.";

            case PRESSURE ->
                    "현재 월운에서 생각을 표현하고 결과물로 만들어내는 기능에 부담 또는 약화 근거가 존재합니다.";

            case MIXED ->
                    "현재 월운에서 표현·실행·산출 기능에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 월운에서 표현·실행·산출 기능이 관여하지만 실제 성과를 단독으로 확정할 수준은 아닙니다.";
        };
    }

    private String summaryOfCombined(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 월운에서 배우고 정리하는 기능과 표현하고 실행하는 기능이 함께 지지되는 근거가 존재합니다.";

            case PRESSURE ->
                    "현재 월운에서 배우고 정리하는 기능과 표현하고 실행하는 기능에 함께 부담 근거가 존재합니다.";

            case MIXED ->
                    "현재 월운에서 준비·학습과 표현·실행 사이에 지원·약화·상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 월운에서 준비·학습과 표현·실행 기능이 함께 관여하지만 실제 결과를 단독으로 확정할 수준은 아닙니다.";
        };
    }

    /*
     * =================================================
     * Object Identity 중복 제거
     * =================================================
     */

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

        for (
                List<MonthlyLuckContextSignal> signals
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
     * Natal Bundle.
     *
     * 1개:
     * 현재 Timeline Segment의 Monthly Context.
     *
     * 2개 이상:
     * 동일 Segment Bundle 구성 오류.
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
                    "LearningMonthlyContextRule에는 최대 하나의 "
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