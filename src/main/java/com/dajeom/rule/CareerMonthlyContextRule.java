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
 * 직업·사회 영역의 월운 Context Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 15-7 MonthlyLuckContextAnalysis
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
 * 직업·사회 영역 의미:
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
 * 매우 중요한 시간층 경계:
 *
 * 이 Rule은:
 *
 * MonthlyLuckContextAnalysis.signalsOf(
 *     StructureTenGodType
 * )
 *
 * 만 사용한다.
 *
 * 즉 현재 월운 전체에서 확정 가능한
 * monthlySignals만 읽는다.
 *
 * =================================================
 *
 * 사용하지 않는 것:
 *
 * allSignals()
 *
 * 이유:
 *
 * allSignals()
 *
 * =
 * monthlySignals
 * +
 * decadeContexts.segmentSignals
 *
 * 이기 때문이다.
 *
 * 대운 시간층은 이미 별도의
 * DECADE_CONTEXT Rule이 담당하므로
 * 월운 Rule에서 다시 소비하지 않는다.
 *
 * =================================================
 *
 * 15차에서 이미 계산된:
 *
 * - 월운 십성
 * - 원국 × 월운 관계
 * - 세운 × 월운 관계
 * - 가능한 경우 대운 × 월운 관계
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
 * 정관 월운
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
 * 칠살 월운
 * -> 이직한다
 *
 * 같은 실제 직업 사건 확정.
 *
 * =================================================
 *
 * MONTHLY_CONTEXT가 없는 Natal Bundle에서는
 * 아무 Match도 생성하지 않는다.
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

        /*
         * =================================================
         * 관살 관련 월운 Signal
         * =================================================
         */

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

        /*
         * 동일 Signal이 양쪽 조회에 동시에
         * 포함될 가능성에 대비하여
         * Object Identity 기준으로 중복 제거.
         */

        List<MonthlyLuckContextSignal>
                officerSignals =
                mergeIdentityUnique(
                        directOfficerSignals,
                        sevenKillingsSignals
                );

        if (
                officerSignals.isEmpty()
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

        reasons.add(
                "정관 관련 Monthly Context Signal은 "
                        + directOfficerSignals.size()
                        + "개입니다."
        );

        reasons.add(
                "칠살 관련 Monthly Context Signal은 "
                        + sevenKillingsSignals.size()
                        + "개입니다."
        );

        reasons.add(
                "Object Identity 중복 제거 후 관살 관련 월운 Signal은 "
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
         * deterministic 원 Signal 근거 보존
         * =================================================
         */

        for (
                MonthlyLuckContextSignal signal
                : officerSignals
        ) {

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
        }

        /*
         * =================================================
         * 대운 Segment Context 경계
         * =================================================
         */

        if (
                !monthlyContext
                        .decadeContextAvailable()
        ) {

            reasons.add(
                    "현재 월운에서는 대운 Segment Context를 사용할 수 없지만 15차 정책에 따라 대운 Sequence와 무관하게 확정 가능한 monthlySignals는 그대로 사용합니다."
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
                "이 Rule은 MonthlyLuckContextAnalysis.monthlySignals만 사용하며 decadeContexts.segmentSignals를 포함하는 allSignals()는 사용하지 않습니다."
        );

        reasons.add(
                "현재 활성 대운의 직업·사회 Context는 별도의 CAREER DECADE_CONTEXT Rule이 담당하므로 월운 Rule에서 중복 집계하지 않습니다."
        );

        reasons.add(
                "15차가 이미 판정한 관살 관련 월운 Context를 직업·사회 생활영역 상태로 변환할 뿐, 십성·용신·격국·관계·합화·다층 관계를 18차에서 다시 계산하지 않습니다."
        );

        reasons.add(
                "정관·칠살 관련 월운 Context를 취업·승진·퇴사·이직·직위 획득·직장 갈등 같은 실제 사건으로 직접 환산하지 않습니다."
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
     * 15-7 -> 18차 상태 변환
     * =================================================
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
                    "현재 월운에서 관살 관련 Context가 직업·사회 역할 기능을 지지하는 방향으로 나타납니다.";

            case PRESSURE ->
                    "현재 월운에서 관살 관련 Context에 역할·책임의 약화 또는 부담 방향이 존재합니다.";

            case MIXED ->
                    "현재 월운의 관살 관련 Context에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 월운에서 관살 관련 Context가 존재하지만 직업·사회 방향을 단독으로 확정할 수준은 아닙니다.";
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